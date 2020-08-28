package com.facebook.accountkit.internal;

import android.content.Context;
import android.os.Bundle;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.EmailLoginModel;
import com.facebook.accountkit.PhoneLoginModel;

import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.UUID;

public final class InternalLogger {
  public static final String EVENT_NAME_SDK_START = "ak_sdk_init";
  public static final String EVENT_NAME_LOGIN_START = "ak_login_start";
  public static final String EVENT_NAME_UPDATE_START = "ak_update_start";
  public static final String EVENT_NAME_LOGIN_VERIFY = "ak_login_verify";
  public static final String EVENT_NAME_UPDATE_VERIFY = "ak_update_verify";
  public static final String EVENT_NAME_LOGIN_COMPLETE = "ak_login_complete";
  public static final String EVENT_NAME_UPDATE_COMPLETE = "ak_update_complete";
  public static final String EVENT_NAME_SET_CONFIRMATION_CODE = "ak_confirmation_code_set";
  public static final String EVENT_NAME_CONFIRM_SEAMLESS_PENDING = "ak_seamless_pending";
  public static final String EVENT_NAME_FETCH_SEAMLESS_LOGIN_TOKEN = "ak_fetch_seamless_login_token";
  public static final String EVENT_NAME_ACCOUNT_VERIFIED_VIEW = "ak_account_verified_view";
  public static final String EVENT_NAME_CONFIRM_ACCOUNT_VERIFIED_VIEW = "ak_confirm_account_verified_view";
  public static final String EVENT_NAME_CONFIRMATION_CODE_VIEW = "ak_confirmation_code_view";
  public static final String EVENT_NAME_COUNTRY_CODE_VIEW = "ak_country_code_view";
  public static final String EVENT_NAME_EMAIL_VERIFY_VIEW = "ak_email_sent_view";
  public static final String EVENT_NAME_EMAIL_VIEW = "ak_email_login_view";
  public static final String EVENT_NAME_ERROR_VIEW = "ak_error_view";
  public static final String EVENT_NAME_PHONE_NUMBER_VIEW = "ak_phone_login_view";
  public static final String EVENT_NAME_RESEND_VIEW = "ak_resend_view";
  public static final String EVENT_NAME_SENDING_CODE_VIEW = "ak_sending_code_view";
  public static final String EVENT_NAME_SENT_CODE_VIEW = "ak_sent_code_view";
  public static final String EVENT_NAME_VERIFIED_CODE_VIEW = "ak_verified_code_view";
  public static final String EVENT_NAME_VERIFYING_CODE_VIEW = "ak_verifying_code_view";
  public static final String EVENT_PARAM_EXTRAS_COUNTRY_CODE = "country_code";
  public static final String EVENT_PARAM_EXTRAS_COUNTRY_CODE_SOURCE = "country_code_source";
  public static final String EVENT_PARAM_EXTRAS_GET_ACCOUNTS_PERM = "get_accounts_perm";
  public static final String EVENT_PARAM_EXTRAS_READ_NUMBER_PERM = "read_phone_number_permission";
  public static final String EVENT_PARAM_EXTRAS_SIM_LOCALE = "sim_locale";
  public static final String EVENT_PARAM_EXTRAS_RETRY = "retry";
  public static final String EVENT_PARAM_LOGIN_TYPE_VALUE_EMAIL = "email";
  public static final String EVENT_PARAM_LOGIN_TYPE_VALUE_PHONE = "phone";
  public static final String EVENT_PARAM_TYPE_VALUE_PHONE_UPDATE = "phone_update";
  public static final String EVENT_PARAM_VIEW_STATE_DISMISSED = "dismissed";
  public static final String EVENT_PARAM_VIEW_STATE_PRESENTED = "presented";
  public static final String EVENT_PARAM_VERIFICATION_METHOD_CONFIRMATION_CODE = "confirmation_code";
  public static final String EVENT_PARAM_VERIFICATION_METHOD_INSTANT_VERIFICATION = "instant_verification";
  public static final String EVENT_PARAM_SDK_ANDROID = "Android";
  public static final String EVENT_PARAM_EXTRAS_FALSE = "false";
  public static final String EVENT_PARAM_EXTRAS_TRUE = "true";
  public static final String EVENT_VALUE_EMAIL_NOTIF_MEDIUM = "email";
  private static final String EVENT_PARAM_AUTH_LOGGER_ID = "0_logger_ref";
  private static final String EVENT_PARAM_TIMESTAMP = "1_timestamp_ms";
  private static final String EVENT_PARAM_STATE = "2_state";
  private static final String EVENT_PARAM_LOGIN_TYPE = "3_type";
  private static final String EVENT_PARAM_LOGIN_RESULT = "4_result";
  private static final String EVENT_PARAM_ERROR_CODE = "5_error_code";
  private static final String EVENT_PARAM_ERROR_MESSAGE = "6_error_message";
  private static final String EVENT_PARAM_EXTRAS = "7_extras";
  private static final String EVENT_PARAM_VIEW_STATE = "8_view_state";
  private static final String EVENT_PARAM_COUNTRY_CODE = "9_country_code";
  private static final String EVENT_PARAM_VERIFICATION_METHOD = "10_verification_method";
  private static final String EVENT_PARAM_SDK = "11_sdk";
  private static final String EVENT_PARAM_NOTIFICATION_TYPE = "12_notification_type";
  private static final String SAVED_LOGGING_REF = "accountkitLoggingRef";
  private final Context applicationContext;
  private final String applicationId;
  private final boolean facebookAppEventsEnabled;
  private String loggingRef;

  InternalLogger(Context applicationContext, String applicationId, boolean facebookAppEventsEnabled) {
    this.applicationContext = applicationContext;
    this.applicationId = applicationId;
    this.facebookAppEventsEnabled = facebookAppEventsEnabled;
    this.loggingRef = UUID.randomUUID().toString();
  }

  void saveInstanceState(Bundle outState) {
    outState.putString("accountkitLoggingRef", this.loggingRef);
  }

  void onActivityCreate(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      this.loggingRef = savedInstanceState.getString("accountkitLoggingRef");
    } else {
      this.loggingRef = UUID.randomUUID().toString();
    }

  }

  public void logImpression(String eventName, String loginType, String notificationType, JSONObject extras, boolean isPresented) {
    Bundle bundle = this.getAuthorizationLoggingBundle();
    bundle.putString("3_type", loginType);
    bundle.putString("8_view_state", isPresented ? "presented" : "dismissed");
    if (extras != null) {
      bundle.putString("7_extras", extras.toString());
    }

    if ("ak_account_verified_view".equals(eventName)) {
      bundle.putString("10_verification_method", "instant_verification");
      bundle.putString("12_notification_type", notificationType);
    } else if ("ak_confirmation_code_view".equals(eventName) || "ak_email_sent_view".equals(eventName)) {
      bundle.putString("10_verification_method", "confirmation_code");
      bundle.putString("12_notification_type", notificationType);
    }

    if (this.facebookAppEventsEnabled) {
      (new FacebookAppEventLogger(this.applicationContext)).logImpression(eventName, bundle, isPresented);
    }

  }

  public void logLoginModel(String eventName, LoginModelImpl loginModel) {
    if (loginModel != null) {
      Bundle bundle = this.getAuthorizationLoggingBundle();
      if (loginModel instanceof PhoneLoginModelImpl) {
        bundle.putString("3_type", "phone");
        bundle.putString("9_country_code", ((PhoneLoginModelImpl) loginModel).getPhoneNumber().getCountryCodeIso());
      } else {
        if (!(loginModel instanceof EmailLoginModelImpl)) {
          throw new InvalidParameterException("Unexpected loginModel type");
        }

        bundle.putString("3_type", "email");
      }

      bundle.putString("2_state", loginModel.getStatus().toString());
      AccountKitError error = loginModel.getError();
      if (error != null) {
        bundle.putString("5_error_code", Integer.toString(error.getErrorType().getCode()));
        bundle.putString("6_error_message", error.getErrorType().getMessage());
      }

      if (this.facebookAppEventsEnabled) {
        if (!"ak_seamless_pending".equals(eventName) && !"ak_fetch_seamless_login_token".equals(eventName)) {
          if (!"ak_login_verify".equals(eventName) && !"ak_login_complete".equals(eventName)) {
            if ("ak_login_start".equals(eventName)) {
              if (loginModel instanceof PhoneLoginModel) {
                if (((PhoneLoginModelImpl) loginModel).getNotificationChannel() != null) {
                  bundle.putString("12_notification_type", ((PhoneLoginModelImpl) loginModel).getNotificationChannel().toString());
                }
              } else if (loginModel instanceof EmailLoginModel) {
                bundle.putString("12_notification_type", "email");
              }
            }
          } else {
            bundle.putString("10_verification_method", "confirmation_code");
          }
        } else {
          bundle.putString("10_verification_method", "instant_verification");
        }

        FacebookAppEventLogger facebookAppEventLogger = new FacebookAppEventLogger(this.applicationContext);
        if (eventName.equals("ak_login_complete") && loginModel instanceof EmailLoginModelImpl) {
          facebookAppEventLogger.logFacebookAppEvents("ak_login_verify", (Double) null, bundle);
        }

        facebookAppEventLogger.logFacebookAppEvents(eventName, (Double) null, bundle);
      }

    }
  }

  String getLoggingRef() {
    return this.loggingRef;
  }

  public boolean getFacebookAppEventsEnabled() {
    return this.facebookAppEventsEnabled && FacebookAppEventLogger.isFacebookSDKInitialized();
  }

  private Bundle getAuthorizationLoggingBundle() {
    Bundle bundle = new Bundle();
    bundle.putLong("1_timestamp_ms", System.currentTimeMillis());
    bundle.putString("0_logger_ref", this.loggingRef == null ? "" : this.loggingRef);
    bundle.putString("2_state", "");
    bundle.putString("3_type", "");
    bundle.putString("4_result", "");
    bundle.putString("6_error_message", "");
    bundle.putString("8_view_state", "");
    bundle.putString("5_error_code", "");
    bundle.putString("11_sdk", "Android");
    bundle.putString("7_extras", "");
    bundle.putString("12_notification_type", "");
    return bundle;
  }
}
