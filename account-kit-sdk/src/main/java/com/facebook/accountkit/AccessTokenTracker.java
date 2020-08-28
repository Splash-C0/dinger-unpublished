package com.facebook.accountkit;

import android.content.Intent;

import java.util.Collections;
import java.util.List;

public abstract class AccessTokenTracker extends Tracker {
  public static final String ACTION_CURRENT_ACCESS_TOKEN_CHANGED = "com.facebook.accountkit.sdk.ACTION_CURRENT_ACCESS_TOKEN_CHANGED";
  public static final String EXTRA_NEW_ACCESS_TOKEN = "com.facebook.accountkit.sdk.EXTRA_NEW_ACCESS_TOKEN";
  public static final String EXTRA_OLD_ACCESS_TOKEN = "com.facebook.accountkit.sdk.EXTRA_OLD_ACCESS_TOKEN";

  public AccessTokenTracker() {
    this.startTracking();
  }

  protected abstract void onCurrentAccessTokenChanged(AccessToken var1, AccessToken var2);

  protected List<String> getActionsStateChanged() {
    return Collections.singletonList("com.facebook.accountkit.sdk.ACTION_CURRENT_ACCESS_TOKEN_CHANGED");
  }

  protected void onReceive(Intent intent) {
    AccessToken oldAccessToken = (AccessToken) intent.getParcelableExtra("com.facebook.accountkit.sdk.EXTRA_OLD_ACCESS_TOKEN");
    AccessToken newAccessToken = (AccessToken) intent.getParcelableExtra("com.facebook.accountkit.sdk.EXTRA_NEW_ACCESS_TOKEN");
    this.onCurrentAccessTokenChanged(oldAccessToken, newAccessToken);
  }
}
