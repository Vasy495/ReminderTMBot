package com.tinkov.remindertmbot.repository;

import com.tinkov.remindertmbot.entity.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

}
