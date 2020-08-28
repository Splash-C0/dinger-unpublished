package com.facebook.accountkit.internal;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.facebook.accountkit.PhoneLoginModel;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.NotificationChannel;

import java.util.HashMap;
import java.util.Iterator;

public final class PhoneLoginModelImpl extends LoginModelImpl implements PhoneLoginModel {
  public static final Creator<PhoneLoginModelImpl> CREATOR = new Creator<PhoneLoginModelImpl>() {
    public PhoneLoginModelImpl createFromParcel(Parcel source) {
      return new PhoneLoginModelImpl(source);
    }

    public PhoneLoginModelImpl[] newArray(int size) {
      return new PhoneLoginModelImpl[size];
    }
  };
  @NonNull
  private final NotificationChannel notificationChannel;
  private String confirmationCode;
  private long resendTime;
  private PhoneNumber phoneNumber;
  private boolean testSmsWithInfobip;

  PhoneLoginModelImpl(PhoneNumber phoneNumber, @NonNull NotificationChannel notificationChannel, String requestType) {
    super(requestType);
    this.notificationChannel = notificationChannel;
    this.phoneNumber = phoneNumber;
  }

  private PhoneLoginModelImpl(Parcel parcel) {
    super(parcel);
    this.phoneNumber = (PhoneNumber) parcel.readParcelable(PhoneNumber.class.getClassLoader());
    this.confirmationCode = parcel.readString();
    this.notificationChannel = NotificationChannel.values()[parcel.readInt()];
    this.fields = new HashMap();
    int size = parcel.readInt();

    for (int i = 0; i < size; ++i) {
      String key = parcel.readString();
      String value = parcel.readString();
      this.fields.put(key, value);
    }

    this.resendTime = parcel.readLong();
    this.testSmsWithInfobip = parcel.readByte() != 0;
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

  void setConfirmationCode(@NonNull String confirmationCode) {
    Validate.isEquals(this.getStatus(), LoginStatus.PENDING, "Phone status");
    Validate.sdkInitialized();
    this.confirmationCode = confirmationCode;
  }

  public long getResendTime() {
    return this.resendTime;
  }

  void setResendTime(long resendTime) {
    this.resendTime = resendTime;
  }

  @NonNull
  public NotificationChannel getNotificationChannel() {
    return this.notificationChannel;
  }

  public boolean getTestSmsWithInfobip() {
    return this.testSmsWithInfobip;
  }

  public void setTestSmsWithInfobip(boolean testSmsWithInfobip) {
    this.testSmsWithInfobip = testSmsWithInfobip;
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof PhoneLoginModelImpl)) {
      return false;
    } else {
      PhoneLoginModelImpl o = (PhoneLoginModelImpl) other;
      return super.equals(o) && Utility.areObjectsEqual(this.confirmationCode, o.confirmationCode) && Utility.areObjectsEqual(this.phoneNumber, o.phoneNumber) && this.notificationChannel == o.notificationChannel && this.resendTime == o.resendTime;
    }
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.phoneNumber, flags);
    dest.writeString(this.confirmationCode);
    dest.writeInt(this.notificationChannel.ordinal());
    dest.writeInt(this.fields.size());
    Iterator var3 = this.fields.keySet().iterator();

    while (var3.hasNext()) {
      String key = (String) var3.next();
      dest.writeString(key);
      dest.writeString((String) this.fields.get(key));
    }

    dest.writeLong(this.resendTime);
    dest.writeByte((byte) (this.testSmsWithInfobip ? 1 : 0));
  }
}
