package com.facebook.accountkit.internal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.LoggingBehavior;

final class ConsoleLogger {
  private static final String LOG_TAG_BASE = "AccountKitSDK.";
  private static final String NEWLINE = "\n";
  private final LoggingBehavior behavior;
  private final String tag;
  private StringBuilder contents;

  ConsoleLogger(LoggingBehavior behavior, @NonNull String tag) {
    this.behavior = behavior;
    this.tag = "AccountKitSDK." + tag;
    this.contents = new StringBuilder();
  }

  public static void log(LoggingBehavior behavior, String tag, String string) {
    log(behavior, 3, tag, string);
  }

  public static void log(LoggingBehavior behavior, String tag, String format, Object... args) {
    String string = String.format(format, args);
    log(behavior, 3, tag, string);
  }

  public static void log(LoggingBehavior behavior, int priority, String tag, String format, Object... args) {
    String string = String.format(format, args);
    log(behavior, priority, tag, string);
  }

  private static void log(LoggingBehavior behavior, int priority, String tag, String string) {
    if (AccountKit.getLoggingBehaviors().isEnabled(behavior)) {
      if (!tag.startsWith("AccountKitSDK.")) {
        tag = "AccountKitSDK." + tag;
      }

      Log.println(priority, tag, string);
      if (behavior == LoggingBehavior.DEVELOPER_ERRORS) {
        (new Exception()).printStackTrace();
      }
    }

  }

  public void log() {
    log(this.behavior, 3, this.tag, this.contents.toString());
    this.contents = new StringBuilder();
  }

  void appendLine(String string) {
    if (this.shouldLog()) {
      this.contents.append(string).append("\n");
    }

  }

  private void append(String toFormat, Object... args) {
    if (this.shouldLog()) {
      this.contents.append(String.format(toFormat, args));
    }

  }

  void appendKeyValue(String key, Object value) {
    this.append("  %s:\t%s\n", key, value);
  }

  private boolean shouldLog() {
    return AccountKit.getLoggingBehaviors().isEnabled(this.behavior);
  }
}
