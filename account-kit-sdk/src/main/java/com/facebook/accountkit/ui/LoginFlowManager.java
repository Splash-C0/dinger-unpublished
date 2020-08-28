package com.facebook.accountkit.ui;

import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.internal.AccountKitController;

abstract class LoginFlowManager implements Parcelable {
  private final LoginType loginType;
  protected ActivityHandler activityHandler;
  private boolean isValid = true;
  private LoginFlowState flowState;

  public LoginFlowManager(LoginType loginType) {
    this.loginType = loginType;
    this.flowState = LoginFlowState.NONE;
  }

  protected LoginFlowManager(Parcel parcel) {
    this.isValid = parcel.readByte() == 1;
    this.loginType = LoginType.valueOf(parcel.readString());
    this.flowState = LoginFlowState.values()[parcel.readInt()];
  }

  public void cancel() {
    this.isValid = false;
    AccountKit.cancelLogin();
  }

  public AccessToken getAccessToken() {
    return !this.isValid ? null : AccountKit.getCurrentAccessToken();
  }

  public boolean isValid() {
    return this.isValid;
  }

  public LoginType getLoginType() {
    return this.loginType;
  }

  public LoginFlowState getFlowState() {
    return this.flowState;
  }

  public final void setFlowState(LoginFlowState newState) {
    this.flowState = newState;
  }

  void confirmSeamlessLogin() {
    if (this.isValid()) {
      AccountKitController.continueSeamlessLogin();
    }
  }

  public ActivityHandler getActivityHandler() {
    return this.activityHandler;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte((byte) (this.isValid ? 1 : 0));
    dest.writeString(this.loginType.name());
    dest.writeInt(this.flowState.ordinal());
  }
}
