package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NotificationsResponse extends CommonResponse {

    @SerializedName("notificationList")
    private List<NotificationItem> mNotificationList = new ArrayList<>();

    public List<NotificationItem> getNotificationList() {
        return mNotificationList;
    }

    public static class NotificationItem {

        @SerializedName("notificationImage")
        private String notificationImage = "";
        @SerializedName("notificationTitle")
        private String notificationTitle = "";
        @SerializedName("notificationDesc")
        private String notificationDesc = "";
        @SerializedName("notificationTime")
        private String notificationTime = "";

        public String getNotificationImage() {
            return notificationImage;
        }

        public String getNotificationTitle() {
            return notificationTitle;
        }

        public String getNotificationDesc() {
            return notificationDesc;
        }

        public String getNotificationTime() {
            return notificationTime;
        }
    }
}
