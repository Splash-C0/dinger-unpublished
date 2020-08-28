package com.facebook.accountkit.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputLayout;

public class ClearBackgroundTextInputLayout extends TextInputLayout {
  public ClearBackgroundTextInputLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setError(CharSequence error) {
    super.setError(error);
    this.clearBackground();
  }

  public void drawableStateChanged() {
    super.drawableStateChanged();
    this.clearBackground();
  }

  private void clearBackground() {
    if (this.getEditText() != null) {
      this.getEditText().getBackground().clearColorFilter();
    }

  }
}
