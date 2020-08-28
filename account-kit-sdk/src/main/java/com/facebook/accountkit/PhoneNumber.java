package com.facebook.accountkit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.internal.Utility;

public final class PhoneNumber implements Parcelable {
  public static final Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {
    public PhoneNumber createFromParcel(Parcel source) {
      return new PhoneNumber(source);
    }

    public PhoneNumber[] newArray(int size) {
      return new PhoneNumber[size];
    }
  };
  private final String phoneNumber;
  private final String countryCode;
  private final String countryCodeIso;

  public PhoneNumber(@NonNull String countryCode, @NonNull String phoneNumber, @Nullable String countryCodeIso) {
    this.phoneNumber = Utility.cleanPhoneNumberString(phoneNumber);
    this.countryCode = Utility.cleanPhoneNumberString(countryCode);
    this.countryCodeIso = countryCodeIso;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public PhoneNumber(String countryCode, String phoneNumber) {
    this(countryCode, phoneNumber, (String) null);
  }

  private PhoneNumber(Parcel parcel) {
    this.countryCode = parcel.readString();
    this.phoneNumber = parcel.readString();
    this.countryCodeIso = parcel.readString();
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getCountryCode() {
    return this.countryCode;
  }

  public String getCountryCodeIso() {
    return this.countryCodeIso;
  }

  public String getRawPhoneNumber() {
    return this.countryCode + this.phoneNumber;
  }

  public String toString() {
    return "+" + this.countryCode + this.phoneNumber;
  }

  public String toRtlSafeString() {
    return "\u202a+" + this.countryCode + this.phoneNumber + "\u202c";
  }

  public int hashCode() {
    return this.toString().hashCode();
  }

  public boolean equals(Object other) {
    return other instanceof PhoneNumber && this.hashCode() == other.hashCode();
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.countryCode);
    dest.writeString(this.phoneNumber);
    dest.writeString(this.countryCodeIso);
  }
}
