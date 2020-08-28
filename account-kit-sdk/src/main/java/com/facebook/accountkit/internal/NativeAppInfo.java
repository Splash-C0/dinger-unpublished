package com.facebook.accountkit.internal;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.HashSet;
import java.util.TreeSet;

abstract class NativeAppInfo {
  private static final String TAG = NativeAppInfo.class.getSimpleName();
  private static final String FBI_HASH = "a4b7452e2ed8f5f191058ca7bbfd26b0d3214bfc";
  private static final String FBL_HASH = "5e8f16062ea3cd2c4a0d547876baa6f38cabf625";
  private static final String FBL2_HASH = "df6b721c8b4d3b6eb44c861d4415007e5a35fc95";
  private static final String FBR_HASH = "8a3c4b262d721acd49a4bf97d5213199c86fa2b9";
  private static final String FBR2_HASH = "cc2751449a350f668590264ed76692694a80308a";
  private static final HashSet<String> validAppSignatureHashes = buildAppSignatureHashes();
  private TreeSet<Integer> availableVersions;
  private boolean appInstalled;

  NativeAppInfo() {
  }

  private static HashSet<String> buildAppSignatureHashes() {
    HashSet<String> set = new HashSet();
    set.add("8a3c4b262d721acd49a4bf97d5213199c86fa2b9");
    set.add("cc2751449a350f668590264ed76692694a80308a");
    set.add("a4b7452e2ed8f5f191058ca7bbfd26b0d3214bfc");
    set.add("5e8f16062ea3cd2c4a0d547876baa6f38cabf625");
    set.add("df6b721c8b4d3b6eb44c861d4415007e5a35fc95");
    return set;
  }

  protected abstract String getPackage();

  protected abstract Intent getPlatformServiceIntent();

  public boolean validateSignature(Context context, String packageName) {
    String brand = Build.BRAND;
    int applicationFlags = context.getApplicationInfo().flags;
    if (brand.startsWith("generic") && (applicationFlags & 2) != 0) {
      return true;
    } else {
      PackageInfo packageInfo = null;

      try {
        packageInfo = context.getPackageManager().getPackageInfo(packageName, 64);
      } catch (NameNotFoundException var11) {
        return false;
      }

      if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
        Signature[] var6 = packageInfo.signatures;
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
          Signature signature = var6[var8];
          String hashedSignature = Utility.sha1hash(signature.toByteArray());
          if (!validAppSignatureHashes.contains(hashedSignature)) {
            return false;
          }
        }

        return true;
      } else {
        return false;
      }
    }
  }

  public boolean isAppInstalled() {
    if (this.availableVersions == null) {
      this.fetchAvailableVersions(false);
    }

    return this.appInstalled;
  }

  public TreeSet<Integer> getAvailableVersions() {
    if (this.availableVersions == null) {
      this.fetchAvailableVersions(false);
    }

    return this.availableVersions;
  }

  public synchronized void fetchAvailableVersions(boolean force) {
    if (this.availableVersions == null || force) {
      TreeSet<Integer> allAvailableVersions = new TreeSet();
      Context appContext = AccountKitController.getApplicationContext();
      ContentResolver contentResolver = appContext.getContentResolver();
      String[] projection = new String[]{"version"};
      Uri uri = Uri.parse("content://" + this.getPackage() + ".provider.PlatformProvider/versions");
      Cursor c = null;

      try {
        PackageManager pm = AccountKitController.getApplicationContext().getPackageManager();
        String contentProviderName = this.getPackage() + ".provider.PlatformProvider";
        ProviderInfo pInfo = null;

        try {
          pInfo = pm.resolveContentProvider(contentProviderName, 0);
        } catch (RuntimeException var17) {
          Log.e(TAG, "Failed to query content resolver.", var17);
        }

        if (pInfo != null) {
          try {
            c = contentResolver.query(uri, projection, (String) null, (String[]) null, (String) null);
          } catch (SecurityException | NullPointerException var16) {
            Log.e(TAG, "Failed to query content resolver.");
            c = null;
          }

          if (c != null) {
            while (c.moveToNext()) {
              int version = c.getInt(c.getColumnIndex("version"));
              allAvailableVersions.add(version);
            }
          }
        }
      } finally {
        if (c != null) {
          this.appInstalled = true;
          c.close();
        }

      }

      this.availableVersions = allAvailableVersions;
    }
  }
}
