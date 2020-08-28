package com.facebook.accountkit.ui;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;

class AccountKitLoginResultImpl implements AccountKitLoginResult {
  public static final Creator<AccountKitLoginResultImpl> CREATOR = new Creator<AccountKitLoginResultImpl>() {
    public AccountKitLoginResultImpl createFromParcel(Parcel source) {
      return new AccountKitLoginResultImpl(source);
    }

    public AccountKitLoginResultImpl[] newArray(int size) {
      return new AccountKitLoginResultImpl[size];
    }
  };
  private final AccessToken accessToken;
  private final String authorizationCode;
  private final boolean cancelled;
  private final AccountKitError error;
  private final String finalAuthorizationState;
  private final long tokenRefreshIntervalInSeconds;

  public AccountKitLoginResultImpl(AccessToken accessToken, String authorizationCode, String finalAuthorizationState, long tokenRefreshIntervalInSeconds, AccountKitError error, boolean cancelled) {
    this.accessToken = accessToken;
    this.authorizationCode = authorizationCode;
    this.tokenRefreshIntervalInSeconds = tokenRefreshIntervalInSeconds;
    this.cancelled = cancelled;
    this.error = error;
    this.finalAuthorizationState = finalAuthorizationState;
  }

  private AccountKitLoginResultImpl(Parcel parcel) {
    this.accessToken = (AccessToken) parcel.readParcelable(AccessToken.class.getClassLoader());
    this.authorizationCode = parcel.readString();
    this.finalAuthorizationState = parcel.readString();
    this.tokenRefreshIntervalInSeconds = parcel.readLong();
    this.error = (AccountKitError) parcel.readParcelable(AccountKitError.class.getClassLoader());
    this.cancelled = parcel.readByte() == 1;
  }

  @Nullable
  public AccessToken getAccessToken() {
    return this.accessToken;
  }

  @Nullable
  public String getFinalAuthorizationState() {
    return this.finalAuthorizationState;
  }

  @Nullable
  public String getAuthorizationCode() {
    return this.authorizationCode;
  }

  @Nullable
  public AccountKitError getError() {
    return this.error;
  }

  public boolean wasCancelled() {
    return this.error == null && this.authorizationCode == null && this.accessToken == null;
  }

  public long getTokenRefreshIntervalInSeconds() {
    return this.tokenRefreshIntervalInSeconds;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.accessToken, flags);
    dest.writeString(this.authorizationCode);
    dest.writeString(this.finalAuthorizationState);
    dest.writeLong(this.tokenRefreshIntervalInSeconds);
    dest.writeParcelable(this.error, flags);
    dest.writeByte((byte) (this.cancelled ? 1 : 0));
  }
}
