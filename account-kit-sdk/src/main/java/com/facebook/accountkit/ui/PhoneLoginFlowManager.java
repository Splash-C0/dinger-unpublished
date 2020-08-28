package com.facebook.accountkit.ui;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.PhoneNumber;

/** Emptied class to avoid triggering actual AccountKit flow **/
class PhoneLoginFlowManager extends LoginFlowManager {
  public static final Creator<PhoneLoginFlowManager> CREATOR = new Creator<PhoneLoginFlowManager>() {
    public PhoneLoginFlowManager createFromParcel(Parcel source) {
      return new PhoneLoginFlowManager(source);
    }

    public PhoneLoginFlowManager[] newArray(int size) {
      return new PhoneLoginFlowManager[size];
    }
  };
//  private PhoneNumber lastUsedPhoneNumber;
//  private NotificationChannel notificationChannel;

  PhoneLoginFlowManager(AccountKitConfiguration configuration) {
    super(LoginType.PHONE);
//    this.notificationChannel = NotificationChannel.SMS;
//    this.activityHandler = new ActivityPhoneHandler(configuration);
  }

  PhoneLoginFlowManager(Parcel parcel) {
    super(parcel);
//    this.notificationChannel = NotificationChannel.SMS;
//    this.activityHandler = (ActivityHandler) parcel.readParcelable(ActivityPhoneHandler.class.getClassLoader());
//    this.setLastUsedPhoneNumber((PhoneNumber) parcel.readParcelable(PhoneNumber.class.getClassLoader()));
  }

  private PhoneNumber getLastUsedPhoneNumber() {
//    return this.lastUsedPhoneNumber;
    return null;
  }

  void setLastUsedPhoneNumber(PhoneNumber lastUsedPhoneNumber) {
//    this.lastUsedPhoneNumber = lastUsedPhoneNumber;
  }

  public void logInWithPhoneNumber(PhoneNumber phoneNumber, NotificationChannel notificationChannel, AccountKitActivity.ResponseType responseType, @Nullable String initialAuthState, boolean testSmsWithInfobip) {
    if (this.isValid()) {
//      this.setLastUsedPhoneNumber(phoneNumber);
//      AccountKitController.logInWithPhoneNumber(phoneNumber, notificationChannel, responseType.getValue(), initialAuthState, testSmsWithInfobip);
    }
  }

  public NotificationChannel getNotificationChannel() {
//    return this.notificationChannel;
    return null;
  }

  public void setNotificationChannel(NotificationChannel notificationChannel) {
//    this.notificationChannel = notificationChannel;
  }

  public void setConfirmationCode(String confirmationCode) {
    if (this.isValid()) {
//      AccountKitController.continueLoginWithCode(confirmationCode);
    }
  }

  void confirmSeamlessLogin() {
//    if (this.isValid()) {
//      AccountKitController.continueSeamlessLogin();
//    }
  }

  public void writeToParcel(Parcel dest, int flags) {
//    super.writeToParcel(dest, flags);
//    dest.writeParcelable(this.activityHandler, flags);
//    dest.writeParcelable(this.getLastUsedPhoneNumber(), flags);
  }
}
