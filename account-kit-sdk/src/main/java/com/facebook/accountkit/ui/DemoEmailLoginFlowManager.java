package com.facebook.accountkit.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.EmailLoginModel;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.InternalAccountKitError;
import com.facebook.accountkit.internal.LoginStatus;

import java.util.Date;

final class DemoEmailLoginFlowManager extends EmailLoginFlowManager {
  public static final Creator<DemoEmailLoginFlowManager> CREATOR = new Creator<DemoEmailLoginFlowManager>() {
    public DemoEmailLoginFlowManager createFromParcel(Parcel source) {
      return new DemoEmailLoginFlowManager(source);
    }

    public DemoEmailLoginFlowManager[] newArray(int size) {
      return new DemoEmailLoginFlowManager[size];
    }
  };
  private static final int MOCK_EMAIL_DELAY_MS = 6000;
  private static final int MOCK_NETWORK_DELAY_MS = 2000;
  private boolean isValid = true;
  private DemoEmailLoginFlowManager.DemoEmailLoginModel loginModel;

  public DemoEmailLoginFlowManager(AccountKitConfiguration configuration) {
    super(configuration);
  }

  protected DemoEmailLoginFlowManager(Parcel parcel) {
    super(parcel);
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

  public void logInWithEmail(AccountKitActivity.ResponseType responseType, String initialAuthState) {
    if (this.isValid) {
      final String email = this.getEmail();
      String confirmationCode = responseType == AccountKitActivity.ResponseType.CODE ? "DEMOCODE" : null;
      AccessToken accessToken = responseType == AccountKitActivity.ResponseType.TOKEN ? this.getAccessToken() : null;
      this.loginModel = new DemoEmailLoginFlowManager.DemoEmailLoginModel(email, initialAuthState, confirmationCode, accessToken);
      (new Handler()).postDelayed(new Runnable() {
        public void run() {
          if (email.endsWith("@example.com")) {
            DemoEmailLoginFlowManager.this.broadcastLoginState(LoginStatus.PENDING, (AccountKitError) null);
            (new Handler()).postDelayed(new Runnable() {
              public void run() {
                DemoEmailLoginFlowManager.this.broadcastLoginState(LoginStatus.SUCCESS, (AccountKitError) null);
              }
            }, 6000L);
          } else {
            InternalAccountKitError internalError = new InternalAccountKitError(15003, (String) null, "[Demo] use *@example.com");
            AccountKitError error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
            DemoEmailLoginFlowManager.this.broadcastLoginState(LoginStatus.ERROR, error);
          }

        }
      }, 2000L);
    }
  }

  private void broadcastLoginState(LoginStatus status, AccountKitError error) {
    LocalBroadcastManager.getInstance(AccountKitController.getApplicationContext()).sendBroadcast((new Intent("com.facebook.accountkit.sdk.ACTION_EMAIL_LOGIN_STATE_CHANGED")).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_MODEL", this.loginModel).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_STATUS", status).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_ERROR", error));
  }

  private static class DemoEmailLoginModel implements EmailLoginModel {
    public static final Creator<DemoEmailLoginFlowManager.DemoEmailLoginModel> CREATOR = new Creator<DemoEmailLoginFlowManager.DemoEmailLoginModel>() {
      public DemoEmailLoginFlowManager.DemoEmailLoginModel createFromParcel(Parcel source) {
        return new DemoEmailLoginFlowManager.DemoEmailLoginModel(source);
      }

      public DemoEmailLoginFlowManager.DemoEmailLoginModel[] newArray(int size) {
        return new DemoEmailLoginFlowManager.DemoEmailLoginModel[size];
      }
    };
    private final AccessToken accessToken;
    private final String authState;
    private final String confirmationCode;
    private final String email;

    public DemoEmailLoginModel(String email, String authState, String confirmationCode, AccessToken accessToken) {
      this.email = email;
      this.authState = authState;
      this.confirmationCode = confirmationCode;
      this.accessToken = accessToken;
    }

    DemoEmailLoginModel(Parcel parcel) {
      this.accessToken = (AccessToken) parcel.readParcelable(AccessToken.class.getClassLoader());
      this.authState = parcel.readString();
      this.confirmationCode = parcel.readString();
      this.email = parcel.readString();
    }

    public String getEmail() {
      return this.email;
    }

    public String getFinalAuthState() {
      return this.authState;
    }

    @Nullable
    public String getPrivacyPolicy() {
      return null;
    }

    @Nullable
    public String getTermsOfService() {
      return null;
    }

    public String getCode() {
      return null;
    }

    public AccessToken getAccessToken() {
      return this.accessToken;
    }

    public int describeContents() {
      return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.accessToken, flags);
      dest.writeString(this.authState);
      dest.writeString(this.confirmationCode);
      dest.writeString(this.email);
    }
  }
}
