package com.facebook.accountkit.internal;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.PhoneUpdateModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class PhoneUpdateModelImpl implements PhoneUpdateModel {
  public static final Creator<PhoneUpdateModelImpl> CREATOR = new Creator<PhoneUpdateModelImpl>() {
    public PhoneUpdateModelImpl createFromParcel(Parcel source) {
      return new PhoneUpdateModelImpl(source);
    }

    public PhoneUpdateModelImpl[] newArray(int size) {
      return new PhoneUpdateModelImpl[size];
    }
  };
  private PhoneNumber phoneNumber;
  private long resendTime;
  private long expiresInSeconds;
  private String confirmationCode;
  private String updateRequestCode;
  private String initialUpdateState;
  private String finalUpdateState;
  private UpdateStatus status;
  private AccountKitError error;
  private Map<String, String> fields;

  PhoneUpdateModelImpl(PhoneNumber phoneNumber) {
    this.status = UpdateStatus.EMPTY;
    this.fields = new HashMap();
    this.phoneNumber = phoneNumber;
  }

  private PhoneUpdateModelImpl(Parcel parcel) {
    this.status = UpdateStatus.EMPTY;
    this.fields = new HashMap();
    this.phoneNumber = (PhoneNumber) parcel.readParcelable(PhoneNumber.class.getClassLoader());
    this.resendTime = parcel.readLong();
    this.expiresInSeconds = parcel.readLong();
    this.confirmationCode = parcel.readString();
    this.updateRequestCode = parcel.readString();
    this.finalUpdateState = parcel.readString();
    this.error = (AccountKitError) parcel.readParcelable(AccountKitError.class.getClassLoader());
    this.status = UpdateStatus.valueOf(parcel.readString());
    this.fields = new HashMap();
    int size = parcel.readInt();

    for (int i = 0; i < size; ++i) {
      String key = parcel.readString();
      String value = parcel.readString();
      this.fields.put(key, value);
    }

  }

  public PhoneNumber getPhoneNumber() {
    return this.phoneNumber;
  }

  void setPhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getConfirmationCode() {
    return this.confirmationCode;
  }

  void setConfirmationCode(String confirmationCode) {
    Validate.isEquals(this.getStatus(), UpdateStatus.PENDING, "Phone status");
    Validate.sdkInitialized();
    this.confirmationCode = confirmationCode;
  }

  public String getInitialUpdateState() {
    return this.initialUpdateState;
  }

  void setInitialUpdateState(String initialUpdateState) {
    this.initialUpdateState = initialUpdateState;
  }

  public String getFinalUpdateState() {
    return this.finalUpdateState;
  }

  void setFinalUpdateState(String finalUpdateState) {
    this.finalUpdateState = finalUpdateState;
  }

  public String getUpdateRequestCode() {
    return this.updateRequestCode;
  }

  void setUpdateRequestCode(String updateRequestCode) {
    this.updateRequestCode = updateRequestCode;
  }

  public long getResendTime() {
    return this.resendTime;
  }

  void setResendTime(long resendTime) {
    this.resendTime = resendTime;
  }

  public UpdateStatus getStatus() {
    return this.status;
  }

  void setStatus(UpdateStatus status) {
    this.status = status;
  }

  public AccountKitError getError() {
    return this.error;
  }

  void setError(AccountKitError error) {
    this.error = error;
  }

  void setExpiresInSeconds(long expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
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
    } else if (!(other instanceof PhoneUpdateModelImpl)) {
      return false;
    } else {
      PhoneUpdateModelImpl o = (PhoneUpdateModelImpl) other;
      return this.expiresInSeconds == o.expiresInSeconds && this.resendTime == o.resendTime && Utility.areObjectsEqual(this.error, o.error) && Utility.areObjectsEqual(this.status, o.status) && Utility.areObjectsEqual(this.phoneNumber, o.phoneNumber) && Utility.areObjectsEqual(this.updateRequestCode, o.updateRequestCode) && Utility.areObjectsEqual(this.finalUpdateState, o.finalUpdateState) && Utility.areObjectsEqual(this.confirmationCode, o.confirmationCode);
    }
  }

  public int hashCode() {
    int result = 17;
    result = result * 31 + this.phoneNumber.hashCode();
    result = result * 31 + Long.valueOf(this.resendTime).hashCode();
    result = result * 31 + Long.valueOf(this.expiresInSeconds).hashCode();
    result = result * 31 + this.error.hashCode();
    result = result * 31 + this.status.hashCode();
    result = result * 31 + this.updateRequestCode.hashCode();
    result = result * 31 + this.finalUpdateState.hashCode();
    result = result * 31 + this.confirmationCode.hashCode();
    return result;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.phoneNumber, flags);
    dest.writeLong(this.resendTime);
    dest.writeLong(this.expiresInSeconds);
    dest.writeString(this.confirmationCode);
    dest.writeString(this.updateRequestCode);
    dest.writeString(this.finalUpdateState);
    dest.writeParcelable(this.error, flags);
    dest.writeString(this.status.name());
    dest.writeInt(this.fields.size());
    Iterator var3 = this.fields.keySet().iterator();

    while (var3.hasNext()) {
      String key = (String) var3.next();
      dest.writeString(key);
      dest.writeString((String) this.fields.get(key));
    }

  }
}
