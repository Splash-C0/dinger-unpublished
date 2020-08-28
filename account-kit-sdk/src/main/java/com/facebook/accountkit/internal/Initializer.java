package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;

import java.lang.reflect.Method;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public final class Initializer {
  private final ArrayList<AccountKit.InitializeCallback> callbacks = new ArrayList();
  private volatile Initializer.Data data = null;
  private volatile Initializer.State state;

  public Initializer() {
    this.state = Initializer.State.UNINITIALIZED;
  }

  private static String getRequiredString(Bundle bundle, String key, InternalAccountKitError invalidValueError) throws AccountKitException {
    String value = bundle.getString(key);
    if (value == null) {
      throw new AccountKitException(AccountKitError.Type.INITIALIZATION_ERROR, invalidValueError);
    } else {
      return value;
    }
  }

  private static void fixSamsungClipboardUIManagerMemoryLeak(Context applicationContext) {
    if (VERSION.SDK_INT >= 21) {
      try {
        Class<?> cls = Class.forName("android.sec.clipboard.ClipboardUIManager");
        Method m = cls.getDeclaredMethod("getInstance", Context.class);
        m.setAccessible(true);
        m.invoke((Object) null, applicationContext);
      } catch (Exception var3) {
      }
    }

  }

  public synchronized void initialize(@NonNull Context context, AccountKit.InitializeCallback callback) throws AccountKitException {
    if (this.isInitialized()) {
      if (callback != null) {
        callback.onInitialized();
      }

    } else {
      if (callback != null) {
        this.callbacks.add(callback);
      }

      Validate.checkInternetPermissionAndThrow(context);
      Context applicationContext = context.getApplicationContext();
      fixSamsungClipboardUIManagerMemoryLeak(applicationContext);
      ApplicationInfo applicationInfo = null;

      try {
        applicationInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), 128);
      } catch (NameNotFoundException var16) {
      }

      if (applicationInfo != null && applicationInfo.metaData != null) {
        Bundle metaData = applicationInfo.metaData;
        String applicationId = getRequiredString(metaData, "com.facebook.sdk.ApplicationId", InternalAccountKitError.INVALID_APP_ID);
        String clientToken = getRequiredString(metaData, "com.facebook.accountkit.ClientToken", InternalAccountKitError.INVALID_CLIENT_TOKEN);
        String applicationName = getRequiredString(metaData, "com.facebook.accountkit.ApplicationName", InternalAccountKitError.INVALID_APP_NAME);
        boolean facebookAppEventsEnabled = metaData.getBoolean("com.facebook.accountkit.AccountKitFacebookAppEventsEnabled", true);
        String defaultLanguage = metaData.getString("com.facebook.accountkit.DefaultLanguage", "en-us");
        this.setDefaultLocale(context, defaultLanguage);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext);
        InternalLogger internalLogger = new InternalLogger(context.getApplicationContext(), applicationId, facebookAppEventsEnabled);
        AccessTokenManager accessTokenManager = new AccessTokenManager(applicationContext, localBroadcastManager);
        LoginManager loginManager = new LoginManager(internalLogger, accessTokenManager, localBroadcastManager);
        UpdateManager updateManager = new UpdateManager(internalLogger, localBroadcastManager);
        this.data = new Initializer.Data(applicationContext, applicationId, applicationName, clientToken, accessTokenManager, localBroadcastManager, loginManager, updateManager);
        if (CookieManager.getDefault() == null) {
          CookieManager.setDefault(new CookieManager(new AccountKitCookieStore(context), (CookiePolicy) null));
        }

        this.loadAccessToken();
        this.state = Initializer.State.INITIALIZED;
        NativeProtocol.updateAllAvailableProtocolVersionsAsync();
      } else {
        this.state = Initializer.State.FAILED;
      }
    }
  }

  AccessTokenManager getAccessTokenManager() {
    Validate.sdkInitialized();
    return this.data.accessTokenManager;
  }

  public Context getApplicationContext() {
    Validate.sdkInitialized();
    return this.data.applicationContext;
  }

  public String getApplicationId() {
    Validate.sdkInitialized();
    return this.data.applicationId;
  }

  String getApplicationName() {
    Validate.sdkInitialized();
    return this.data.applicationName;
  }

  String getClientToken() {
    Validate.sdkInitialized();
    return this.data.clientToken;
  }

  public InternalLogger getLogger() {
    Validate.sdkInitialized();
    return this.data.loginManager.getLogger();
  }

  LoginManager getLoginManager() {
    Validate.sdkInitialized();
    return this.data.loginManager;
  }

  UpdateManager getUpdateManager() {
    Validate.sdkInitialized();
    return this.data.updateManager;
  }

  public boolean isInitialized() {
    return this.state == Initializer.State.INITIALIZED;
  }

  boolean getAccountKitFacebookAppEventsEnabled() {
    return this.getLogger().getFacebookAppEventsEnabled();
  }

  private synchronized void loadAccessToken() {
    if (!this.isInitialized()) {
      this.data.accessTokenManager.loadCurrentAccessToken();
      Iterator var1 = this.callbacks.iterator();

      while (var1.hasNext()) {
        AccountKit.InitializeCallback callback = (AccountKit.InitializeCallback) var1.next();
        callback.onInitialized();
      }

      this.callbacks.clear();
    }

  }

  private boolean isValidLocale(String localeIn) {
    Locale[] validLocales = Locale.getAvailableLocales();
    Locale[] var3 = validLocales;
    int var4 = validLocales.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      Locale locale = var3[var5];
      if (localeIn.equalsIgnoreCase(locale.toString())) {
        return true;
      }
    }

    return false;
  }

  private void setDefaultLocale(Context context, String localeIn) {
    if (this.isValidLocale(localeIn)) {
      Locale locale = new Locale(localeIn);
      Configuration config = context.getResources().getConfiguration();
      config.locale = locale;
      context.getResources().updateConfiguration(config, (DisplayMetrics) null);
    }

  }

  private static enum State {
    UNINITIALIZED,
    INITIALIZED,
    FAILED;

    private State() {
    }
  }

  private static final class Data {
    final AccessTokenManager accessTokenManager;
    final Context applicationContext;
    final String applicationId;
    final String applicationName;
    final String clientToken;
    final LocalBroadcastManager localBroadcastManager;
    final LoginManager loginManager;
    final UpdateManager updateManager;

    Data(Context applicationContext, String applicationId, String applicationName, String clientToken, AccessTokenManager accessTokenManager, LocalBroadcastManager localBroadcastManager, LoginManager loginManager, UpdateManager updateManager) {
      this.applicationContext = applicationContext;
      this.applicationId = applicationId;
      this.applicationName = applicationName;
      this.clientToken = clientToken;
      this.accessTokenManager = accessTokenManager;
      this.localBroadcastManager = localBroadcastManager;
      this.loginManager = loginManager;
      this.updateManager = updateManager;
    }
  }
}
