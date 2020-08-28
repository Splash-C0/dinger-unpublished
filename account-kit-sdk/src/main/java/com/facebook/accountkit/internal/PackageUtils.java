package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PackageUtils {
  public PackageUtils() {
  }

  @Nullable
  public static String computePackageHash(Context context, String packageName) {
    return computePackageHash(context, packageName, 0);
  }

  @Nullable
  public static String computePackageHash(Context context, String packageName, int encodingFlags) {
    byte[] pkghash = getPackageSig(context, packageName);
    return pkghash == null ? null : Base64.encodeToString(pkghash, encodingFlags);
  }

  @Nullable
  private static byte[] getPackageSig(Context context, String pkg) {
    PackageManager pm = context.getPackageManager();

    PackageInfo pinfo;
    try {
      pinfo = pm.getPackageInfo(pkg, 64);
    } catch (NameNotFoundException var7) {
      return null;
    }

    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException var6) {
      return null;
    }

    String s = pkg + " " + pinfo.signatures[0].toCharsString();
    md.update(s.trim().getBytes(Charset.forName("US-ASCII")));
    return md.digest();
  }
}
