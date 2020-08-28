package com.facebook.accountkit.internal;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.LoginModel;

import java.util.HashMap;
import java.util.Map;

abstract class LoginModelImpl implements LoginModel {
  private static final int PARCEL_VERSION = 2;
  protected Map<String, String> fields;
  private AccessToken accessToken;
  private String code;
  private AccountKitError error;
  private long expiresInSeconds;
  private String initialAuthState;
  private String finalAuthState;
  private String loginModelCode;
  private String responseType;
  private LoginStatus status;

  LoginModelImpl(String responseType) {
    this.status = LoginStatus.EMPTY;
    this.fields = new HashMap();
    this.responseType = responseType;
  }

  LoginModelImpl(Parcel parcel) {
    this.status = LoginStatus.EMPTY;
    this.fields = new HashMap();
    int version = parcel.readInt();
    if (version == 2) {
      this.error = (AccountKitError) parcel.readParcelable(AccountKitError.class.getClassLoader());
      this.expiresInSeconds = parcel.readLong();
      this.loginModelCode = parcel.readString();
      this.status = LoginStatus.valueOf(parcel.readString());
      this.responseType = parcel.readString();
      this.finalAuthState = parcel.readString();
      this.code = parcel.readString();
    } else {
      this.error = new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED);
      this.status = LoginStatus.ERROR;
    }

  }

  public String getCode() {
    return this.code;
  }

  void setCode(String code) {
    this.code = code;
  }

  public AccountKitError getError() {
    return this.error;
  }

  void setError(AccountKitError error) {
    this.error = error;
  }

  String getLoginRequestCode() {
    return this.loginModelCode;
  }

  void setLoginCode(String loginModelCode) {
    this.loginModelCode = loginModelCode;
  }

  long getExpiresInSeconds() {
    return this.expiresInSeconds;
  }

  void setExpiresInSeconds(long expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
  }

  public LoginStatus getStatus() {
    return this.status;
  }

  void setStatus(LoginStatus status) {
    this.status = status;
  }

  public String getResponseType() {
    return this.responseType;
  }

  public String getInitialAuthState() {
    return this.initialAuthState;
  }

  void setInitialAuthState(String initialAuthState) {
    this.initialAuthState = initialAuthState;
  }

  public String getFinalAuthState() {
    return this.finalAuthState;
  }

  void setFinalAuthState(String finalAuthState) {
    this.finalAuthState = finalAuthState;
  }

  @Nullable
  public AccessToken getAccessToken() {
    return this.accessToken;
  }

  void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  void putField(String key, String value) {
    this.fields.put(key, value);
  }

  @Nullable
  public String getPrivacyPolicy() {
    return (String) this.fields.get("privacy_policy");
  }

  @Nullable
  public String getTermsOfService() {
    return (String) this.fields.get("terms_of_service");
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof LoginModelImpl)) {
      return false;
    } else {
      LoginModelImpl o = (LoginModelImpl) other;
      return this.expiresInSeconds == o.expiresInSeconds && Utility.areObjectsEqual(this.error, o.error) && Utility.areObjectsEqual(this.loginModelCode, o.loginModelCode) && Utility.areObjectsEqual(this.status, o.status) && Utility.areObjectsEqual(this.responseType, o.responseType) && Utility.areObjectsEqual(this.finalAuthState, o.finalAuthState) && Utility.areObjectsEqual(this.code, o.code);
    }
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(2);
    dest.writeParcelable(this.error, flags);
    dest.writeLong(this.expiresInSeconds);
    dest.writeString(this.loginModelCode);
    dest.writeString(this.status.name());
    dest.writeString(this.responseType);
    dest.writeString(this.finalAuthState);
    dest.writeString(this.code);
  }
}
