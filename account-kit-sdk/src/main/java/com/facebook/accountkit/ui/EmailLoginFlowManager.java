package com.facebook.accountkit.ui;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.internal.AccountKitController;

class EmailLoginFlowManager extends LoginFlowManager {
  public static final Creator<EmailLoginFlowManager> CREATOR = new Creator<EmailLoginFlowManager>() {
    public EmailLoginFlowManager createFromParcel(Parcel source) {
      return new EmailLoginFlowManager(source);
    }

    public EmailLoginFlowManager[] newArray(int size) {
      return new EmailLoginFlowManager[size];
    }
  };
  private String email;

  public EmailLoginFlowManager(AccountKitConfiguration configuration) {
    super(LoginType.EMAIL);
    this.activityHandler = new ActivityEmailHandler(configuration);
  }

  protected EmailLoginFlowManager(Parcel parcel) {
    super(parcel);
    this.activityHandler = (ActivityHandler) parcel.readParcelable(ActivityEmailHandler.class.getClassLoader());
    this.email = parcel.readString();
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void logInWithEmail(AccountKitActivity.ResponseType responseType, @Nullable String initialAuthState) {
    if (this.isValid() && this.email != null) {
      AccountKitController.logInWithEmail(this.email, responseType.getValue(), initialAuthState);
    }
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.activityHandler, flags);
    dest.writeString(this.email);
  }
}
