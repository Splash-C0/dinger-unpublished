package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Intent;

public abstract class ContentControllerBase implements ContentController {
  protected final AccountKitConfiguration configuration;

  ContentControllerBase(AccountKitConfiguration configuration) {
    this.configuration = configuration;
  }

  public void onPause(Activity activity) {
    ViewUtility.hideKeyboard(activity);
  }

  public void onResume(Activity activity) {
    this.logImpression();
  }

  public boolean isTransient() {
    return true;
  }

  protected abstract void logImpression();

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }
}
