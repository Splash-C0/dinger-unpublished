package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.LoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.Tracker;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.InternalAccountKitError;
import com.facebook.accountkit.internal.Utility;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public final class AccountKitActivity extends AccountKitActivityBase {
  private static final String TAG = AccountKitActivity.class.getSimpleName();
  private static final String LOGIN_FLOW_MANAGER_KEY;
  private static final String PENDING_LOGIN_FLOW_STATE_KEY;
  private static final String TRACKING_SMS_KEY;
  private static final IntentFilter LOGIN_FLOW_BROADCAST_RECEIVER_FILTER;
  public static final int RESULT_CODE_PHONE_FORMATTING_FAILED = 5;
  public static final String EXTRA_EXCEPTION = "exception";

  static {
    LOGIN_FLOW_MANAGER_KEY = TAG + ".loginFlowManager";
    PENDING_LOGIN_FLOW_STATE_KEY = TAG + ".pendingLoginFlowState";
    TRACKING_SMS_KEY = TAG + ".trackingSms";
    LOGIN_FLOW_BROADCAST_RECEIVER_FILTER = LoginFlowBroadcastReceiver.getIntentFilter();
  }

  private final Bundle viewState;
  private final BroadcastReceiver loginFlowBroadcastReceiver;
  private GoogleApiClient mCredentialsApiClient;
  private AccessToken accessToken;
  private String authorizationCode;
  private Tracker loginTracker;
  private AccountKitError error;
  private String finalAuthState;
  private boolean isActive;
  @Nullable
  private LoginFlowManager loginFlowManager;
  private LoginResult result;
  private StateStackManager stateStackManager;
  private long tokenRefreshIntervalInSeconds;

  public AccountKitActivity() {
    this.result = LoginResult.CANCELLED;
    this.viewState = new Bundle();
    this.loginFlowBroadcastReceiver = new LoginFlowBroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (LoginFlowBroadcastReceiver.ACTION_UPDATE.contentEquals(action)) {
          LoginFlowBroadcastReceiver.Event event = (LoginFlowBroadcastReceiver.Event) intent.getSerializableExtra(EXTRA_EVENT);
          ContentController contentController = AccountKitActivity.this.stateStackManager.getContentController();
          PhoneNumber phoneNumber;
          PhoneLoginFlowManager managerx;
          NotificationChannel notificationChannel;
          String confirmationCode;
          switch (event) {
            case SENT_CODE_COMPLETE:
              AccountKitActivity.this.loginFlowManager.getActivityHandler().onSentCodeComplete(AccountKitActivity.this);
              break;
            case ACCOUNT_VERIFIED_COMPLETE:
              AccountKitActivity.this.loginFlowManager.getActivityHandler().onAccountVerifiedComplete(AccountKitActivity.this);
              break;
            case CONFIRM_SEAMLESS_LOGIN:
              AccountKitActivity.this.loginFlowManager.getActivityHandler().onConfirmSeamlessLogin(AccountKitActivity.this, AccountKitActivity.this.loginFlowManager);
              break;
            case EMAIL_LOGIN_COMPLETE:
              if (contentController instanceof EmailLoginContentController) {
                confirmationCode = intent.getStringExtra(EXTRA_EMAIL);
                EmailLoginFlowManager manager = (EmailLoginFlowManager) AccountKitActivity.this.loginFlowManager;
                ((ActivityEmailHandler) manager.getActivityHandler()).onEmailLoginComplete(AccountKitActivity.this, manager, confirmationCode);
              }
              break;
            case EMAIL_VERIFY_RETRY:
              if (contentController instanceof EmailVerifyContentController) {
                ((ActivityEmailHandler) AccountKitActivity.this.loginFlowManager.getActivityHandler()).onEmailVerifyRetry(AccountKitActivity.this);
              }
              break;
            case ERROR_RESTART:
              if (contentController instanceof LoginErrorContentController) {
                LoginFlowState returnState = LoginFlowState.values()[intent.getIntExtra(EXTRA_RETURN_LOGIN_FLOW_STATE, 0)];
                ActivityErrorHandler.onErrorRestart(AccountKitActivity.this, returnState);
              }
              break;
            case PHONE_LOGIN_COMPLETE:
              if (contentController instanceof PhoneLoginContentController) {
                phoneNumber = (PhoneNumber) intent.getParcelableExtra(EXTRA_PHONE_NUMBER);
                if (phoneNumber != null) {
                  InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                  //Find the currently focused view, so we can grab the correct window token from it.
                  View view = getCurrentFocus();
                  //If no view currently has focus, create a new one, just so we can grab a window token from it
                  if (view == null) {
                    view = new View(AccountKitActivity.this);
                  }
                  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                  final Phonenumber.PhoneNumber parsedPhoneNumber;
                  try {
                    parsedPhoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumber.toRtlSafeString(), null);
                    final CharSequence formattedPhoneNumber = PhoneNumberUtil.getInstance().format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                    AccountKitActivity.this.setResult(RESULT_OK, new Intent().putExtra(LoginFlowBroadcastReceiver.EXTRA_PHONE_NUMBER, formattedPhoneNumber));
                  } catch (final NumberParseException e) {
                    AccountKitActivity.this.setResult(RESULT_CODE_PHONE_FORMATTING_FAILED, new Intent().putExtra(EXTRA_EXCEPTION, e));
                  } finally {
                    AccountKitActivity.this.supportFinishAfterTransition();
                  }
                }
              }
              break;
            case PHONE_CONFIRMATION_CODE_COMPLETE:
              if (contentController instanceof LoginConfirmationCodeContentController) {
                confirmationCode = intent.getStringExtra(EXTRA_CONFIRMATION_CODE);
                String stringPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
                managerx = (PhoneLoginFlowManager) AccountKitActivity.this.loginFlowManager;
                ((ActivityPhoneHandler) managerx.getActivityHandler()).onConfirmationCodeInputComplete(AccountKitActivity.this, managerx, stringPhoneNumber, confirmationCode);
              }
              break;
            case PHONE_CONFIRMATION_CODE_RETRY:
              if (contentController instanceof LoginConfirmationCodeContentController) {
                ((ActivityPhoneHandler) AccountKitActivity.this.loginFlowManager.getActivityHandler()).onConfirmationCodeRetry(AccountKitActivity.this);
              }
              break;
            case PHONE_RESEND:
              if (contentController instanceof ResendContentController || contentController instanceof LoginConfirmationCodeContentController) {
                ((ActivityPhoneHandler) AccountKitActivity.this.loginFlowManager.getActivityHandler()).onResend(AccountKitActivity.this);
              }
              break;
            case PHONE_RESEND_FACEBOOK_NOTIFICATION:
              if (contentController instanceof ResendContentController) {
                PhoneLoginFlowManager managerxx = (PhoneLoginFlowManager) AccountKitActivity.this.loginFlowManager;
                ((ActivityPhoneHandler) managerxx.getActivityHandler()).onResendFacebookNotification(AccountKitActivity.this, managerxx);
              }
              break;
            case PHONE_RESEND_SWITCH:
              if (contentController instanceof ResendContentController) {
                phoneNumber = (PhoneNumber) intent.getParcelableExtra(EXTRA_PHONE_NUMBER);
                managerx = (PhoneLoginFlowManager) AccountKitActivity.this.loginFlowManager;
                notificationChannel = (NotificationChannel) intent.getSerializableExtra(EXTRA_NOTIFICATION_CHANNEL);
                ((ActivityPhoneHandler) managerx.getActivityHandler()).onResendSwitchLoginMethod(AccountKitActivity.this, managerx, phoneNumber, notificationChannel);
              }
          }

        }
      }
    };
  }

  private static boolean urlIsRedirectUrl(@NonNull String url) {
    return url.startsWith(Utility.getRedirectURL());
  }

  public GoogleApiClient getGoogleApiClient() {
    return this.mCredentialsApiClient;
  }

  @Nullable
  public LoginFlowState getCurrentState() {
    return this.loginFlowManager != null ? this.loginFlowManager.getFlowState() : null;
  }

  ContentController getContentController() {
    return this.stateStackManager.getContentController();
  }

  public void onBackPressed() {
    if (this.stateStackManager.getContentController() == null) {
      super.onBackPressed();
    } else {
      this.backPressed();
    }

  }

  public void onBackPressed(View view) {
    this.onBackPressed();
  }

  public void onCancelPressed(View view) {
    this.sendCancelResult();
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = this.getIntent();
    String url = intent.getDataString();
    if (url != null && !urlIsRedirectUrl(url)) {
      this.sendResult();
    } else if (this.configuration != null && this.configuration.getLoginType() != null) {
      if (this.configuration.getResponseType() == null) {
        this.error = new AccountKitError(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.INVALID_INTENT_EXTRAS_RESPONSE_TYPE);
        this.sendResult();
      } else {
        this.stateStackManager = new StateStackManager(this, this.configuration);
        AccountKit.onActivityCreate(this, savedInstanceState);
        this.onViewReadyWithState(this.viewState, savedInstanceState != null);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.loginFlowBroadcastReceiver, LOGIN_FLOW_BROADCAST_RECEIVER_FILTER);
        this.mCredentialsApiClient = (new Builder(this)).addApi(Auth.CREDENTIALS_API).build();
      }
    } else {
      this.error = new AccountKitError(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.INVALID_INTENT_EXTRAS_LOGIN_TYPE);
      this.sendResult();
    }
    loginFlowManager.activityHandler = new ActivityPhoneHandler(configuration);
  }

  protected void onStart() {
    super.onStart();
    this.mCredentialsApiClient.connect();
  }

  protected void onStop() {
    super.onStop();
    this.mCredentialsApiClient.disconnect();
    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window token from it.
    View view = getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
      view = new View(this);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ContentController contentController = this.getContentController();
    if (contentController != null) {
      contentController.onActivityResult(requestCode, resultCode, data);
    }

  }

  public void onPause() {
    super.onPause();
    ContentController contentController = this.getContentController();
    if (contentController != null) {
      contentController.onPause(this);
    }

    this.isActive = false;
  }

  public void onSaveInstanceState(Bundle outState) {
    AccountKit.onActivitySaveInstanceState(this, outState);
    if (this.loginFlowManager.getLoginType() == LoginType.PHONE) {
      ActivityPhoneHandler phoneHandler = (ActivityPhoneHandler) this.loginFlowManager.getActivityHandler();
      this.viewState.putBoolean(TRACKING_SMS_KEY, phoneHandler.isSmsTracking());
      phoneHandler.pauseSmsTracker();
      this.viewState.putParcelable(LOGIN_FLOW_MANAGER_KEY, this.loginFlowManager);
    }

    if (this.loginTracker != null) {
      this.loginTracker.pauseTracking();
    }

    super.onSaveInstanceState(outState);
  }

  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    String url = intent.getDataString();
    if (url != null) {
      if (!urlIsRedirectUrl(url)) {
        this.sendResult();
      } else {
        if (this.getContentController() instanceof EmailVerifyContentController) {
          this.pushState(LoginFlowState.VERIFYING_CODE, (StateStackManager.OnPushListener) null);
        }

      }
    }
  }

  private void onViewReadyWithState(Bundle viewState, boolean restored) {
    this.setNewLoginFlowManagerAndHandler((LoginFlowManager) viewState.getParcelable(LOGIN_FLOW_MANAGER_KEY));
    if (restored) {
      this.stateStackManager.updateContentController(this);
    } else {
      if (this.configuration == null) {
        return;
      }

      switch (this.configuration.getLoginType()) {
        case PHONE:
          this.pushState(LoginFlowState.PHONE_NUMBER_INPUT, (StateStackManager.OnPushListener) null);
          break;
        case EMAIL:
          this.pushState(LoginFlowState.EMAIL_INPUT, (StateStackManager.OnPushListener) null);
          break;
        default:
          this.error = new AccountKitError(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.INVALID_LOGIN_TYPE);
          this.sendResult();
      }
    }

  }

  protected void onResume() {
    super.onResume();
    ContentController contentController = this.getContentController();
    if (contentController != null) {
      contentController.onResume(this);
    }

    this.isActive = true;
    if (this.configuration != null) {
      switch (this.configuration.getLoginType()) {
        case PHONE:
        case EMAIL:
          this.loginTracker = this.loginFlowManager.getActivityHandler().getLoginTracker(this);
          this.loginTracker.startTracking();
      }

      if (this.loginFlowManager.getLoginType() == LoginType.PHONE && (this.loginFlowManager.getFlowState() == LoginFlowState.SENDING_CODE || this.viewState.getBoolean(TRACKING_SMS_KEY, false))) {
        ((ActivityPhoneHandler) this.loginFlowManager.getActivityHandler()).startSmsTrackerIfPossible(this);
      }

      String pending = this.viewState.getString(PENDING_LOGIN_FLOW_STATE_KEY);
      if (!Utility.isNullOrEmpty(pending)) {
        this.viewState.putString(PENDING_LOGIN_FLOW_STATE_KEY, (String) null);
        LoginFlowState loginFlowState = LoginFlowState.valueOf(pending);
        this.pushState(loginFlowState, (StateStackManager.OnPushListener) null);
      }

    }
  }

  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(this.loginFlowBroadcastReceiver);
    super.onDestroy();
    if (this.loginTracker != null) {
      this.loginTracker.stopTracking();
      this.loginTracker = null;
    }

    if (this.loginFlowManager != null && this.loginFlowManager.getLoginType() == LoginType.PHONE) {
      ((ActivityPhoneHandler) this.loginFlowManager.getActivityHandler()).stopSmsTracker();
    }

    AccountKit.onActivityDestroy(this);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case 16908332:
        this.onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case 4:
        this.backPressed();
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  void sendCancelResult() {
    this.sendResult(0, new AccountKitLoginResultImpl((AccessToken) null, (String) null, (String) null, 0L, (AccountKitError) null, true));
  }

  void sendResult() {
    int resultCode = this.result == LoginResult.SUCCESS ? -1 : 0;
    this.sendResult(resultCode, new AccountKitLoginResultImpl(this.accessToken, this.authorizationCode, this.finalAuthState, this.tokenRefreshIntervalInSeconds, this.error, false));
  }

  private void sendResult(int resultCode, AccountKitLoginResult loginResult) {
    if (this.getCallingActivity() == null) {
      PackageManager pm = this.getPackageManager();
      Intent intent = pm.getLaunchIntentForPackage(this.getPackageName());
      this.startActivity(intent);
      this.finish();
    } else {
      Intent data = new Intent();
      data.putExtra("account_kit_log_in_result", loginResult);
      this.setResult(resultCode, data);
      this.finish();
    }
  }

  void setAuthorizationCode(String authorizationCode) {
    this.authorizationCode = authorizationCode;
  }

  void setFinalAuthState(String finalAuthState) {
    this.finalAuthState = finalAuthState;
  }

  void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  void setTokenRefreshIntervalInSeconds(long tokenRefreshIntervalInSeconds) {
    this.tokenRefreshIntervalInSeconds = tokenRefreshIntervalInSeconds;
  }

  void setLoginResult(LoginResult result) {
    this.result = result;
  }

  private void backPressed() {
    ContentController contentController = this.stateStackManager.getContentController();
    if (contentController != null) {
      if (contentController instanceof LoginConfirmationCodeContentController) {
        ((LoginConfirmationCodeContentController) contentController).setRetry(false);
      }

      this.onContentControllerDismissed(contentController);
      LoginFlowState fromState = contentController.getLoginFlowState();
      LoginFlowState toState = LoginFlowState.getBackState(fromState);
      switch (fromState) {
        case NONE:
        case PHONE_NUMBER_INPUT:
        case EMAIL_INPUT:
          this.sendCancelResult();
          break;
        case SENDING_CODE:
        case SENT_CODE:
        case CODE_INPUT:
        case ACCOUNT_VERIFIED:
        case CONFIRM_ACCOUNT_VERIFIED:
        case CONFIRM_INSTANT_VERIFICATION_LOGIN:
        case EMAIL_VERIFY:
        case VERIFYING_CODE:
        case RESEND:
          this.resetFlowTo(fromState, toState);
          break;
        case ERROR:
          LoginFlowState returnState = ((LoginErrorContentController) contentController).getReturnState();
          this.resetFlowTo(fromState, returnState);
          break;
        case VERIFIED:
          this.sendResult();
          break;
        default:
          this.resetFlowTo(fromState, LoginFlowState.NONE);
      }

    }
  }

  private void resetFlowTo(LoginFlowState fromState, LoginFlowState toState) {
    this.loginFlowManager.setFlowState(toState);
    StateStackManager.OnPopListener onPopListener = new StateStackManager.OnPopListener() {
      public void onContentPopped() {
        AccountKitActivity.this.getContentController().onResume(AccountKitActivity.this);
      }
    };
    if (fromState != LoginFlowState.RESEND) {
      this.setNewLoginFlowManagerAndHandler((LoginFlowManager) null);
    }

    this.popBackStack(toState, onPopListener);
  }

  void setNewLoginFlowManagerAndHandler(LoginFlowManager restoredLoginFlowManager) {
    LoginFlowState existingState = this.loginFlowManager == null ? LoginFlowState.NONE : this.loginFlowManager.getFlowState();
    if (restoredLoginFlowManager == null && this.loginFlowManager != null) {
      this.loginFlowManager.cancel();
    }

    switch (this.configuration.getLoginType()) {
      case PHONE:
        this.loginFlowManager = new PhoneLoginFlowManager(this.configuration);
        this.loginFlowManager.setFlowState(existingState);
        break;
      case EMAIL:
        this.loginFlowManager = new EmailLoginFlowManager(this.configuration);
        this.loginFlowManager.setFlowState(existingState);
    }

  }

  void popBackStack(@NonNull LoginFlowState loginFlowState, @Nullable StateStackManager.OnPopListener onPopListener) {
    if (this.isActive) {
      this.stateStackManager.popBackStack(loginFlowState, onPopListener);
    }
  }

  void multiPopBackStack(@Nullable StateStackManager.OnPopListener onPopListener) {
    if (this.isActive) {
      this.stateStackManager.multiPopBackStack(onPopListener);
    }
  }

  void pushError(@Nullable AccountKitError error) {
    String userFacingError = error == null ? null : error.getUserFacingMessage();
    this.error = error;
    LoginFlowState returnState = LoginFlowState.getBackState(this.loginFlowManager.getFlowState());
    this.loginFlowManager.setFlowState(LoginFlowState.ERROR);
    this.stateStackManager.pushError(this, this.loginFlowManager, returnState, error, this.stateStackManager.getErrorOnPushListener(userFacingError));
  }

  void pushState(LoginFlowState loginFlowState, @Nullable StateStackManager.OnPushListener onPushListener) {
    if (this.isActive) {
      this.loginFlowManager.setFlowState(loginFlowState);
      if (onPushListener == null) {
        switch (loginFlowState) {
          case CODE_INPUT:
            onPushListener = ((ActivityPhoneHandler) this.loginFlowManager.getActivityHandler()).getConfirmationCodePushListener(this);
            break;
          case ERROR:
            this.pushError((AccountKitError) null);
            return;
        }
      }

      this.stateStackManager.pushState(this, this.loginFlowManager, onPushListener);
    } else {
      this.viewState.putString(PENDING_LOGIN_FLOW_STATE_KEY, loginFlowState.name());
    }

    if (!loginFlowState.equals(LoginFlowState.ERROR)) {
      this.error = null;
    }

  }

  void onContentControllerDismissed(ContentController contentController) {
    if (contentController != null) {
      contentController.onPause(this);
      this.logContentControllerDismissed(contentController);
    }

  }

  private void logContentControllerDismissed(ContentController contentController) {
    if (this.configuration != null) {
      if (contentController instanceof PhoneLoginContentController) {
        AccountKitController.Logger.logUIPhoneLogin();
      } else if (contentController instanceof SendingCodeContentController) {
        AccountKitController.Logger.logUISendingCode(false, this.configuration.getLoginType());
      } else if (contentController instanceof SentCodeContentController) {
        AccountKitController.Logger.logUISentCode(false, this.configuration.getLoginType());
      } else if (contentController instanceof LoginConfirmationCodeContentController) {
        AccountKitController.Logger.logUIConfirmationCode();
      } else if (contentController instanceof VerifyingCodeContentController) {
        AccountKitController.Logger.logUIVerifyingCode(false, this.configuration.getLoginType());
      } else if (contentController instanceof VerifiedCodeContentController) {
        AccountKitController.Logger.logUIVerifiedCode(false, this.configuration.getLoginType());
      } else if (contentController instanceof LoginErrorContentController) {
        AccountKitController.Logger.logUIError(false, this.configuration.getLoginType());
      } else if (contentController instanceof EmailLoginContentController) {
        AccountKitController.Logger.logUIEmailLogin();
      } else if (contentController instanceof EmailVerifyContentController) {
        AccountKitController.Logger.logUIEmailVerify(false);
      } else if (contentController instanceof ResendContentController) {
        AccountKitController.Logger.logUIResend(false);
      } else if (contentController instanceof ConfirmAccountVerifiedContentController) {
        AccountKitController.Logger.logUIConfirmAccountVerified(false, this.configuration.getLoginType());
      } else {
        if (!(contentController instanceof AccountVerifiedContentController)) {
          throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.UNEXPECTED_FRAGMENT, contentController.getClass().getName());
        }

        AccountKitController.Logger.logUIAccountVerified(false, this.configuration.getLoginType());
      }

    }
  }

  public static enum ResponseType {
    CODE("code"),
    TOKEN("token");

    private final String value;

    private ResponseType(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static enum TitleType {
    /**
     * @deprecated
     */
    @Deprecated
    APP_NAME,
    /**
     * @deprecated
     */
    @Deprecated
    LOGIN;

    private TitleType() {
    }
  }
}
