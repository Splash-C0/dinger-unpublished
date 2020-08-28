package com.facebook.accountkit.ui;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public abstract class LoginFlowBroadcastReceiver extends BroadcastReceiver {
  public static final String ACTION_UPDATE;
  public static final String EXTRA_EVENT;
  public static final String EXTRA_EMAIL;
  public static final String EXTRA_CONFIRMATION_CODE;
  public static final String EXTRA_NOTIFICATION_CHANNEL;
  public static final String EXTRA_PHONE_NUMBER;
  public static final String EXTRA_RETURN_LOGIN_FLOW_STATE;
  public static final String EXTRA_OTP_LENGTH;
  private static final String TAG = LoginFlowBroadcastReceiver.class.getSimpleName();

  static {
    ACTION_UPDATE = TAG + ".action_update";
    EXTRA_EVENT = TAG + ".extra_event";
    EXTRA_EMAIL = TAG + ".extra_email";
    EXTRA_CONFIRMATION_CODE = TAG + ".extra_confirmationCode";
    EXTRA_NOTIFICATION_CHANNEL = TAG + ".extra_notificationChannel";
    EXTRA_PHONE_NUMBER = TAG + ".extra_phoneNumber";
    EXTRA_RETURN_LOGIN_FLOW_STATE = TAG + ".EXTRA_RETURN_LOGIN_FLOW_STATE";
    EXTRA_OTP_LENGTH = TAG + ".EXTRA_OTP_LENGTH";
  }

  LoginFlowBroadcastReceiver() {
  }

  public static IntentFilter getIntentFilter() {
    return new IntentFilter(ACTION_UPDATE);
  }

  public static enum Event {
    SENT_CODE_COMPLETE,
    ACCOUNT_VERIFIED_COMPLETE,
    CONFIRM_SEAMLESS_LOGIN,
    EMAIL_LOGIN_COMPLETE,
    EMAIL_VERIFY_RETRY,
    ERROR_RESTART,
    PHONE_LOGIN_COMPLETE,
    PHONE_CONFIRMATION_CODE_COMPLETE,
    PHONE_CONFIRMATION_CODE_RETRY,
    PHONE_RESEND,
    PHONE_RESEND_FACEBOOK_NOTIFICATION,
    PHONE_RESEND_SWITCH;

    private Event() {
    }
  }
}
