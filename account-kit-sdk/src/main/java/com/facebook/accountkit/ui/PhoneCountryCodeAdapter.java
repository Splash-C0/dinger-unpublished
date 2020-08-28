package com.facebook.accountkit.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.custom.R.array;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.internal.Utility;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

final class PhoneCountryCodeAdapter extends BaseAdapter implements SpinnerAdapter {
  private final Context context;
  private final UIManager uiManager;
  private final PhoneCountryCodeAdapter.PhoneCountryCode[] phoneCountryCodes;

  public PhoneCountryCodeAdapter(Context context, UIManager uiManager, String[] blacklist, String[] whitelist) {
    this.context = context;
    this.uiManager = uiManager;
    this.phoneCountryCodes = getAllPhoneCountryCodes(context, blacklist, whitelist);
  }

  private static PhoneCountryCodeAdapter.PhoneCountryCode[] getAllPhoneCountryCodes(Context context, String[] blacklist, String[] whitelist) {
    String[] resources = context.getResources().getStringArray(array.com_accountkit_phone_country_codes);
    ArrayList<PhoneCountryCodeAdapter.PhoneCountryCode> phoneCountryCodeList = new ArrayList();
    Set<String> clientWhitelisted = whitelist != null ? new HashSet(Arrays.asList(whitelist)) : null;
    Set<String> clientBlacklisted = blacklist != null && blacklist.length > 0 ? new HashSet(Arrays.asList(blacklist)) : new HashSet();
    String[] var7 = resources;
    int var8 = resources.length;

    for (int var9 = 0; var9 < var8; ++var9) {
      String resource = var7[var9];
      String[] components = resource.split(":", 3);
      if (!clientBlacklisted.contains(components[1]) && (clientWhitelisted == null || clientWhitelisted.contains(components[1]))) {
        phoneCountryCodeList.add(new PhoneCountryCodeAdapter.PhoneCountryCode(components[0], components[1], components[2]));
      }
    }

    final Collator collator = Collator.getInstance(Resources.getSystem().getConfiguration().locale);
    collator.setStrength(0);
    Collections.sort(phoneCountryCodeList, new Comparator<PhoneCountryCodeAdapter.PhoneCountryCode>() {
      public int compare(PhoneCountryCodeAdapter.PhoneCountryCode code1, PhoneCountryCodeAdapter.PhoneCountryCode code2) {
        return collator.compare(code1.countryName, code2.countryName);
      }
    });
    PhoneCountryCodeAdapter.PhoneCountryCode[] phoneCountryCodes = new PhoneCountryCodeAdapter.PhoneCountryCode[phoneCountryCodeList.size()];
    phoneCountryCodeList.toArray(phoneCountryCodes);
    return phoneCountryCodes;
  }

  public int getCount() {
    return this.phoneCountryCodes.length;
  }

  public PhoneCountryCodeAdapter.ValueData getInitialValue(@Nullable PhoneNumber initialPhoneNumber, @Nullable String defaultCountryCode) {
    String countryCodeSource = null;
    String countryCode = null;
    int position = -1;
    int attempt;
    if (initialPhoneNumber != null) {
      countryCodeSource = PhoneCountryCodeAdapter.CountryCodeSource.APP_SUPPLIED_PHONE_NUMBER.name();
      attempt = this.phoneCountryCodes.length;
      countryCode = initialPhoneNumber.getCountryCode();
      String countryCodeIso = initialPhoneNumber.getCountryCodeIso();
      if (countryCodeIso != null) {
        position = this.getIndexOfCountryCode(countryCodeIso);
      } else {
        for (int i = 0; i < attempt; ++i) {
          if (countryCode.equalsIgnoreCase(this.phoneCountryCodes[i].countryCode)) {
            position = i;
            break;
          }
        }
      }
    }

    for (attempt = 0; attempt <= 3 && position == -1; ++attempt) {
      switch (attempt) {
        case 0:
          countryCodeSource = PhoneCountryCodeAdapter.CountryCodeSource.APP_SUPPLIED_DEFAULT_VALUE.name();
          countryCode = defaultCountryCode;
          break;
        case 1:
          countryCodeSource = PhoneCountryCodeAdapter.CountryCodeSource.TELEPHONY_SERVICE.name();
          countryCode = Utility.getCurrentCountry(this.context);
          break;
        case 2:
          countryCodeSource = PhoneCountryCodeAdapter.CountryCodeSource.DEFAULT_VALUE.name();
          countryCode = "US";
          break;
        case 3:
          countryCodeSource = PhoneCountryCodeAdapter.CountryCodeSource.FIRST_VALUE.name();
          countryCode = this.phoneCountryCodes[0].countryCode;
          break;
        default:
          countryCodeSource = this.phoneCountryCodes[position].isoCode;
          countryCode = this.phoneCountryCodes[position].countryCode;
      }

      if (attempt <= 3) {
        position = this.getIndexOfCountryCode(countryCode);
      }
    }

    return new PhoneCountryCodeAdapter.ValueData(countryCode, countryCodeSource, position);
  }

  public PhoneCountryCodeAdapter.ValueData getItem(int position) {
    PhoneCountryCodeAdapter.PhoneCountryCode countryCode = this.phoneCountryCodes[position];
    return new PhoneCountryCodeAdapter.ValueData(countryCode.countryCode, countryCode.isoCode, position);
  }

  public long getItemId(int position) {
    return this.phoneCountryCodes[position].itemId;
  }

  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View view;
    if (convertView == null) {
      view = View.inflate(this.context, layout.com_accountkit_phone_country_code_item_layout, (ViewGroup) null);
      view.setLayoutParams(new LayoutParams(-1, -1));
    } else {
      view = convertView;
    }

    PhoneCountryCodeAdapter.PhoneCountryCode phoneCountryCode = this.phoneCountryCodes[position];
    TextView labelTextView = (TextView) view.findViewById(id.label);
    TextView countryFlagView = (TextView) view.findViewById(id.flag);
    labelTextView.setText(this.getCountryLabel(phoneCountryCode));
    countryFlagView.setText(phoneCountryCode.getCountrySymbol());
    return view;
  }

  private String getCountryLabel(PhoneCountryCodeAdapter.PhoneCountryCode phoneCountryCode) {
    return phoneCountryCode.getCountryName() + " (+" + phoneCountryCode.getCountryCode() + ")";
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    if (convertView == null) {
      view = View.inflate(this.context, layout.com_accountkit_phone_country_code_layout, (ViewGroup) null);
      view.setLayoutParams(new android.view.ViewGroup.LayoutParams(parent.getLayoutParams()));
    } else {
      view = convertView;
    }

    PhoneCountryCodeAdapter.PhoneCountryCode phoneCountryCode = this.phoneCountryCodes[position];
    TextView countryCodeTextView = (TextView) view.findViewById(id.country_code);
    countryCodeTextView.setText(phoneCountryCode.getCountrySymbol());
    if (!ViewUtility.useLegacy(this.uiManager)) {
      countryCodeTextView.setTextColor(((SkinManager) this.uiManager).getTextColor());
    }

    return view;
  }

  public int getIndexOfCountryCode(String countryCode) {
    if (Utility.isNullOrEmpty(countryCode)) {
      return -1;
    } else {
      int length = this.phoneCountryCodes.length;

      for (int i = 0; i < length; ++i) {
        if (countryCode.equalsIgnoreCase(this.phoneCountryCodes[i].countryCode) || countryCode.equalsIgnoreCase(this.phoneCountryCodes[i].isoCode)) {
          return i;
        }
      }

      return -1;
    }
  }

  private static enum CountryCodeSource {
    APP_SUPPLIED_DEFAULT_VALUE,
    APP_SUPPLIED_PHONE_NUMBER,
    DEFAULT_VALUE,
    FIRST_VALUE,
    TELEPHONY_SERVICE;

    private CountryCodeSource() {
    }
  }

  public static class ValueData implements Parcelable {
    public static final Creator<PhoneCountryCodeAdapter.ValueData> CREATOR = new Creator<PhoneCountryCodeAdapter.ValueData>() {
      public PhoneCountryCodeAdapter.ValueData createFromParcel(Parcel source) {
        return new PhoneCountryCodeAdapter.ValueData(source);
      }

      public PhoneCountryCodeAdapter.ValueData[] newArray(int size) {
        return new PhoneCountryCodeAdapter.ValueData[size];
      }
    };
    public final String countryCode;
    public final String countryCodeSource;
    public final int position;

    private ValueData(String countryCode, String countryCodeSource, int position) {
      this.countryCode = countryCode;
      this.countryCodeSource = countryCodeSource;
      this.position = position;
    }

    private ValueData(Parcel parcel) {
      this.countryCode = parcel.readString();
      this.countryCodeSource = parcel.readString();
      this.position = parcel.readInt();
    }

    public int describeContents() {
      return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.countryCode);
      dest.writeString(this.countryCodeSource);
      dest.writeInt(this.position);
    }
  }

  private static final class PhoneCountryCode {
    final String countryCode;
    final String isoCode;
    final String countryName;
    final long itemId;
    final String countrySymbol;

    PhoneCountryCode(String countryCode, String isoCode, String countryName) {
      this.countryCode = countryCode;
      this.isoCode = isoCode;
      this.countryName = countryName;
      String itemIdString = countryCode.replaceAll("[\\D]", "");
      int count = isoCode.length();

      for (int i = 0; i < count; ++i) {
        itemIdString = itemIdString + Integer.toString(isoCode.charAt(i));
      }

      this.itemId = Long.valueOf(itemIdString);
      if (areFlagsSupported()) {
        String emoji = isoCodeToEmojiFlag(isoCode);
        this.countrySymbol = TextUtils.isEmpty(emoji) ? isoCode : emoji;
      } else {
        this.countrySymbol = isoCode;
      }

    }

    private static String isoCodeToEmojiFlag(String isoCode) {
      int flagOffset = 127462;
      int asciiOffset = 65;
      int firstChar = Character.codePointAt(isoCode, 0) - asciiOffset + flagOffset;
      int secondChar = Character.codePointAt(isoCode, 1) - asciiOffset + flagOffset;
      String emoji = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
      return canShowFlagEmoji(emoji) ? emoji : "";
    }

    private static boolean areFlagsSupported() {
      return VERSION.SDK_INT >= 23;
    }

    @TargetApi(23)
    private static boolean canShowFlagEmoji(String flagEmoji) {
      return (new Paint()).hasGlyph(flagEmoji);
    }

    String getCountryCode() {
      return this.countryCode;
    }

    String getCountrySymbol() {
      return this.countrySymbol;
    }

    String getCountryName() {
      return this.countryName;
    }
  }
}
