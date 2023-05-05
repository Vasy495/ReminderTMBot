package com.tinkov.remindertmbot.repository;

import com.tinkov.remindertmbot.entity.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findAllByNotificationDateTime(LocalDateTime localDateTime); //Запрос вытаскивает все записи по времени (возможно несколько записей на одно время). Spring сам формирует запрос из названия метода, при этом мы не пишем sql запрос

    List<NotificationTask> findAllByNotificationDateTimeAndChatId(LocalDateTime localDateTime, long chatId);

    List<NotificationTask> findAllByUser_NameLike(String nameLike); //Вытаскиваем все сообщения по имени пользователя (пример join)

    @Query("SELECT nt FROM NotificationTask nt WHERE nt.user.name like %:nameLike%") //Используем название классов и полей, а не таблицы как в SQL
//    @Query("SELECT nt.* FROM notification_task nt INNER JOIN user u ON nt.user_id = u.id WHERE u.name like %:nameLike%", nativeQuery = true) //Пример нативного запроса SQL
    List<NotificationTask> findAllByNameLike(@Param("nameLike") String nameLike); //Вытаскиваем все сообщения по имени пользователя (пример для сложных запросов через аннотацию Query). Нет привязки к названию метода

    @Modifying //Если запрос не select, то обязательно наличие этой аннотации (для update, delete, insert запросов)
    @Query("DELETE FROM NotificationTask WHERE message like %:nameLike%")
    void removeAllLike(@Param("nameLike") String nameLike);

//    Больше информации по запросам в документации: docs.spring.io (5.1.3 Query Methods (jpql))
}
