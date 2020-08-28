package com.facebook.accountkit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.internal.Utility;

public final class Account implements Parcelable {
  public static final Creator<Account> CREATOR = new Creator<Account>() {
    public Account createFromParcel(Parcel source) {
      return new Account(source);
    }

    public Account[] newArray(int size) {
      return new Account[size];
    }
  };
  private final String email;
  private final String id;
  private final PhoneNumber phoneNumber;

  public Account(@NonNull String id, @Nullable PhoneNumber phoneNumber, @Nullable String email) {
    this.id = id;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  private Account(Parcel source) {
    this.id = source.readString();
    this.phoneNumber = (PhoneNumber) source.readParcelable(PhoneNumber.class.getClassLoader());
    this.email = source.readString();
  }

  public String getEmail() {
    return this.email;
  }

  public String getId() {
    return this.id;
  }

  public PhoneNumber getPhoneNumber() {
    return this.phoneNumber;
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof Account)) {
      return false;
    } else {
      Account o = (Account) other;
      return Utility.areObjectsEqual(this.email, o.email) && Utility.areObjectsEqual(this.id, o.id) && Utility.areObjectsEqual(this.phoneNumber, o.phoneNumber);
    }
  }

  public int hashCode() {
    int result = 17;
    result = result * 31 + Utility.getHashCode(this.email);
    result = result * 31 + Utility.getHashCode(this.id);
    result = result * 31 + Utility.getHashCode(this.phoneNumber);
    return result;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeParcelable(this.phoneNumber, flags);
    dest.writeString(this.email);
  }
}
