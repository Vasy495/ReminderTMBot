package com.tinkov.remindertmbot.service;

import com.tinkov.remindertmbot.entity.NotificationTask;
import com.tinkov.remindertmbot.repository.NotificationTaskRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void save(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }
}
