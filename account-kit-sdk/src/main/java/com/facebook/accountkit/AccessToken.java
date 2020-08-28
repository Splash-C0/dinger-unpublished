package com.facebook.accountkit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.internal.Utility;

import java.util.Date;

public final class AccessToken implements Parcelable {
  public static final Creator<AccessToken> CREATOR = new Creator<AccessToken>() {
    public AccessToken createFromParcel(Parcel source) {
      return new AccessToken(source);
    }

    public AccessToken[] newArray(int size) {
      return new AccessToken[size];
    }
  };
  private static final long DEFAULT_TOKEN_REFRESH_INTERVAL = 604800L;
  private static final int PARCEL_VERSION = 2;
  private final String accountId;
  private final String applicationId;
  private final Date lastRefresh;
  private final String token;
  private final long tokenRefreshIntervalInSeconds;

  public AccessToken(@NonNull String token, @NonNull String accountId, @NonNull String applicationId, long tokenRefreshIntervalInSeconds, @Nullable Date lastRefreshTime) {
    this.token = token;
    this.accountId = accountId;
    this.applicationId = applicationId;
    this.tokenRefreshIntervalInSeconds = tokenRefreshIntervalInSeconds;
    this.lastRefresh = lastRefreshTime != null ? lastRefreshTime : new Date();
  }

  private AccessToken(Parcel parcel) {
    int version = 1;

    try {
      version = parcel.readInt();
    } catch (ClassCastException var6) {
    }

    String toSetToken;
    try {
      toSetToken = parcel.readString();
    } catch (ClassCastException var5) {
      parcel.readLong();
      toSetToken = parcel.readString();
    }

    this.token = toSetToken;
    this.accountId = parcel.readString();
    this.lastRefresh = new Date(parcel.readLong());
    this.applicationId = parcel.readString();
    if (version == 2) {
      this.tokenRefreshIntervalInSeconds = parcel.readLong();
    } else {
      this.tokenRefreshIntervalInSeconds = 604800L;
    }

  }

  public String getAccountId() {
    return this.accountId;
  }

  public String getApplicationId() {
    return this.applicationId;
  }

  public Date getLastRefresh() {
    return this.lastRefresh;
  }

  public String getToken() {
    return this.token;
  }

  public long getTokenRefreshIntervalSeconds() {
    return this.tokenRefreshIntervalInSeconds;
  }

  public String toString() {
    return "{AccessToken token:" + this.tokenToString() + " accountId:" + this.accountId + "}";
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof AccessToken)) {
      return false;
    } else {
      AccessToken o = (AccessToken) other;
      return this.tokenRefreshIntervalInSeconds == o.tokenRefreshIntervalInSeconds && Utility.areObjectsEqual(this.accountId, o.accountId) && Utility.areObjectsEqual(this.applicationId, o.applicationId) && Utility.areObjectsEqual(this.lastRefresh, o.lastRefresh) && Utility.areObjectsEqual(this.token, o.token);
    }
  }

  public int hashCode() {
    int result = 17;
    result = result * 31 + Utility.getHashCode(this.accountId);
    result = result * 31 + Utility.getHashCode(this.applicationId);
    result = result * 31 + Utility.getHashCode(this.lastRefresh);
    result = result * 31 + Utility.getHashCode(this.token);
    result = result * 31 + Utility.getHashCode(this.tokenRefreshIntervalInSeconds);
    return result;
  }

  private String tokenToString() {
    if (this.token == null) {
      return "null";
    } else {
      return AccountKit.getLoggingBehaviors().isEnabled(LoggingBehavior.INCLUDE_ACCESS_TOKENS) ? this.token : "ACCESS_TOKEN_REMOVED";
    }
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(2);
    dest.writeString(this.token);
    dest.writeString(this.accountId);
    dest.writeLong(this.lastRefresh.getTime());
    dest.writeString(this.applicationId);
    dest.writeLong(this.tokenRefreshIntervalInSeconds);
  }
}
