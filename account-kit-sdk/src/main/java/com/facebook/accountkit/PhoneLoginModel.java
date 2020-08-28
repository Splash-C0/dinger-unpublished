package com.facebook.accountkit;

import com.facebook.accountkit.ui.NotificationChannel;

public interface PhoneLoginModel extends LoginModel {
  String getConfirmationCode();

  PhoneNumber getPhoneNumber();

  NotificationChannel getNotificationChannel();

  long getResendTime();
}
