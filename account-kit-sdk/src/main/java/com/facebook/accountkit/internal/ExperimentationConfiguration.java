package com.facebook.accountkit.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ExperimentationConfiguration {
  private static final String TAG = ExperimentationConfiguration.class.getSimpleName();
  private static final String PREFERENCE_PREFIX;
  private static final String AK_PREFERENCES;
  private static final String PREF_CREATE_TIME;
  private static final String PREF_TTL;
  private static final String PREF_UNIT_ID;
  private static final long DEFAULT_TTL;

  static {
    PREFERENCE_PREFIX = TAG;
    AK_PREFERENCES = PREFERENCE_PREFIX + ".AK_PREFERENCES";
    PREF_CREATE_TIME = PREFERENCE_PREFIX + ".PREF_CREATE_TIME";
    PREF_TTL = PREFERENCE_PREFIX + ".PREF_TTL";
    PREF_UNIT_ID = PREFERENCE_PREFIX + ".PREF_UNIT_ID";
    DEFAULT_TTL = TimeUnit.DAYS.toMillis(3L);
  }

  private final SharedPreferences mSharedPrefs;

  ExperimentationConfiguration(Context context) {
    this.mSharedPrefs = getSharedPreferences(context);
  }

  static void load(Context context, String unitID, Long createTime, @Nullable Long ttl, Map<Integer, Integer> featureSet) {
    if (unitID != null && createTime != null) {
      saveConfiguration(context, unitID, createTime, ttl, featureSet);
    }
  }

  @SuppressLint({"CommitPrefEdits"})
  private static void saveConfiguration(Context context, String unitID, long createTime, @Nullable Long ttl, Map<Integer, Integer> featureSet) {
    SharedPreferences sharedPrefs = getSharedPreferences(context);
    Editor editor = sharedPrefs.edit();
    editor.clear();
    editor.putLong(PREF_CREATE_TIME, createTime);
    if (ttl != null) {
      editor.putLong(PREF_TTL, ttl);
    }

    editor.putString(PREF_UNIT_ID, unitID);
    Iterator var8 = featureSet.keySet().iterator();

    while (var8.hasNext()) {
      Integer prefKey = (Integer) var8.next();
      editor.putInt(PREFERENCE_PREFIX + prefKey, (Integer) featureSet.get(prefKey));
    }

    editor.commit();
  }

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getApplicationContext().getSharedPreferences(AK_PREFERENCES, 0);
  }

  public boolean exists() {
    return this.mSharedPrefs.getLong(PREF_CREATE_TIME, -1L) > 0L;
  }

  boolean isStale() {
    long now = Calendar.getInstance().getTime().getTime();
    long createTime = this.mSharedPrefs.getLong(PREF_CREATE_TIME, now);
    long ttl = this.mSharedPrefs.getLong(PREF_TTL, DEFAULT_TTL);
    return Math.abs(now - createTime) > ttl;
  }

  @Nullable
  String getUnitID() {
    return this.mSharedPrefs.getString(PREF_UNIT_ID, (String) null);
  }

  public int getIntValue(Feature feature) {
    return this.mSharedPrefs.getInt(PREFERENCE_PREFIX + feature.getPrefKey(), feature.getDefaultValue());
  }

  public boolean getBooleanValue(Feature feature) {
    return this.getIntValue(feature) > 0;
  }
}
