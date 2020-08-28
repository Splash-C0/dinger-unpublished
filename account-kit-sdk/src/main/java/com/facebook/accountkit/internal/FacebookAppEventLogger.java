package com.facebook.accountkit.internal;

import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class FacebookAppEventLogger {
  private static final String TAG = FacebookAppEventLogger.class.getCanonicalName();
  private static final String FB_EVENT_NAME_LOGIN_ATTEMPT = "fb_ak_login_attempt";
  private static final String FB_EVENT_NAME_LOGIN_COMPLETE = "fb_ak_login_complete";
  private static final String FB_EVENT_NAME_LOGIN_DIALOG_IMPRESSION = "fb_ak_login_dialog_impression";
  private static final String FB_EVENT_NAME_LOGIN_START = "fb_ak_login_start";
  private static final String FB_EVENT_NAME_LOGIN_CONFIRMATION_VIEW = "fb_ak_login_confirmation_view";
  private static final Map<String, String> eventNameMap = new HashMap<String, String>() {
    {
      this.put("ak_email_login_view", "fb_ak_login_dialog_impression");
      this.put("ak_phone_login_view", "fb_ak_login_dialog_impression");
      this.put("ak_login_start", "fb_ak_login_start");
      this.put("ak_confirmation_code_view", "fb_ak_login_confirmation_view");
      this.put("ak_account_verified_view", "fb_ak_login_confirmation_view");
      this.put("ak_email_sent_view", "fb_ak_login_confirmation_view");
      this.put("ak_login_verify", "fb_ak_login_attempt");
      this.put("ak_seamless_pending", "fb_ak_login_attempt");
      this.put("ak_login_complete", "fb_ak_login_complete");
    }
  };
  private Object fbAppEventLogger = null;

  FacebookAppEventLogger(Context applicationContext) {
    if (isFacebookSDKInitialized()) {
      try {
        Class fbAppEventClass = Class.forName("com.facebook.appevents.AppEventsLogger");

        try {
          Method logSDKEventMethod = fbAppEventClass.getMethod("newLogger", Context.class);

          try {
            this.fbAppEventLogger = logSDKEventMethod.invoke((Object) null, applicationContext);
          } catch (Exception var5) {
            Utility.logd(TAG, var5);
          }
        } catch (NoSuchMethodException var6) {
          Utility.logd(TAG, var6);
        }
      } catch (ClassNotFoundException var7) {
      }
    }

  }

  static boolean isFacebookSDKInitialized() {
    try {
      Class fbSDKClass = Class.forName("com.facebook.FacebookSdk");

      try {
        Method initializedMethod = fbSDKClass.getMethod("isInitialized");

        try {
          Object result = initializedMethod.invoke((Object) null);
          return (Boolean) result;
        } catch (Exception var3) {
          Utility.logd(TAG, var3);
        }
      } catch (NoSuchMethodException var4) {
        Utility.logd(TAG, var4);
      }
    } catch (ClassNotFoundException var5) {
    }

    return false;
  }

  public void logImpression(String eventName, Bundle parameters, boolean isPresented) {
    if (isPresented) {
      this.logFacebookAppEvents(eventName, (Double) null, parameters);
    }

  }

  void logFacebookAppEvents(String eventName, Double valueToSum, Bundle parameters) {
    String fbEventName = (String) eventNameMap.get(eventName);
    if (fbEventName != null) {
      if (this.fbAppEventLogger != null) {
        try {
          Method logSDKEventMethod = this.fbAppEventLogger.getClass().getMethod("logSdkEvent", String.class, Double.class, Bundle.class);

          try {
            logSDKEventMethod.invoke(this.fbAppEventLogger, fbEventName, valueToSum, parameters);
          } catch (Exception var7) {
            Utility.logd(TAG, var7);
          }
        } catch (NoSuchMethodException var8) {
          Utility.logd(TAG, var8);
        }
      }

    }
  }
}
