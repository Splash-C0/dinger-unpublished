package com.facebook.accountkit.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneLoginModel;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.InternalAccountKitError;
import com.facebook.accountkit.internal.LoginStatus;

import java.util.Date;

final class DemoPhoneLoginFlowManager extends PhoneLoginFlowManager {
  public static final Creator<DemoPhoneLoginFlowManager> CREATOR = new Creator<DemoPhoneLoginFlowManager>() {
    public DemoPhoneLoginFlowManager createFromParcel(Parcel source) {
      return new DemoPhoneLoginFlowManager(source);
    }

    public DemoPhoneLoginFlowManager[] newArray(int size) {
      return new DemoPhoneLoginFlowManager[size];
    }
  };
  private static final String MOCK_CONFIRMATION_CODE = "123456";
  private static final int MOCK_NETWORK_DELAY_MS = 2000;
  private boolean isValid;
  private DemoPhoneLoginFlowManager.DemoPhoneLoginModel loginModel;
  private ActivityPhoneHandler phoneListeners;
  private AccountKitActivity activity;

  public DemoPhoneLoginFlowManager(AccountKitConfiguration configuration, AccountKitActivity activity, ActivityPhoneHandler phoneListeners) {
    super(configuration);
    this.isValid = true;
    this.activity = activity;
    this.phoneListeners = phoneListeners;
  }

  private DemoPhoneLoginFlowManager(Parcel parcel) {
    super(parcel);
    this.isValid = true;
  }

  public void cancel() {
    this.isValid = false;
    this.broadcastLoginState(LoginStatus.CANCELLED, (AccountKitError) null);
  }

  public AccessToken getAccessToken() {
    return !this.isValid ? null : new AccessToken("TEST_ACCESS_TOKEN", "TEST_ACCOUNT_ID", AccountKit.getApplicationId(), 300000L, new Date());
  }

  public boolean isValid() {
    return this.isValid;
  }

  public void logInWithPhoneNumber(final PhoneNumber phoneNumber, NotificationChannel notificationChannel, AccountKitActivity.ResponseType responseType, String initialAuthState, boolean testSmsWithInfobip) {
    if (this.isValid) {
      String confirmationCode = responseType == AccountKitActivity.ResponseType.CODE ? "DEMOCODE" : null;
      AccessToken accessToken = responseType == AccountKitActivity.ResponseType.TOKEN ? this.getAccessToken() : null;
      this.loginModel = new DemoPhoneLoginFlowManager.DemoPhoneLoginModel(phoneNumber, initialAuthState, confirmationCode, accessToken);
      this.setLastUsedPhoneNumber(phoneNumber);
      (new Handler()).postDelayed(new Runnable() {
        public void run() {
          if (phoneNumber.getPhoneNumber().length() == 10) {
            DemoPhoneLoginFlowManager.this.broadcastLoginState(LoginStatus.PENDING, (AccountKitError) null);
            (new Handler()).postDelayed(new Runnable() {
              public void run() {
                DemoPhoneLoginFlowManager.this.phoneListeners.startSmsTrackerIfPossible(DemoPhoneLoginFlowManager.this.activity);
                DemoPhoneLoginFlowManager.this.phoneListeners.getSmsTracker().confirmationCodeReceived("123456");
              }
            }, 2000L);
          } else {
            InternalAccountKitError internalError = new InternalAccountKitError(1948002, (String) null, "[Demo] use a 10 digit number");
            AccountKitError error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
            DemoPhoneLoginFlowManager.this.broadcastLoginState(LoginStatus.ERROR, error);
          }

        }
      }, 2000L);
    }
  }

  public void setConfirmationCode(final String confirmationCode) {
    if (this.isValid) {
      (new Handler()).postDelayed(new Runnable() {
        public void run() {
          if (confirmationCode.equals("123456")) {
            DemoPhoneLoginFlowManager.this.broadcastLoginState(LoginStatus.SUCCESS, (AccountKitError) null);
          } else {
            InternalAccountKitError internalError = new InternalAccountKitError(1948002, (String) null, "[Demo] use confirmation code 123456");
            AccountKitError error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
            DemoPhoneLoginFlowManager.this.broadcastLoginState(LoginStatus.ERROR, error);
          }

        }
      }, 2000L);
    }
  }

  private void broadcastLoginState(LoginStatus status, AccountKitError error) {
    LocalBroadcastManager.getInstance(AccountKitController.getApplicationContext()).sendBroadcast((new Intent("com.facebook.accountkit.sdk.ACTION_PHONE_LOGIN_STATE_CHANGED")).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_MODEL", this.loginModel).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_STATUS", status).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_ERROR", error));
  }

  private static class DemoPhoneLoginModel implements PhoneLoginModel {
    public static final Creator<DemoPhoneLoginFlowManager.DemoPhoneLoginModel> CREATOR = new Creator<DemoPhoneLoginFlowManager.DemoPhoneLoginModel>() {
      public DemoPhoneLoginFlowManager.DemoPhoneLoginModel createFromParcel(Parcel source) {
        return new DemoPhoneLoginFlowManager.DemoPhoneLoginModel(source);
      }

      public DemoPhoneLoginFlowManager.DemoPhoneLoginModel[] newArray(int size) {
        return new DemoPhoneLoginFlowManager.DemoPhoneLoginModel[size];
      }
    };
    private final AccessToken accessToken;
    private final String authState;
    private final String confirmationCode;
    private final PhoneNumber phoneNumber;

    DemoPhoneLoginModel(PhoneNumber phoneNumber, String authState, String confirmationCode, AccessToken accessToken) {
      this.phoneNumber = phoneNumber;
      this.authState = authState;
      this.confirmationCode = confirmationCode;
      this.accessToken = accessToken;
    }

    DemoPhoneLoginModel(Parcel parcel) {
      this.accessToken = (AccessToken) parcel.readParcelable(AccessToken.class.getClassLoader());
      this.authState = parcel.readString();
      this.confirmationCode = parcel.readString();
      this.phoneNumber = (PhoneNumber) parcel.readParcelable(PhoneNumber.class.getClassLoader());
    }

    public String getConfirmationCode() {
      return this.confirmationCode;
    }

    public PhoneNumber getPhoneNumber() {
      return this.phoneNumber;
    }

    public String getPrivacyPolicy() {
      return null;
    }

    public String getTermsOfService() {
      return null;
    }

    public String getFinalAuthState() {
      return this.authState;
    }

    public String getCode() {
      return this.confirmationCode;
    }

    public AccessToken getAccessToken() {
      return this.accessToken;
    }

    public NotificationChannel getNotificationChannel() {
      return NotificationChannel.SMS;
    }

    public long getResendTime() {
      return System.currentTimeMillis();
    }

    public int describeContents() {
      return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.accessToken, flags);
      dest.writeString(this.authState);
      dest.writeString(this.confirmationCode);
      dest.writeParcelable(this.phoneNumber, flags);
    }
  }
}
