package com.facebook.accountkit.internal;

public enum Feature {
  EMAIL_ENABLED(0, 1),
  PHONE_NUMBER_ENABLED(1, 1),
  CALLBACK_BUTTON_ALTERNATE_TEXT(2, 1);

  private int prefKey;
  private int defaultValue;

  private Feature(int prefKey, int defaultValue) {
    this.prefKey = prefKey;
    this.defaultValue = defaultValue;
  }

  int getPrefKey() {
    return this.prefKey;
  }

  int getDefaultValue() {
    return this.defaultValue;
  }
}
