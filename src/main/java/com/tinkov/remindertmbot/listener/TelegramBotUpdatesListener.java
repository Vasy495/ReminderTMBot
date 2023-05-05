package com.tinkov.remindertmbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tinkov.remindertmbot.entity.NotificationTask;
import com.tinkov.remindertmbot.service.NotificationTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final Pattern pattern = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}:\\d{2})\\s+([А-я\\d\\s.,!?:]+)"  //Регулярное выражение для сравнения разделенное на 2 группы ()()
    );

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);  //через telegramBot регестрируем listener
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.stream()
                    .filter(update -> update.message() != null)  //Такая реализация не реагирует на изменение отправленных сообщений в телеграме
                    .forEach(update -> {
                        logger.info("Handler update: {}", update);
                        //Обработка update:
                        Message message = update.message();
                        Long chatId = message.chat().id(); //message.from().id() - id пользователя
                        String text = message.text();

                        if ("/start".equals(text)) {
                            sendMessage(chatId, """
                                    Привет! 
                                    Я помогу тебе запланировать задачу. Отправь ее в формате: 01.05.2023 20:00 Сдать домашку
                                    """);
                        } else if (text != null) {
                            Matcher matcher = pattern.matcher(text);
                            if (matcher.find()) { //Проверяем находит ли паттерн в тексте
                                LocalDateTime dateTime = parse(matcher.group(1)); //Переводим сразу в нужный формате LocalDateTime  с помощью вспомогательного метода
                                if (Objects.isNull(dateTime)) { //Проверяем дату на null
                                    sendMessage(chatId, "Некорректный фомат даты и/или времени!");
                                } else {
                                    String txt = matcher.group(2);
                                    NotificationTask notificationTask = new NotificationTask();
                                    notificationTask.setChatId(chatId);
                                    notificationTask.setMessage(txt);
                                    notificationTask.setNotificationDateTime(dateTime);
                                    notificationTaskService.save(notificationTask);
                                    sendMessage(chatId, "Задача успешно запланирована!");
                                }
                            } else {
                                sendMessage(chatId, "Некорректный фомат сообщения!");
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    //Переводим полученную дату и время из сообщения в формат LocalDateTime
    @Nullable //Для подсвечивания потенциальных NPE
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, dateTimeFormatter); // parse может вернуть ошибку если дата неправильная, например 25:61
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message); //sendMessage.parseMode(ParseMod) - можно менять оформление разными способами
        SendResponse sendResponse = telegramBot.execute(sendMessage); // execute() - отправка сообщения
        if (!sendResponse.isOk()) { //Проверяем успешность отправки сообщения
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

}
