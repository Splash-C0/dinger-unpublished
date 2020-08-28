package com.facebook.accountkit.ui;

import com.facebook.accountkit.custom.R.string;

public enum ButtonType {
  BEGIN(string.com_accountkit_button_begin),
  CONFIRM(string.com_accountkit_button_confirm),
  CONTINUE(string.com_accountkit_button_continue),
  LOG_IN(string.com_accountkit_button_log_in),
  NEXT(string.com_accountkit_button_next),
  USE_SMS(string.com_accountkit_button_use_sms),
  OK(string.com_accountkit_button_ok),
  SEND(string.com_accountkit_button_send),
  START(string.com_accountkit_button_start),
  SUBMIT(string.com_accountkit_button_submit);

  private final int value;

  private ButtonType(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }
}
