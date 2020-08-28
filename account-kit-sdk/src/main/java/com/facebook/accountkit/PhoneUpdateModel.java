package com.facebook.accountkit;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.facebook.accountkit.internal.UpdateStatus;

public interface PhoneUpdateModel extends Parcelable {
  PhoneNumber getPhoneNumber();

  String getConfirmationCode();

  String getInitialUpdateState();

  String getFinalUpdateState();

  String getUpdateRequestCode();

  long getResendTime();

  UpdateStatus getStatus();

  AccountKitError getError();

  @Nullable
  String getPrivacyPolicy();

  @Nullable
  String getTermsOfService();
}
