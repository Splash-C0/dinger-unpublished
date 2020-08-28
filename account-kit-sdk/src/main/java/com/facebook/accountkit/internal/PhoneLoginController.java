package com.facebook.accountkit.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

final class PhoneLoginController extends LoginController<PhoneLoginModelImpl> {
  private static final String TAG = PhoneLoginController.class.getName();
  private static final String PARAMETER_CONFIRMATION_CODE = "confirmation_code";
  private static final String PARAMETER_PHONE = "phone_number";
  private static final String PARAMETER_USER_TOKEN = "fb_user_token";

  PhoneLoginController(AccessTokenManager accessTokenManager, LoginManager loginManager, PhoneLoginModelImpl loginModel) {
    super(accessTokenManager, loginManager, loginModel);
  }

  @Nullable
  private static String createSmsToken(Context context) {
    String appSpecificSmsToken = null;
    if (Utility.hasGooglePlayServices(context)) {
      String packageHash = PackageUtils.computePackageHash(context, context.getPackageName());
      appSpecificSmsToken = packageHash.substring(0, 11);
      SmsRetrieverClient client = SmsRetriever.getClient(context);
      client.startSmsRetriever();
    }

    return appSpecificSmsToken;
  }

  protected String getCredentialsType() {
    return "phone_number";
  }

  protected String getLoginStateChangedIntentName() {
    return "com.facebook.accountkit.sdk.ACTION_PHONE_LOGIN_STATE_CHANGED";
  }

  public void logIn(@Nullable String initialAuthState) {
    AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
      public void onCompleted(AccountKitGraphResponse response) {
        LoginManager loginManager = PhoneLoginController.this.getLoginManager();
        if (loginManager != null && response != null) {
          try {
            if (response.getError() == null) {
              JSONObject result = response.getResponseObject();
              if (result == null) {
                PhoneLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
                return;
              }

              String privacyPolicy = result.optString("privacy_policy");
              if (!Utility.isNullOrEmpty(privacyPolicy)) {
                ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).putField("privacy_policy", privacyPolicy);
              }

              String termsOfService = result.optString("terms_of_service");
              if (!Utility.isNullOrEmpty(termsOfService)) {
                ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).putField("terms_of_service", termsOfService);
              }

              String expiresInString;
              long expiresIn;
              try {
                boolean canSkipCode = result.getBoolean("can_attempt_seamless_login");
                expiresInString = result.getString("expires_at");
                expiresIn = Long.parseLong(expiresInString) * 1000L;
                if (canSkipCode && expiresIn > System.currentTimeMillis()) {
                  ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setStatus(LoginStatus.ACCOUNT_VERIFIED);
                  return;
                }
              } catch (JSONException var18) {
              }

              try {
                String loginModelCode = result.getString("login_request_code");
                expiresInString = result.getString("expires_in_sec");
                expiresIn = Long.parseLong(expiresInString);
                ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setExpiresInSeconds(expiresIn);
                String minResendIntervalSecString = result.optString("min_resend_interval_sec");
                if (!Utility.isNullOrEmpty(minResendIntervalSecString)) {
                  long minResendIntervalSec = Long.parseLong(minResendIntervalSecString);
                  ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setResendTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(minResendIntervalSec));
                } else {
                  ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setResendTime(System.currentTimeMillis());
                }

                ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setStatus(LoginStatus.PENDING);
                ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setLoginCode(loginModelCode);
              } catch (NumberFormatException | JSONException var17) {
                PhoneLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
              }

              return;
            }

            Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
            PhoneLoginController.this.onError((AccountKitError) error.first);
          } finally {
            PhoneLoginController.this.broadcastLoginStateChange();
          }

        }
      }
    };
    String phoneNumberString = ((PhoneLoginModelImpl) this.loginModel).getPhoneNumber().toString();
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "phone_number", phoneNumberString);
    Utility.putNonNullString(parameters, "state", initialAuthState);
    Utility.putNonNullString(parameters, "response_type", ((PhoneLoginModelImpl) this.loginModel).getResponseType());
    Utility.putNonNullString(parameters, "fields", "terms_of_service,privacy_policy");
    switch (((PhoneLoginModelImpl) this.loginModel).getNotificationChannel()) {
      case FACEBOOK:
        Utility.putNonNullString(parameters, "notif_medium", "facebook");
        break;
      case SMS:
        Utility.putNonNullString(parameters, "notif_medium", "sms");
        Utility.putNonNullString(parameters, "sms_provider", ((PhoneLoginModelImpl) this.loginModel).getTestSmsWithInfobip() ? "infobip" : "facebook");
        break;
      case WHATSAPP:
        Utility.putNonNullString(parameters, "notif_medium", "whatsapp");
    }

    String appSpecificSmsToken = createSmsToken(AccountKitController.getApplicationContext());
    if (appSpecificSmsToken != null) {
      Utility.putNonNullString(parameters, "sms_token", appSpecificSmsToken);
    }

    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null && !loginManager.isSeamlessLoginRunning()) {
      Utility.putNonNullString(parameters, "fb_user_token", loginManager.getSeamlessLoginToken());
    }

    ((PhoneLoginModelImpl) this.loginModel).setInitialAuthState(initialAuthState);
    AccountKitGraphRequest request = this.buildGraphRequest("start_login", parameters);
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
    AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
    AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
  }

  public void onCancel() {
    ((PhoneLoginModelImpl) this.loginModel).setStatus(LoginStatus.CANCELLED);
    this.broadcastLoginStateChange();
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
  }

  public void onPending() {
    if (!Utility.isNullOrEmpty(((PhoneLoginModelImpl) this.loginModel).getConfirmationCode())) {
      Validate.loginModelInProgress(this.loginModel);
      final LoginManager loginManager = this.getLoginManager();
      if (loginManager != null) {
        loginManager.onLoginVerify(this.loginModel);
        AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
          public void onCompleted(AccountKitGraphResponse response) {
            if (!loginManager.isActivityAvailable()) {
              Log.w(PhoneLoginController.TAG, "Warning: Callback issues while activity not available.");
            } else if (response != null) {
              Pair error = null;

              try {
                if (response.getError() != null) {
                  error = Utility.createErrorFromServerError(response.getError());
                  if (!Utility.isConfirmationCodeRetryable((InternalAccountKitError) error.second)) {
                    PhoneLoginController.this.onError((AccountKitError) error.first);
                  }

                  return;
                }

                JSONObject result = response.getResponseObject();
                if (result == null) {
                  PhoneLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
                  return;
                }

                try {
                  PhoneLoginController.this.extractAccessTokenOrCodeIntoModel(result);
                } catch (NumberFormatException | JSONException var8) {
                  PhoneLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
                }
              } finally {
                if (((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).getStatus() == LoginStatus.ERROR && error != null && Utility.isConfirmationCodeRetryable((InternalAccountKitError) error.second)) {
                  ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setStatus(LoginStatus.PENDING);
                  ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).setError((AccountKitError) null);
                }

                PhoneLoginController.this.broadcastLoginStateChange();
                loginManager.onLoginComplete(PhoneLoginController.this.loginModel);
                if (((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).getStatus() == LoginStatus.SUCCESS || ((PhoneLoginModelImpl) PhoneLoginController.this.loginModel).getStatus() == LoginStatus.ERROR) {
                  loginManager.clearLogIn();
                }

              }

            }
          }
        };
        Bundle parameters = new Bundle();
        Utility.putNonNullString(parameters, "confirmation_code", ((PhoneLoginModelImpl) this.loginModel).getConfirmationCode());
        Utility.putNonNullString(parameters, "phone_number", ((PhoneLoginModelImpl) this.loginModel).getPhoneNumber().toString());
        AccountKitGraphRequest request = this.buildGraphRequest("confirm_login", parameters);
        AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
        AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
        AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
      }
    }
  }

  public void onAccountVerified() {
    Validate.loginModelInProgress(this.loginModel);
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null) {
      loginManager.onSeamlessLoginPending(this.loginModel);
      LoginController<PhoneLoginModelImpl>.AccountVerifedCallback requestCallback = new LoginController.AccountVerifedCallback(loginManager);
      Bundle parameters = new Bundle();
      Utility.putNonNullString(parameters, "fb_user_token", loginManager.getSeamlessLoginTokenRegardlessTimeOut());
      Utility.putNonNullString(parameters, "phone_number", ((PhoneLoginModelImpl) this.loginModel).getPhoneNumber().toString());
      Utility.putNonNullString(parameters, "response_type", ((PhoneLoginModelImpl) this.loginModel).getResponseType());
      Utility.putNonNullString(parameters, "state", ((PhoneLoginModelImpl) this.loginModel).getInitialAuthState());
      AccountKitGraphRequest request = this.buildGraphRequest("instant_verification_login", parameters);
      AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
      AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
    }
  }
}
