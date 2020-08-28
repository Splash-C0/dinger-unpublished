package com.facebook.accountkit.ui;

public enum NotificationChannel {
  SMS("sms"),
  FACEBOOK("facebook"),
  WHATSAPP("whatsapp");

  private final String notificationChannel;

  private NotificationChannel(String notificationChannel) {
    this.notificationChannel = notificationChannel;
  }

  public String toString() {
    return this.notificationChannel;
  }
}
