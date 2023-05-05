package com.tinkov.remindertmbot.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.tinkov.remindertmbot.repository.NotificationTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTaskTimer {

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;

    public NotificationTaskTimer(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    //fixedRate - 21:01:10, 21:02:10, 21:03:10 без смещения из-за выполнения
    public void task() {
        notificationTaskRepository.findAllByNotificationDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) //Обрезаем секунды до минуты
        ).forEach(notificationTask -> {
            telegramBot.execute(new SendMessage(
                    notificationTask.getChatId(), "Вы просили напомнить о задаче: " + notificationTask.getMessage()));
            notificationTaskRepository.delete(notificationTask);
        });
    }

}
