package com.tinkov.remindertmbot.repository;

import com.tinkov.remindertmbot.entity.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findAllByNotificationDateTime(LocalDateTime localDateTime); //Запрос вытаскивает все записи по времени (возможно несколько записей на одно время). Spring сам формирует запрос из названия метода, при этом мы не пишем sql запрос

    List<NotificationTask> findAllByNotificationDateTimeAndChatId(LocalDateTime localDateTime, long chatId);

}
