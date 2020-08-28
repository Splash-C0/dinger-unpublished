package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

final class AccessTokenCache {
  static final String CACHED_ACCESS_TOKEN_KEY = "com.facebook.accountkit.AccessTokenManager.CachedAccessToken";
  private static final String ACCESS_TOKEN_ACCOUNT_ID_KEY = "account_id";
  private static final String ACCESS_TOKEN_APPLICATION_ID_KEY = "application_id";
  private static final String ACCESS_TOKEN_LAST_REFRESH_KEY = "last_refresh";
  private static final String ACCESS_TOKEN_REFRESH_INTERVAL_KEY = "tokenRefreshIntervalInSeconds";
  private static final String ACCESS_TOKEN_TOKEN_KEY = "token";
  private static final String ACCESS_TOKEN_VERSION_KEY = "version";
  private static final int ACCESS_TOKEN_VERSION_VALUE = 1;
  private final SharedPreferences sharedPreferences;

  @VisibleForTesting
  AccessTokenCache(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  AccessTokenCache(Context applicationContext) {
    this(applicationContext.getSharedPreferences("com.facebook.accountkit.AccessTokenManager.SharedPreferences", 0));
  }

  @VisibleForTesting
  static AccessToken deserializeAccessToken(JSONObject jsonObject) throws JSONException {
    if (jsonObject.getInt("version") > 1) {
      throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.INVALID_ACCESS_TOKEN_FORMAT);
    } else {
      return new AccessToken(jsonObject.getString("token"), jsonObject.getString("account_id"), jsonObject.getString("application_id"), jsonObject.getLong("tokenRefreshIntervalInSeconds"), new Date(jsonObject.getLong("last_refresh")));
    }
  }

  @VisibleForTesting
  static JSONObject serializeAccessToken(AccessToken accessToken) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("version", 1);
    jsonObject.put("account_id", accessToken.getAccountId());
    jsonObject.put("application_id", accessToken.getApplicationId());
    jsonObject.put("tokenRefreshIntervalInSeconds", accessToken.getTokenRefreshIntervalSeconds());
    jsonObject.put("last_refresh", accessToken.getLastRefresh().getTime());
    jsonObject.put("token", accessToken.getToken());
    return jsonObject;
  }

  public void clear() {
    this.sharedPreferences.edit().remove("com.facebook.accountkit.AccessTokenManager.CachedAccessToken").apply();
  }

  public AccessToken load() {
    String jsonString = this.sharedPreferences.getString("com.facebook.accountkit.AccessTokenManager.CachedAccessToken", (String) null);
    if (jsonString != null) {
      try {
        return deserializeAccessToken(new JSONObject(jsonString));
      } catch (JSONException var3) {
        return null;
      }
    } else {
      return null;
    }
  }

  public void save(@NonNull AccessToken accessToken) {
    try {
      JSONObject jsonObject = serializeAccessToken(accessToken);
      this.sharedPreferences.edit().putString("com.facebook.accountkit.AccessTokenManager.CachedAccessToken", jsonObject.toString()).apply();
    } catch (JSONException var4) {
    }

  }
}
