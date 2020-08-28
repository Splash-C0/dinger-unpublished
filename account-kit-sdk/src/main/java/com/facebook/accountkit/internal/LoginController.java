package com.facebook.accountkit.internal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;

abstract class LoginController<E extends LoginModelImpl> {
  static final String GRAPH_PATH_LOGIN_START = "start_login";
  static final String GRAPH_PATH_LOGIN_REQUEST_STATUS = "poll_login";
  static final String GRAPH_PATH_LOGIN_REQUEST_CONFIRM = "confirm_login";
  static final String GRAPH_PATH_SEAMLESS_REQUEST_CONFIRM = "instant_verification_login";
  static final String PARAMETER_ARGUMENT_FACEBOOK = "facebook";
  static final String PARAMETER_ARGUMENT_SMS = "sms";
  static final String PARAMETER_ARGUMENT_WHATSAPP = "whatsapp";
  static final String PARAMETER_ARGUMENT_INFOBIP = "infobip";
  static final String PARAMETER_FIELDS_TYPE = "fields";
  static final String PARAMETER_REDIRECT_URI = "redirect_uri";
  static final String PARAMETER_STATE = "state";
  static final String PARAMETER_RESPONSE_TYPE = "response_type";
  static final String PARAMETER_FB_USER_TOKEN = "fb_user_token";
  private static final String TAG = LoginController.class.getName();
  private static final String PARAMETER_CREDENTIALS_TYPE = "credentials_type";
  private static final String PARAMETER_LOGGING_REF = "logging_ref";
  private static final String PARAMETER_LOGIN_REQUEST_CODE = "login_request_code";
  protected final E loginModel;
  final AccessTokenManager accessTokenManager;
  private final WeakReference<LoginManager> loginManagerRef;

  LoginController(@NonNull AccessTokenManager accessTokenManager, @NonNull LoginManager loginManager, @NonNull E loginModel) {
    this.accessTokenManager = accessTokenManager;
    this.loginManagerRef = new WeakReference(loginManager);
    this.loginModel = loginModel;
  }

  public E getLoginModel() {
    return this.loginModel;
  }

  public abstract void logIn(String var1);

  public abstract void onCancel();

  public void onError(AccountKitError error) {
    this.loginModel.setError(error);
    this.loginModel.setStatus(LoginStatus.ERROR);
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null) {
      loginManager.cancel(this.loginModel);
    }
  }

  public abstract void onPending();

  public abstract void onAccountVerified();

  void extractAccessTokenOrCodeIntoModel(JSONObject result) throws JSONException {
    String accessToken;
    String id;
    if (Utility.areObjectsEqual(this.loginModel.getResponseType(), "token")) {
      accessToken = result.getString("access_token");
      id = result.getString("id");
      String tokenRefreshIntervalString = result.getString("token_refresh_interval_sec");
      long tokenRefreshIntervalInSeconds = Long.parseLong(tokenRefreshIntervalString);
      AccessToken token = new AccessToken(accessToken, id, AccountKit.getApplicationId(), tokenRefreshIntervalInSeconds, new Date());
      this.accessTokenManager.setCurrentAccessToken(token);
      String finalAuthState = result.optString("state");
      this.loginModel.setFinalAuthState(finalAuthState);
      this.loginModel.setAccessToken(token);
      this.loginModel.setStatus(LoginStatus.SUCCESS);
    } else {
      accessToken = result.getString("code");
      this.loginModel.setCode(accessToken);
      id = result.optString("state");
      this.loginModel.setFinalAuthState(id);
      this.loginModel.setStatus(LoginStatus.SUCCESS);
    }

  }

  AccountKitGraphRequest buildGraphRequest(String graphPath, Bundle extraParameters) {
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "credentials_type", this.getCredentialsType());
    Utility.putNonNullString(parameters, "login_request_code", this.loginModel.getLoginRequestCode());
    Utility.putNonNullString(parameters, "logging_ref", this.getLoginManager() != null ? this.getLoginManager().getLogger().getLoggingRef() : null);
    parameters.putAll(extraParameters);
    return new AccountKitGraphRequest((AccessToken) null, graphPath, parameters, this.isLoginRequestPath(graphPath), HttpMethod.POST);
  }

  protected abstract String getCredentialsType();

  protected abstract String getLoginStateChangedIntentName();

  LoginManager getLoginManager() {
    LoginManager loginManager = (LoginManager) this.loginManagerRef.get();
    if (loginManager == null) {
      return null;
    } else if (!loginManager.isActivityAvailable()) {
      Log.w(TAG, "Warning: Callback issues while activity not available.");
      return null;
    } else {
      return loginManager;
    }
  }

  protected void onError(AccountKitError.Type errorType, InternalAccountKitError internalError) {
    this.onError(new AccountKitError(errorType, internalError));
  }

  void broadcastLoginStateChange() {
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null) {
      loginManager.getLocalBroadcastManager().sendBroadcast((new Intent(this.getLoginStateChangedIntentName())).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_MODEL", this.loginModel).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_STATUS", this.loginModel.getStatus()).putExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_ERROR", this.loginModel.getError()));
    }
  }

  private boolean isLoginRequestPath(String requestPath) {
    return Utility.areObjectsEqual(requestPath, "start_login") || Utility.areObjectsEqual(requestPath, "poll_login") || Utility.areObjectsEqual(requestPath, "confirm_login");
  }

  class AccountVerifedCallback implements AccountKitGraphRequest.Callback {
    final LoginManager loginManager;

    AccountVerifedCallback(LoginManager loginManager) {
      this.loginManager = loginManager;
    }

    public void onCompleted(AccountKitGraphResponse response) {
      if (!this.loginManager.isActivityAvailable()) {
        Log.w(LoginController.TAG, "Warning: Callback issues while activity not available.");
      } else {
        try {
          if (response.getError() == null) {
            JSONObject result = response.getResponseObject();

            try {
              LoginController.this.extractAccessTokenOrCodeIntoModel(result);
            } catch (JSONException var8) {
              LoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
            }

            return;
          }

          Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
          LoginController.this.onError((AccountKitError) error.first);
        } finally {
          LoginController.this.broadcastLoginStateChange();
          this.loginManager.onLoginComplete(LoginController.this.loginModel);
          if (LoginController.this.loginModel.getStatus() == LoginStatus.SUCCESS || LoginController.this.loginModel.getStatus() == LoginStatus.ERROR) {
            this.loginManager.clearLogIn();
          }

        }

      }
    }
  }
}
