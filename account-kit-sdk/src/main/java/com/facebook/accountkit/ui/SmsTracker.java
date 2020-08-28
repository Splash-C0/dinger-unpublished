package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.facebook.accountkit.Tracker;
import com.facebook.accountkit.internal.Utility;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SmsTracker extends Tracker {
  public static final String AK_SMS_INTENT = "com.facebook.accountkit.SMS_RECIEVED";
  public static final int AK_SMS_INTENT_REQUEST_CODE = 42;
  static final String SMS_INTENT = "android.provider.Telephony.SMS_RECEIVED";
  private static final Pattern ACCOUNT_KIT_PATTERN = Pattern.compile("(\\d{6})(?=.*\\bAccount Kit\\b)(?=.*\\bFacebook\\b)");

  public SmsTracker() {
    this.startTracking();
  }

  static String getCodeFromString(String body) {
    Matcher matcher = ACCOUNT_KIT_PATTERN.matcher(body);
    return matcher.find() ? matcher.group(1) : null;
  }

  static boolean canTrackSms(Context context) {
    return Utility.hasGooglePlayServices(context);
  }

  protected abstract void confirmationCodeReceived(String var1);

  protected List<String> getActionsStateChanged() {
    return Arrays.asList("android.provider.Telephony.SMS_RECEIVED", "com.facebook.accountkit.SMS_RECIEVED", "com.google.android.gms.auth.api.phone.SMS_RETRIEVED");
  }

  protected boolean isLocal() {
    return false;
  }

  protected void onReceive(Intent intent) {
    String smsText = null;
    if ("com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      Status status = (Status) extras.get("com.google.android.gms.auth.api.phone.EXTRA_STATUS");
      if (status != null) {
        switch (status.getStatusCode()) {
          case 0:
            smsText = (String) extras.get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
        }
      }
    } else {
      Object[] objects = (Object[]) ((Object[]) intent.getSerializableExtra("pdus"));
      StringBuilder message = new StringBuilder();
      Object[] var5 = objects;
      int var6 = objects.length;

      for (int var7 = 0; var7 < var6; ++var7) {
        Object o = var5[var7];
        message.append(SmsMessage.createFromPdu((byte[]) ((byte[]) o)).getDisplayMessageBody());
      }

      smsText = message.toString();
    }

    if (smsText != null) {
      String confirmationCode = getCodeFromString(smsText);
      if (confirmationCode != null) {
        this.confirmationCodeReceived(confirmationCode);
      }
    }

  }
}
