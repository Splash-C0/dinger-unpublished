package com.facebook.accountkit.internal;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

public final class Utility {
  private static final String TAG = Utility.class.getName();
  private static final ScheduledThreadPoolExecutor BACKGROUND_EXECUTOR = new ScheduledThreadPoolExecutor(1);
  private static final String HASH_ALGORITHM_SHA1 = "SHA-1";
  private static final String EXTRA_APP_EVENTS_INFO_FORMAT_VERSION = "a2";
  private static final String NO_CARRIER = "NoCarrier";
  private static final int REFRESH_TIME_FOR_EXTENDED_DEVICE_INFO_MILLIS = 1800000;
  private static long availableExternalStorageGB = -1L;
  private static String carrierName = "NoCarrier";
  private static String deviceTimezone = "";
  private static int numCPUCores = 0;
  private static long timestampOfLastCheck = -1L;
  private static long totalExternalStorageGB = -1L;

  public Utility() {
  }

  public static boolean isNullOrEmpty(String s) {
    return s == null || s.length() == 0;
  }

  static ScheduledThreadPoolExecutor getBackgroundExecutor() {
    return BACKGROUND_EXECUTOR;
  }

  public static Executor getThreadPoolExecutor() {
    return AccountKit.getExecutor();
  }

  static boolean isDebuggable(Context context) {
    try {
      return (context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.flags & 2) != 0;
    } catch (NameNotFoundException var2) {
      return false;
    }
  }

  static Object getStringPropertyAsJSON(JSONObject jsonObject, String key) throws JSONException {
    Object value = jsonObject.opt(key);
    if (value != null && value instanceof String) {
      value = (new JSONTokener((String) value)).nextValue();
    }

    return value;
  }

  static boolean hasReadPhoneStatePermissions(@NonNull Context context) {
    return hasPermission(context, "android.permission.READ_PHONE_STATE");
  }

  static boolean hasGetAccountsPermissions(@NonNull Context context) {
    return hasPermission(context, "android.permission.GET_ACCOUNTS");
  }

  private static boolean hasPermission(Context context, String permission) {
    try {
      return 0 == PermissionChecker.checkCallingOrSelfPermission(context, permission);
    } catch (Exception var3) {
      return false;
    }
  }

  public static List<String> getDeviceEmailsIfAvailable(Context context) {
    if (hasGetAccountsPermissions(context)) {
      List<String> emails = new ArrayList();
      Account[] accounts = AccountManager.get(context).getAccounts();
      Account[] var3 = accounts;
      int var4 = accounts.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        Account account = var3[var5];
        if (!isNullOrEmpty(account.name) && Patterns.EMAIL_ADDRESS.matcher(account.name).matches() && !emails.contains(account.name)) {
          emails.add(account.name);
        }
      }

      return emails;
    } else {
      return null;
    }
  }

  @SuppressLint({"HardwareIds"})
  @Nullable
  public static String readPhoneNumberIfAvailable(Context context) {
    if (hasReadPhoneStatePermissions(context)) {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
      if (telephonyManager != null) {
        return telephonyManager.getLine1Number();
      }
    }

    return null;
  }

  public static boolean hasGooglePlayServices(Context context) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(context);
    return googlePlayServicesAvailable == 0;
  }

  @Nullable
  public static PhoneNumber createI8nPhoneNumber(@Nullable String phone) {
    if (isNullOrEmpty(phone)) {
      return null;
    } else {
      if (!phone.startsWith("+")) {
        phone = "+" + phone;
      }

      try {
        return PhoneNumberUtil.getInstance().parse(phone, "");
      } catch (NumberParseException var2) {
        return null;
      }
    }
  }

  @Nullable
  public static com.facebook.accountkit.PhoneNumber convertI8nPhoneNumber(@Nullable PhoneNumber phoneNumber) {
    return phoneNumber != null && PhoneNumberUtil.getInstance().isValidNumber(phoneNumber) ? new com.facebook.accountkit.PhoneNumber(String.valueOf(phoneNumber.getCountryCode()), String.valueOf(phoneNumber.getNationalNumber()), PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber)) : null;
  }

  @Nullable
  public static com.facebook.accountkit.PhoneNumber createPhoneNumber(@Nullable String phone) {
    return convertI8nPhoneNumber(createI8nPhoneNumber(phone));
  }

  @Nullable
  public static String getCountryCode(String phone) {
    if (isNullOrEmpty(phone)) {
      return null;
    } else {
      if (phone.startsWith("+")) {
        phone = phone.substring(1);
      }

      String region = null;

      try {
        StringBuilder sb = new StringBuilder(phone.length());

        for (int i = 0; i < phone.length() && region == null; ++i) {
          sb.append(phone.charAt(i));
          region = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(Integer.valueOf(sb.toString()));
          if (region.equals("ZZ")) {
            region = null;
          }
        }
      } catch (NumberFormatException var4) {
      }

      return region;
    }
  }

  public static String cleanPhoneNumberString(String input) {
    return isNullOrEmpty(input) ? "" : input.replaceAll("[^\\d]", "");
  }

  static void putNonNullString(Bundle bundle, String key, String value) {
    if (bundle != null && key != null && value != null) {
      bundle.putString(key, value);
    }

  }

  static String readStreamToString(InputStream inputStream) throws IOException {
    BufferedInputStream bufferedInputStream = null;
    InputStreamReader reader = null;

    try {
      bufferedInputStream = new BufferedInputStream(inputStream);
      reader = new InputStreamReader(bufferedInputStream);
      StringBuilder stringBuilder = new StringBuilder();
      int bufferSize = 2048;
      char[] buffer = new char[2048];

      int n;
      while ((n = reader.read(buffer)) != -1) {
        stringBuilder.append(buffer, 0, n);
      }

      String var7 = stringBuilder.toString();
      return var7;
    } finally {
      closeQuietly(bufferedInputStream);
      closeQuietly(reader);
    }
  }

  static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException var2) {
    }

  }

  public static String getCurrentCountry(Context context) {
    try {
      TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
      String simCountry = tm.getSimCountryIso();
      if (simCountry != null && simCountry.length() == 2) {
        return simCountry.toLowerCase(Locale.US);
      }

      if (tm.getPhoneType() != 2) {
        String networkCountry = tm.getNetworkCountryIso();
        if (networkCountry != null && networkCountry.length() == 2) {
          return networkCountry.toLowerCase(Locale.US);
        }
      }
    } catch (Exception var4) {
    }

    return null;
  }

  static void disconnectQuietly(URLConnection connection) {
    if (connection instanceof HttpURLConnection) {
      ((HttpURLConnection) connection).disconnect();
    }

  }

  static int copyAndCloseInputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
    BufferedInputStream bufferedInputStream = null;
    int totalBytes = 0;

    try {
      bufferedInputStream = new BufferedInputStream(inputStream);

      int bytesRead;
      for (byte[] buffer = new byte[8192]; (bytesRead = bufferedInputStream.read(buffer)) != -1; totalBytes += bytesRead) {
        outputStream.write(buffer, 0, bytesRead);
      }
    } finally {
      if (bufferedInputStream != null) {
        bufferedInputStream.close();
      }

      if (inputStream != null) {
        inputStream.close();
      }

    }

    return totalBytes;
  }

  static boolean notEquals(Object o1, Object o2) {
    return o1 == null || !o1.equals(o2);
  }

  public static <T> boolean areObjectsEqual(T a, T b) {
    if (a == null) {
      return b == null;
    } else {
      return a.equals(b);
    }
  }

  public static int getHashCode(Object object) {
    return object == null ? 0 : object.hashCode();
  }

  static String sha1hash(byte[] bytes) {
    MessageDigest hash;
    try {
      hash = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException var3) {
      return null;
    }

    return hashBytes(hash, bytes);
  }

  private static String hashBytes(MessageDigest hash, byte[] bytes) {
    hash.update(bytes);
    byte[] digest = hash.digest();
    StringBuilder builder = new StringBuilder();
    byte[] var4 = digest;
    int var5 = digest.length;

    for (int var6 = 0; var6 < var5; ++var6) {
      int b = var4[var6];
      builder.append(Integer.toHexString(b >> 4 & 15));
      builder.append(Integer.toHexString(b & 15));
    }

    return builder.toString();
  }

  static String getMetadataApplicationId() {
    return AccountKit.getApplicationId();
  }

  static void logd(String tag, Exception e) {
    if (tag != null && e != null) {
      Log.d(tag, e.getClass().getSimpleName() + ": " + e.getMessage());
    }

  }

  static void logd(String tag, String msg, Throwable t) {
    if (!isNullOrEmpty(tag)) {
      Log.d(tag, msg, t);
    }

  }

  static void assertUIThread() {
    if (!Looper.getMainLooper().equals(Looper.myLooper())) {
      Log.w(TAG, "This method should be called from the UI thread");
    }

  }

  static Pair<AccountKitError, InternalAccountKitError> createErrorFromServerError(AccountKitRequestError graphError) {
    int errorCode = graphError.getErrorCode();
    if (graphError.getSubErrorCode() == 1550001) {
      errorCode = 605;
    }

    InternalAccountKitError internalError = new InternalAccountKitError(errorCode, graphError.getErrorMessage(), graphError.getUserErrorMessage());
    AccountKitError error;
    switch (graphError.getErrorCode()) {
      case 100:
        error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
        break;
      case 101:
        error = new AccountKitError(AccountKitError.Type.NETWORK_CONNECTION_ERROR, internalError);
        break;
      case 15003:
        error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
        break;
      case 1948001:
        error = new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED, internalError);
        break;
      case 1948002:
        error = new AccountKitError(AccountKitError.Type.ARGUMENT_ERROR, internalError);
        break;
      case 1948003:
        error = new AccountKitError(AccountKitError.Type.SERVER_ERROR, internalError);
        break;
      default:
        error = new AccountKitError(AccountKitError.Type.SERVER_ERROR, internalError);
    }

    return new Pair(error, internalError);
  }

  static boolean isConfirmationCodeRetryable(InternalAccountKitError internalAccountKitError) {
    return internalAccountKitError != null && internalAccountKitError.getCode() == 15003;
  }

  static void setAppEventAttributionParameters(JSONObject params, String anonymousAppDeviceGUID) throws JSONException {
    params.put("anon_id", anonymousAppDeviceGUID);
  }

  static void setAppEventExtendedDeviceInfoParameters(JSONObject params, Context appContext) throws JSONException {
    JSONArray extraInfoArray = new JSONArray();
    extraInfoArray.put("a2");
    refreshPeriodicExtendedDeviceInfo(appContext);
    String pkgName = appContext.getPackageName();
    int versionCode = -1;
    String versionName = "";

    try {
      PackageInfo pi = appContext.getPackageManager().getPackageInfo(pkgName, 0);
      versionCode = pi.versionCode;
      versionName = pi.versionName;
    } catch (NameNotFoundException var16) {
    }

    extraInfoArray.put(pkgName);
    extraInfoArray.put(versionCode);
    extraInfoArray.put(versionName);
    extraInfoArray.put(VERSION.RELEASE);
    extraInfoArray.put(Build.MODEL);

    Locale locale;
    try {
      locale = appContext.getResources().getConfiguration().locale;
    } catch (Exception var15) {
      locale = Locale.getDefault();
    }

    extraInfoArray.put(locale.getLanguage() + "_" + locale.getCountry());
    extraInfoArray.put(deviceTimezone);
    extraInfoArray.put(carrierName);
    int width = 0;
    int height = 0;
    double density = 0.0D;

    try {
      WindowManager wm = (WindowManager) appContext.getSystemService("window");
      if (wm != null) {
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        density = (double) displayMetrics.density;
      }
    } catch (Exception var14) {
    }

    extraInfoArray.put(width);
    extraInfoArray.put(height);
    extraInfoArray.put(String.format(Locale.ENGLISH, "%.2f", density));
    extraInfoArray.put(refreshBestGuessNumberOfCPUCores());
    extraInfoArray.put(totalExternalStorageGB);
    extraInfoArray.put(availableExternalStorageGB);
    params.put("extinfo", extraInfoArray.toString());
  }

  public static String getRedirectURL() {
    return "ak" + AccountKit.getApplicationId() + "://authorize";
  }

  private static int refreshBestGuessNumberOfCPUCores() {
    if (numCPUCores > 0) {
      return numCPUCores;
    } else {
      try {
        File cpuDir = new File("/sys/devices/system/cpu/");
        File[] cpuFiles = cpuDir.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String fileName) {
            return Pattern.matches("cpu[0-9]+", fileName);
          }
        });
        numCPUCores = cpuFiles.length;
      } catch (Exception var2) {
      }

      if (numCPUCores <= 0) {
        numCPUCores = Math.max(Runtime.getRuntime().availableProcessors(), 1);
      }

      return numCPUCores;
    }
  }

  private static void refreshPeriodicExtendedDeviceInfo(Context appContext) {
    if (timestampOfLastCheck == -1L || System.currentTimeMillis() - timestampOfLastCheck >= 1800000L) {
      timestampOfLastCheck = System.currentTimeMillis();
      refreshTimezone();
      refreshCarrierName(appContext);
      refreshTotalExternalStorage();
      refreshAvailableExternalStorage();
    }

  }

  private static void refreshTimezone() {
    try {
      TimeZone tz = TimeZone.getDefault();
      deviceTimezone = tz.getDisplayName(tz.inDaylightTime(new Date()), 0);
    } catch (Exception var1) {
    }

  }

  private static void refreshCarrierName(Context appContext) {
    if (carrierName.equals("NoCarrier")) {
      try {
        TelephonyManager telephonyManager = (TelephonyManager) appContext.getSystemService("phone");
        carrierName = telephonyManager.getNetworkOperatorName();
      } catch (Exception var2) {
      }
    }

  }

  private static void refreshAvailableExternalStorage() {
    try {
      if (externalStorageExists()) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        availableExternalStorageGB = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
      }

      availableExternalStorageGB = convertBytesToGB((double) availableExternalStorageGB);
    } catch (Exception var2) {
    }

  }

  private static void refreshTotalExternalStorage() {
    try {
      if (externalStorageExists()) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        totalExternalStorageGB = (long) stat.getBlockCount() * (long) stat.getBlockSize();
      }

      totalExternalStorageGB = convertBytesToGB((double) totalExternalStorageGB);
    } catch (Exception var2) {
    }

  }

  private static boolean externalStorageExists() {
    return "mounted".equals(Environment.getExternalStorageState());
  }

  private static long convertBytesToGB(double bytes) {
    return Math.round(bytes / 1.073741824E9D);
  }
}
