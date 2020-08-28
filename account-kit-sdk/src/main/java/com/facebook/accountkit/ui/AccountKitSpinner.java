package com.facebook.accountkit.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

public class AccountKitSpinner extends AppCompatSpinner {
  @Nullable
  private AccountKitSpinner.OnSpinnerEventsListener listener;
  private boolean openStarted = false;

  public AccountKitSpinner(Context context) {
    super(context);
  }

  public AccountKitSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AccountKitSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public boolean performClick() {
    this.openStarted = true;
    if (this.listener != null) {
      this.listener.onSpinnerOpened();
    }

    return super.performClick();
  }

  public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);
    if (this.openStarted && hasWindowFocus) {
      this.performClosedEvent();
    }

  }

  void setOnSpinnerEventsListener(AccountKitSpinner.OnSpinnerEventsListener onSpinnerEventsListener) {
    this.listener = onSpinnerEventsListener;
  }

  private void performClosedEvent() {
    this.openStarted = false;
    if (this.listener != null) {
      this.listener.onSpinnerClosed();
    }

  }

  public interface OnSpinnerEventsListener {
    void onSpinnerOpened();

    void onSpinnerClosed();
  }
}
