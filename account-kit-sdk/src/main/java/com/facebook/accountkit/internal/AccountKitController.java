package com.facebook.accountkit.internal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.EmailLoginModel;
import com.facebook.accountkit.PhoneLoginModel;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.PhoneUpdateModel;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.NotificationChannel;

import org.json.JSONException;
import org.json.JSONObject;

public final class AccountKitController {
  private static final String GRAPH_BASE_HOST = "graph.accountkit.com";
  private static final String ACCOUNT_KIT_PREFERENCES = "com.facebook.accountkit.internal.AccountKitController.preferences";
  private static final String GRAPH_HOST_PREFERENCE_KEY = "AccountHost";
  private static final Initializer initializer = new Initializer();
  private static final ExperimentationConfigurator experimentationConfigurator = new ExperimentationConfigurator();

  public AccountKitController() {
  }

  public static Context getApplicationContext() {
    return initializer.getApplicationContext();
  }

  public static boolean isInitialized() {
    return initializer.isInitialized();
  }

  public static void initialize(Context applicationContext, AccountKit.InitializeCallback callback) {
    initializer.initialize(applicationContext, callback);
    experimentationConfigurator.initialize(applicationContext);
  }

  public static void initializeLogin() {
    initializer.getLoginManager().initializeLogin();
  }

  public static EmailLoginModel logInWithEmail(String email, String responseType, @Nullable String initialAuthState) {
    if (getCurrentAccessToken() != null) {
      logOut();
    }

    return initializer.getLoginManager().logInWithEmail(email, responseType, initialAuthState);
  }

  public static PhoneLoginModel logInWithPhoneNumber(PhoneNumber phoneNumber, NotificationChannel notificationChannel, String responseType, @Nullable String initialAuthState, boolean testSmsWithInfobip) {
    if (getCurrentAccessToken() != null) {
      logOut();
    }

    return initializer.getLoginManager().logInWithPhoneNumber(phoneNumber, notificationChannel, responseType, initialAuthState, testSmsWithInfobip);
  }

  public static void logOut() {
    initializer.getLoginManager().logOut();
  }

  public static void logOut(AccountKitCallback<Void> callback) {
    initializer.getLoginManager().logOut(callback);
  }

  public static void cancelLogin() {
    initializer.getLoginManager().cancelLogin();
  }

  public static void continueLoginWithCode(String code) {
    initializer.getLoginManager().continueWithCode(code);
  }

  public static void continueSeamlessLogin() {
    initializer.getLoginManager().continueSeamlessLogin();
  }

  @Nullable
  public static PhoneUpdateModel updatePhoneNumber(PhoneNumber phoneNumber, @Nullable String initialAuthState) {
    return initializer.getUpdateManager().updatePhoneNumber(phoneNumber, initialAuthState);
  }

  public static void continueUpdateWithCode(String code) {
    initializer.getUpdateManager().continueWithCode(code);
  }

  public static void cancelUpdate() {
    initializer.getUpdateManager().cancelExisting();
  }

  public static ExperimentationConfiguration getExperimentationConfiguration() {
    return experimentationConfigurator.getExperimentationConfiguration();
  }

  @Nullable
  public static AccessToken getCurrentAccessToken() {
    return initializer.getAccessTokenManager().getCurrentAccessToken();
  }

  public static void getCurrentAccount(AccountKitCallback<Account> callback) {
    initializer.getLoginManager().getCurrentAccount(callback);
  }

  public static EmailLoginModel getCurrentEmailLogInModel() {
    return initializer.getLoginManager().getCurrentEmailLogInModel();
  }

  public static PhoneLoginModel getCurrentPhoneNumberLogInModel() {
    return initializer.getLoginManager().getCurrentPhoneNumberLogInModel();
  }

  public static String getLastUsedPhoneNotificationChannelValue() {
    NotificationChannel notifChannel = getCurrentPhoneNumberLogInModel() != null ? getCurrentPhoneNumberLogInModel().getNotificationChannel() : null;
    return notifChannel == null ? null : notifChannel.toString();
  }

  public static void onActivityCreate(Activity activity, Bundle savedInstanceState) {
    initializer.getLoginManager().onActivityCreate(activity, savedInstanceState);
  }

  public static void onActivityDestroy(Activity activity) {
    initializer.getLoginManager().onActivityDestroy(activity);
  }

  public static void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    initializer.getLoginManager().onActivitySaveInstanceState(activity, outState);
  }

  public static void onUpdateActivityCreate(Activity activity, Bundle savedInstanceState) {
    initializer.getUpdateManager().onActivityCreate(activity, savedInstanceState);
  }

  public static void onUpdateActivityDestroy(Activity activity) {
    initializer.getUpdateManager().onActivityDestroy(activity);
  }

  public static void onUpdateActivitySaveInstanceState(Activity activity, Bundle outState) {
    initializer.getUpdateManager().onActivitySaveInstanceState(activity, outState);
  }

  public static String getApplicationId() {
    return initializer.getApplicationId();
  }

  public static String getApplicationName() {
    return initializer.getApplicationName();
  }

  public static String getClientToken() {
    return initializer.getClientToken();
  }

  public static boolean getAccountKitFacebookAppEventsEnabled() {
    return initializer.getAccountKitFacebookAppEventsEnabled();
  }

  public static String getBaseGraphHost() {
    return getApplicationContext().getSharedPreferences("com.facebook.accountkit.internal.AccountKitController.preferences", 0).getString("AccountHost", "graph.accountkit.com");
  }

  public static void setBaseGraphHost(String host) {
    getApplicationContext().getSharedPreferences("com.facebook.accountkit.internal.AccountKitController.preferences", 0).edit().putString("AccountHost", host).apply();
  }

  public static class Logger {
    public Logger() {
    }

    public static void logUIPhoneLoginShown(String countryCode, String countryCodeSource, boolean isRetry) {
      JSONObject extras = new JSONObject();

      try {
        extras.put("country_code", countryCode);
        extras.put("country_code_source", countryCodeSource);
        extras.put("read_phone_number_permission", Utility.hasReadPhoneStatePermissions(AccountKitController.initializer.getApplicationContext()) ? "true" : "false");
        extras.put("sim_locale", Utility.getCurrentCountry(AccountKitController.initializer.getApplicationContext()));
        extras.put("retry", isRetry ? "true" : "false");
      } catch (JSONException var5) {
      }

      AccountKitController.initializer.getLogger().logImpression("ak_phone_login_view", "phone", (String) null, extras, true);
    }

    public static void logUIEmailLoginShown(boolean isRetry) {
      JSONObject extras = new JSONObject();

      try {
        extras.put("get_accounts_perm", Utility.hasGetAccountsPermissions(AccountKitController.initializer.getApplicationContext()) ? "true" : "false");
        extras.put("retry", isRetry ? "true" : "false");
      } catch (JSONException var3) {
      }

      AccountKitController.initializer.getLogger().logImpression("ak_email_login_view", "email", (String) null, extras, true);
    }

    public static void logUIPhoneLogin() {
      AccountKitController.initializer.getLogger().logImpression("ak_phone_login_view", "phone", (String) null, (JSONObject) null, false);
    }

    public static void logUIConfirmationCodeShown(boolean isRetry) {
      JSONObject extras = new JSONObject();

      try {
        extras.put("retry", isRetry ? "true" : "false");
      } catch (JSONException var3) {
      }

      AccountKitController.initializer.getLogger().logImpression("ak_confirmation_code_view", "phone", AccountKitController.getLastUsedPhoneNotificationChannelValue(), extras, true);
    }

    public static void logUIConfirmationCode() {
      AccountKitController.initializer.getLogger().logImpression("ak_confirmation_code_view", "phone", AccountKitController.getLastUsedPhoneNotificationChannelValue(), (JSONObject) null, false);
    }

    public static void logUIError(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_error_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUIResend(boolean isPresented) {
      AccountKitController.initializer.getLogger().logImpression("ak_resend_view", "phone", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUISendingCode(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_sending_code_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUISentCode(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_sent_code_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUIVerifyingCode(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_verifying_code_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUIVerifiedCode(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_verified_code_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }

    public static void logUIEmailLogin() {
      AccountKitController.initializer.getLogger().logImpression("ak_email_login_view", "email", (String) null, (JSONObject) null, false);
    }

    public static void logUIEmailVerify(boolean isPresented) {
      AccountKitController.initializer.getLogger().logImpression("ak_email_sent_view", "email", "email", (JSONObject) null, isPresented);
    }

    public static void logUICountryCode(boolean isPresented, String selectedCountry) {
      JSONObject extras = new JSONObject();

      try {
        extras.put("country_code", selectedCountry);
      } catch (JSONException var4) {
      }

      AccountKitController.initializer.getLogger().logImpression("ak_country_code_view", "phone", (String) null, extras, isPresented);
    }

    public static void logUIAccountVerified(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_account_verified_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", AccountKitController.getLastUsedPhoneNotificationChannelValue(), (JSONObject) null, isPresented);
    }

    public static void logUIConfirmAccountVerified(boolean isPresented, LoginType loginType) {
      AccountKitController.initializer.getLogger().logImpression("ak_confirm_account_verified_view", loginType.equals(LoginType.PHONE) ? "phone" : "email", (String) null, (JSONObject) null, isPresented);
    }
  }
}
