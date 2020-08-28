package com.facebook.accountkit.ui;

import androidx.annotation.StyleRes;

public interface UIManager extends UIManagerStub {
  @StyleRes
  int getThemeId();

  void setThemeId(@StyleRes int var1);

  void setUIManagerListener(UIManager.UIManagerListener var1);

  public interface UIManagerListener {
    void onBack();

    void onCancel();
  }
}
