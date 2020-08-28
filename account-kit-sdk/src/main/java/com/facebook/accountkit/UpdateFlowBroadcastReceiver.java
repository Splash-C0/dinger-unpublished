package com.facebook.accountkit;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public abstract class UpdateFlowBroadcastReceiver extends BroadcastReceiver {
  public static final String ACTION_UPDATE;
  public static final String EXTRA_EVENT;
  public static final String EXTRA_PHONE_NUMBER;
  public static final String EXTRA_ERROR_MESSAGE;
  public static final String EXTRA_CONFIRMATION_CODE;
  public static final String EXTRA_UPDATE_STATE;
  private static final String TAG = UpdateFlowBroadcastReceiver.class.getSimpleName();

  static {
    ACTION_UPDATE = TAG + ".action_update";
    EXTRA_EVENT = TAG + ".extra_event";
    EXTRA_PHONE_NUMBER = TAG + ".extra_phoneNumber";
    EXTRA_ERROR_MESSAGE = TAG + ".extra_error_message";
    EXTRA_CONFIRMATION_CODE = TAG + ".extra_confirmationCode";
    EXTRA_UPDATE_STATE = TAG + ".extra_updateState";
  }

  public UpdateFlowBroadcastReceiver() {
  }

  public static IntentFilter getIntentFilter() {
    return new IntentFilter(ACTION_UPDATE);
  }

  public static enum Event {
    UPDATE_START,
    SENT_CODE,
    SENT_CODE_COMPLETE,
    ERROR_UPDATE,
    ERROR_CONFIRMATION_CODE,
    RETRY_CONFIRMATION_CODE,
    CONFIRMATION_CODE_COMPLETE,
    ACCOUNT_UPDATE_COMPLETE,
    RETRY;

    private Event() {
    }
  }
}
