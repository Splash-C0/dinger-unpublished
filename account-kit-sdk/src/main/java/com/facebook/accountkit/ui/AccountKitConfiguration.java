package com.facebook.accountkit.ui;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.PhoneNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public final class AccountKitConfiguration implements Parcelable {
  public static final Creator CREATOR = new Creator() {
    public AccountKitConfiguration createFromParcel(Parcel in) {
      return new AccountKitConfiguration(in);
    }

    public AccountKitConfiguration[] newArray(int size) {
      return new AccountKitConfiguration[size];
    }
  };
  static final String TAG = AccountKitConfiguration.class.getSimpleName();
  @NonNull
  private final UIManager uiManager;
  private final String defaultCountryCode;
  private final LinkedHashSet<NotificationChannel> notificationChannels;
  private final String initialAuthState;
  private final String initialEmail;
  private final PhoneNumber initialPhoneNumber;
  private final LoginType loginType;
  private final boolean readPhoneStateEnabled;
  private final AccountKitActivity.ResponseType responseType;
  private final String[] smsBlacklist;
  private final String[] smsWhitelist;
  private final boolean enableSms;
  private final boolean testSmsWithInfobip;

  private AccountKitConfiguration(@NonNull UIManager uiManager, String defaultCountryCode, LinkedHashSet<NotificationChannel> notificationChannels, String initialAuthState, String initialEmail, PhoneNumber initialPhoneNumber, LoginType loginType, boolean readPhoneStateEnabled, AccountKitActivity.ResponseType responseType, String[] smsBlacklist, String[] smsWhitelist, boolean enableSms, boolean testSmsWithInfobip) {
    this.notificationChannels = new LinkedHashSet(NotificationChannel.values().length);
    this.initialAuthState = initialAuthState;
    this.defaultCountryCode = defaultCountryCode;
    this.initialEmail = initialEmail;
    this.notificationChannels.addAll(notificationChannels);
    this.uiManager = uiManager;
    this.loginType = loginType;
    this.initialPhoneNumber = initialPhoneNumber;
    this.readPhoneStateEnabled = readPhoneStateEnabled;
    this.responseType = responseType;
    this.smsBlacklist = smsBlacklist;
    this.smsWhitelist = smsWhitelist;
    this.enableSms = enableSms;
    this.testSmsWithInfobip = testSmsWithInfobip;
  }

  private AccountKitConfiguration(Parcel parcel) {
    this.notificationChannels = new LinkedHashSet(NotificationChannel.values().length);
    this.uiManager = (UIManager) parcel.readParcelable(UIManager.class.getClassLoader());
    this.defaultCountryCode = parcel.readString();
    this.notificationChannels.clear();
    int[] channelList = parcel.createIntArray();
    int[] var3 = channelList;
    int var4 = channelList.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      int channel = var3[var5];
      this.notificationChannels.add(NotificationChannel.values()[channel]);
    }

    this.initialAuthState = parcel.readString();
    this.initialEmail = parcel.readString();
    this.initialPhoneNumber = (PhoneNumber) parcel.readParcelable(PhoneNumber.class.getClassLoader());
    this.loginType = LoginType.valueOf(parcel.readString());
    this.readPhoneStateEnabled = parcel.readByte() != 0;
    this.responseType = AccountKitActivity.ResponseType.valueOf(parcel.readString());
    this.smsBlacklist = parcel.createStringArray();
    this.smsWhitelist = parcel.createStringArray();
    this.enableSms = parcel.readByte() != 0;
    this.testSmsWithInfobip = parcel.readByte() != 0;
  }

  /**
   * @deprecated
   */
  @Deprecated
  @Nullable
  public AdvancedUIManager getAdvancedUIManager() {
    return this.uiManager instanceof AdvancedUIManagerWrapper ? ((AdvancedUIManagerWrapper) this.uiManager).getAdvancedUIManager() : null;
  }

  @NonNull
  public UIManager getUIManager() {
    return this.uiManager;
  }

  public String getDefaultCountryCode() {
    return this.defaultCountryCode;
  }

  public List<NotificationChannel> getNotificationChannels() {
    return Collections.unmodifiableList(new ArrayList(this.notificationChannels));
  }

  /**
   * @deprecated
   */
  public boolean areFacebookNotificationsEnabled() {
    return this.getNotificationChannels().contains(NotificationChannel.FACEBOOK);
  }

  public String getInitialAuthState() {
    return this.initialAuthState;
  }

  public String getInitialEmail() {
    return this.initialEmail;
  }

  public PhoneNumber getInitialPhoneNumber() {
    return this.initialPhoneNumber;
  }

  public LoginType getLoginType() {
    return this.loginType;
  }

  public boolean isReadPhoneStateEnabled() {
    return this.readPhoneStateEnabled;
  }

  public AccountKitActivity.ResponseType getResponseType() {
    return this.responseType;
  }

  public String[] getSmsBlacklist() {
    return this.smsBlacklist;
  }

  public String[] getSmsWhitelist() {
    return this.smsWhitelist;
  }

  public boolean getEnableSms() {
    return this.enableSms;
  }

  public boolean getTestSmsWithInfobip() {
    return this.testSmsWithInfobip;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public int getTheme() {
    return this.uiManager.getThemeId();
  }

  /**
   * @deprecated
   */
  @Deprecated
  public AccountKitActivity.TitleType getTitleType() {
    return null;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.uiManager, flags);
    dest.writeString(this.defaultCountryCode);
    NotificationChannel[] arr = new NotificationChannel[this.notificationChannels.size()];
    this.notificationChannels.toArray(arr);
    int[] channels = new int[arr.length];

    for (int i = 0; i < arr.length; ++i) {
      channels[i] = arr[i].ordinal();
    }

    dest.writeIntArray(channels);
    dest.writeString(this.initialAuthState);
    dest.writeString(this.initialEmail);
    dest.writeParcelable(this.initialPhoneNumber, flags);
    dest.writeString(this.loginType.name());
    dest.writeByte((byte) (this.readPhoneStateEnabled ? 1 : 0));
    dest.writeString(this.responseType.name());
    dest.writeStringArray(this.smsBlacklist);
    dest.writeStringArray(this.smsWhitelist);
    dest.writeByte((byte) (this.enableSms ? 1 : 0));
    dest.writeByte((byte) (this.testSmsWithInfobip ? 1 : 0));
  }

  public static class AccountKitConfigurationBuilder {
    private final LinkedHashSet<NotificationChannel> notificationChannels = new LinkedHashSet(NotificationChannel.values().length);
    private UIManagerStub uiManager;
    private String defaultCountryCode;
    private String initialAuthState;
    private String initialEmail;
    private PhoneNumber initialPhoneNumber;
    private LoginType loginType;
    private boolean readPhoneStateEnabled = true;
    private AccountKitActivity.ResponseType responseType;
    private String[] smsBlacklist;
    private String[] smsWhitelist;
    /**
     * @deprecated
     */
    @Deprecated
    private int theme = -1;
    private boolean enableSms = true;
    private boolean testSmsWithInfobip;

    public AccountKitConfigurationBuilder(LoginType loginType, AccountKitActivity.ResponseType responseType) {
      this.notificationChannels.add(NotificationChannel.FACEBOOK);
      this.notificationChannels.add(NotificationChannel.SMS);
      this.loginType = loginType;
      this.responseType = responseType;
    }

    /**
     * @deprecated
     */
    public AccountKitConfiguration.AccountKitConfigurationBuilder setAdvancedUIManager(@Nullable AdvancedUIManager advancedUIManager) {
      this.uiManager = advancedUIManager;
      this.theme = -1;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setUIManager(@Nullable UIManager uiManager) {
      this.uiManager = uiManager;
      this.theme = -1;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setDefaultCountryCode(@Nullable String defaultCountryCode) {
      this.defaultCountryCode = defaultCountryCode;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setFacebookNotificationsEnabled(boolean facebookNotificationsEnabled) {
      if (!facebookNotificationsEnabled) {
        this.notificationChannels.remove(NotificationChannel.FACEBOOK);
      } else if (!this.notificationChannels.contains(NotificationChannel.FACEBOOK)) {
        this.notificationChannels.add(NotificationChannel.FACEBOOK);
      }

      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setInitialAuthState(@Nullable String initialAuthState) {
      this.initialAuthState = initialAuthState;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setInitialEmail(@Nullable String initialEmail) {
      this.initialEmail = initialEmail;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setInitialPhoneNumber(@Nullable PhoneNumber initialPhoneNumber) {
      this.initialPhoneNumber = initialPhoneNumber;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setReadPhoneStateEnabled(boolean readPhoneStateEnabled) {
      this.readPhoneStateEnabled = readPhoneStateEnabled;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setSMSBlacklist(@Nullable String[] smsBlacklist) {
      this.smsBlacklist = smsBlacklist;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setSMSWhitelist(@Nullable String[] smsWhitelist) {
      this.smsWhitelist = smsWhitelist;
      return this;
    }

    /**
     * @deprecated
     */
    public AccountKitConfiguration.AccountKitConfigurationBuilder setTheme(int theme) {
      this.theme = theme;
      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setEnableSms(@Nullable boolean enableSms) {
      this.enableSms = enableSms;
      if (!enableSms) {
        this.notificationChannels.remove(NotificationChannel.SMS);
      } else if (!this.notificationChannels.contains(NotificationChannel.SMS)) {
        this.notificationChannels.add(NotificationChannel.SMS);
      }

      return this;
    }

    public AccountKitConfiguration.AccountKitConfigurationBuilder setTestSmsWithInfobip(boolean testSmsWithInfobip) {
      this.testSmsWithInfobip = testSmsWithInfobip;
      return this;
    }

    /**
     * @deprecated
     */
    public AccountKitConfiguration.AccountKitConfigurationBuilder setTitleType(@Nullable AccountKitActivity.TitleType titleType) {
      return this;
    }

    public AccountKitConfiguration build() {
      if (this.uiManager == null) {
        this.uiManager = new ThemeUIManager(this.theme);
      } else if (this.theme != -1 && this.uiManager instanceof SkinManager) {
        ((UIManager) this.uiManager).setThemeId(this.theme);
      }

      if (this.uiManager instanceof AdvancedUIManager) {
        this.uiManager = new AdvancedUIManagerWrapper((AdvancedUIManager) this.uiManager, this.theme);
      }

      return new AccountKitConfiguration((UIManager) this.uiManager, this.defaultCountryCode, this.notificationChannels, this.initialAuthState, this.initialEmail, this.initialPhoneNumber, this.loginType, this.readPhoneStateEnabled, this.responseType, this.smsBlacklist, this.smsWhitelist, this.enableSms, this.testSmsWithInfobip);
    }
  }
}
