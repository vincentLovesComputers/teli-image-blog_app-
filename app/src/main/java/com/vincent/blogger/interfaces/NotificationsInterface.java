package com.vincent.blogger.interfaces;

import com.vincent.blogger.models.Notification;

public interface NotificationsInterface {
    void getNotifications(Notification notification);
    void getCountOfLikes(int count);

}
