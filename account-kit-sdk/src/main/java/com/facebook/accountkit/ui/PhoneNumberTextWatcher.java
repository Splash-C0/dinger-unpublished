package com.facebook.accountkit.ui;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Locale;

public class PhoneNumberTextWatcher implements TextWatcher {
  private boolean mSelfChange;
  private AsYouTypeFormatter mFormatter;

  public PhoneNumberTextWatcher() {
    this(Locale.getDefault().getCountry());
  }

  public PhoneNumberTextWatcher(String countryCode) {
    this.mSelfChange = false;
    if (countryCode == null) {
      throw new IllegalArgumentException();
    } else {
      this.mFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode);
    }
  }

  public synchronized void beforeTextChanged(CharSequence s, int start, int count, int after) {
    if (!this.mSelfChange) {
      ;
    }
  }

  public synchronized void onTextChanged(CharSequence s, int start, int before, int count) {
    if (!this.mSelfChange) {
      ;
    }
  }

  public synchronized void afterTextChanged(Editable s) {
    if (!this.mSelfChange) {
      String formatted = this.reformat(s, Selection.getSelectionEnd(s));
      if (formatted != null) {
        int rememberedPos = this.mFormatter.getRememberedPosition();
        this.mSelfChange = true;
        s.replace(0, s.length(), formatted, 0, formatted.length());
        if (formatted.equals(s.toString())) {
          Selection.setSelection(s, rememberedPos);
        }

        this.mSelfChange = false;
      }

    }
  }

  @Nullable
  private String reformat(CharSequence s, int cursor) {
    int curIndex = cursor - 1;
    String formatted = null;
    this.mFormatter.clear();
    char lastNonSeparator = 0;
    boolean hasCursor = false;
    int len = s.length();

    for (int i = 0; i < len; ++i) {
      char c = s.charAt(i);
      if (PhoneNumberUtils.isNonSeparator(c)) {
        if (lastNonSeparator != 0) {
          formatted = this.getFormattedNumber(lastNonSeparator, hasCursor);
          hasCursor = false;
        }

        lastNonSeparator = c;
      }

      if (i == curIndex) {
        hasCursor = true;
      }
    }

    if (lastNonSeparator != 0) {
      formatted = this.getFormattedNumber(lastNonSeparator, hasCursor);
    }

    return formatted;
  }

  private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
    return hasCursor ? this.mFormatter.inputDigitAndRememberPosition(lastNonSeparator) : this.mFormatter.inputDigit(lastNonSeparator);
  }
}
