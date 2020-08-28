package com.facebook.accountkit.internal;

import android.os.Parcel;

import com.facebook.accountkit.EmailLoginModel;

import java.util.HashMap;
import java.util.Iterator;

public final class EmailLoginModelImpl extends LoginModelImpl implements EmailLoginModel {
  public static final Creator<EmailLoginModelImpl> CREATOR = new Creator<EmailLoginModelImpl>() {
    public EmailLoginModelImpl createFromParcel(Parcel source) {
      return new EmailLoginModelImpl(source);
    }

    public EmailLoginModelImpl[] newArray(int size) {
      return new EmailLoginModelImpl[size];
    }
  };
  private String email;
  private int interval;

  EmailLoginModelImpl(String email, String requestType) {
    super(requestType);
    this.email = email;
  }

  private EmailLoginModelImpl(Parcel parcel) {
    super(parcel);
    this.email = parcel.readString();
    this.interval = parcel.readInt();
    this.fields = new HashMap();
    int size = parcel.readInt();

    for (int i = 0; i < size; ++i) {
      String key = parcel.readString();
      String value = parcel.readString();
      this.fields.put(key, value);
    }

  }

  public String getEmail() {
    return this.email;
  }

  void setEmail(String email) {
    this.email = email;
  }

  public int getInterval() {
    return this.interval;
  }

  void setInterval(int interval) {
    this.interval = interval;
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof EmailLoginModelImpl)) {
      return false;
    } else {
      EmailLoginModelImpl o = (EmailLoginModelImpl) other;
      return super.equals(other) && this.interval == o.interval && Utility.areObjectsEqual(this.email, o.email);
    }
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.email);
    dest.writeInt(this.interval);
    dest.writeInt(this.fields.size());
    Iterator var3 = this.fields.keySet().iterator();

    while (var3.hasNext()) {
      String key = (String) var3.next();
      dest.writeString(key);
      dest.writeString((String) this.fields.get(key));
    }

  }
}
