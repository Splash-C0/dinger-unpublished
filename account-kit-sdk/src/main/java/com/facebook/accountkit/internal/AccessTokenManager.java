package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;

import java.util.Date;

final class AccessTokenManager {
  static final String SHARED_PREFERENCES_NAME = "com.facebook.accountkit.AccessTokenManager.SharedPreferences";
  private final AccessTokenCache accessTokenCache;
  private final LocalBroadcastManager localBroadcastManager;
  private AccessToken currentAccessToken;

  AccessTokenManager(Context applicationContext, LocalBroadcastManager localBroadcastManager) {
    this(new AccessTokenCache(applicationContext), localBroadcastManager);
  }

  AccessTokenManager(@NonNull AccessTokenCache accessTokenCache, @NonNull LocalBroadcastManager localBroadcastManager) {
    this.accessTokenCache = accessTokenCache;
    this.localBroadcastManager = localBroadcastManager;
  }

  AccessToken getCurrentAccessToken() {
    return this.currentAccessToken;
  }

  void setCurrentAccessToken(AccessToken currentAccessToken) {
    this.setCurrentAccessToken(currentAccessToken, true);
  }

  boolean loadCurrentAccessToken() {
    AccessToken accessToken = this.accessTokenCache.load();
    if (accessToken != null) {
      this.setCurrentAccessToken(accessToken, false);
      return true;
    } else {
      return false;
    }
  }

  void refreshCurrentAccessToken(AccessToken currentAccessToken) {
    AccessToken newAccessToken = new AccessToken(currentAccessToken.getToken(), currentAccessToken.getAccountId(), currentAccessToken.getApplicationId(), currentAccessToken.getTokenRefreshIntervalSeconds(), (Date) null);
    this.setCurrentAccessToken(newAccessToken);
  }

  private void setCurrentAccessToken(AccessToken currentAccessToken, boolean saveToCache) {
    AccessToken oldAccessToken = this.currentAccessToken;
    this.currentAccessToken = currentAccessToken;
    if (saveToCache) {
      if (currentAccessToken != null) {
        this.accessTokenCache.save(currentAccessToken);
      } else {
        this.accessTokenCache.clear();
      }
    }

    if (!Utility.areObjectsEqual(oldAccessToken, currentAccessToken)) {
      this.sendCurrentAccessTokenChangedBroadcast(oldAccessToken, currentAccessToken);
    }

  }

  private void sendCurrentAccessTokenChangedBroadcast(AccessToken oldAccessToken, AccessToken currentAccessToken) {
    Intent intent = new Intent("com.facebook.accountkit.sdk.ACTION_CURRENT_ACCESS_TOKEN_CHANGED");
    intent.putExtra("com.facebook.accountkit.sdk.EXTRA_OLD_ACCESS_TOKEN", oldAccessToken);
    intent.putExtra("com.facebook.accountkit.sdk.EXTRA_NEW_ACCESS_TOKEN", currentAccessToken);
    this.localBroadcastManager.sendBroadcast(intent);
  }
}
