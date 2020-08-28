package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

final class NativeProtocol {
  static final int PROTOCOL_VERSION_20161017 = 20161017;
  static final String CONTENT_SCHEME = "content://";
  static final String PLATFORM_PROVIDER = ".provider.PlatformProvider";
  static final String PLATFORM_PROVIDER_VERSIONS = ".provider.PlatformProvider/versions";
  static final String PLATFORM_PROVIDER_VERSION_COLUMN = "version";
  static final int MESSAGE_GET_AK_SEAMLESS_TOKEN_REQUEST = 65544;
  static final int MESSAGE_GET_AK_SEAMLESS_TOKEN_REPLY = 65545;
  static final String EXTRA_APPLICATION_ID = "com.facebook.platform.extra.APPLICATION_ID";
  static final String EXTRA_SEAMLESS_LOGIN_TOKEN = "com.facebook.platform.extra.SEAMLESS_LOGIN_TOKEN";
  static final String EXTRA_EXPIRES_SECONDS_SINCE_EPOCH = "com.facebook.platform.extra.EXPIRES_SECONDS_SINCE_EPOCH";
  static final String STATUS_ERROR_TYPE = "com.facebook.platform.status.ERROR_TYPE";
  private static final String INTENT_ACTION_PLATFORM_SERVICE = "com.facebook.platform.PLATFORM_SERVICE";
  private static final String INTENT_ACTION_FBLITE_PLATFORM_SERVICE = "com.facebook.lite.platform.PLATFORM_SERVICE";
  private static AtomicBoolean protocolVersionsAsyncUpdating = new AtomicBoolean(false);
  private static List<NativeAppInfo> facebookAppInfoList = Arrays.asList(new NativeProtocol.KatanaAppInfo(), new NativeProtocol.WakizashiAppInfo(), new NativeProtocol.FBLiteAppInfo());

  NativeProtocol() {
  }

  static boolean validateApplicationForService() {
    Iterator var0 = facebookAppInfoList.iterator();

    NativeAppInfo appInfo;
    do {
      if (!var0.hasNext()) {
        return false;
      }

      appInfo = (NativeAppInfo) var0.next();
    } while (!appInfo.isAppInstalled());

    return true;
  }

  static boolean validateProtocolVersionForService(int version) {
    Iterator var1 = facebookAppInfoList.iterator();

    NativeAppInfo appInfo;
    do {
      if (!var1.hasNext()) {
        return false;
      }

      appInfo = (NativeAppInfo) var1.next();
    } while (!appInfo.getAvailableVersions().contains(version));

    return true;
  }

  static void updateAllAvailableProtocolVersionsAsync() {
    if (protocolVersionsAsyncUpdating.compareAndSet(false, true)) {
      Utility.getThreadPoolExecutor().execute(new Runnable() {
        public void run() {
          try {
            Iterator var1 = NativeProtocol.facebookAppInfoList.iterator();

            while (var1.hasNext()) {
              NativeAppInfo appInfo = (NativeAppInfo) var1.next();
              appInfo.fetchAvailableVersions(true);
            }
          } finally {
            NativeProtocol.protocolVersionsAsyncUpdating.set(false);
          }

        }
      });
    }
  }

  static Intent createPlatformServiceIntent(Context context) {
    Iterator var1 = facebookAppInfoList.iterator();

    Intent intent;
    do {
      if (!var1.hasNext()) {
        return null;
      }

      NativeAppInfo appInfo = (NativeAppInfo) var1.next();
      intent = appInfo.getPlatformServiceIntent().addCategory("android.intent.category.DEFAULT");
      intent = validateServiceIntent(context, intent, appInfo);
    } while (intent == null);

    return intent;
  }

  private static Intent validateServiceIntent(Context context, Intent intent, NativeAppInfo appInfo) {
    ResolveInfo resolveInfo = context.getPackageManager().resolveService(intent, 0);
    if (resolveInfo == null) {
      return null;
    } else {
      return !appInfo.validateSignature(context, resolveInfo.serviceInfo.packageName) ? null : intent;
    }
  }

  private static class FBLiteAppInfo extends NativeAppInfo {
    private static final String FBLITE_PACKAGE = "com.facebook.lite";

    private FBLiteAppInfo() {
    }

    protected String getPackage() {
      return "com.facebook.lite";
    }

    protected Intent getPlatformServiceIntent() {
      return (new Intent("com.facebook.lite.platform.PLATFORM_SERVICE")).setPackage(this.getPackage());
    }
  }

  private static class WakizashiAppInfo extends NativeAppInfo {
    private static final String WAKIZASHI_PACKAGE = "com.facebook.wakizashi";

    private WakizashiAppInfo() {
    }

    protected String getPackage() {
      return "com.facebook.wakizashi";
    }

    protected Intent getPlatformServiceIntent() {
      return (new Intent("com.facebook.platform.PLATFORM_SERVICE")).setPackage(this.getPackage());
    }
  }

  private static class KatanaAppInfo extends NativeAppInfo {
    private static final String KATANA_PACKAGE = "com.facebook.katana";

    private KatanaAppInfo() {
    }

    protected String getPackage() {
      return "com.facebook.katana";
    }

    protected Intent getPlatformServiceIntent() {
      return (new Intent("com.facebook.platform.PLATFORM_SERVICE")).setPackage(this.getPackage());
    }
  }
}
