package com.facebook.accountkit.internal;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class LocaleMapper {
  private static final String DEFAULT_LOCALE = "en_US";
  private static final Map<String, String> LANGUAGE_MAP = new HashMap();
  private static final Map<String, String> LOCALE_MAP = new HashMap();

  static {
    LANGUAGE_MAP.put("af", "af_ZA");
    LANGUAGE_MAP.put("ar", "ar_AR");
    LANGUAGE_MAP.put("az", "az_AZ");
    LANGUAGE_MAP.put("be", "be_BY");
    LANGUAGE_MAP.put("bg", "bg_BG");
    LANGUAGE_MAP.put("bn", "bn_IN");
    LANGUAGE_MAP.put("bs", "bs_BA");
    LANGUAGE_MAP.put("ca", "ca_ES");
    LANGUAGE_MAP.put("ck", "ck_US");
    LANGUAGE_MAP.put("cs", "cs_CZ");
    LANGUAGE_MAP.put("cy", "cy_GB");
    LANGUAGE_MAP.put("da", "da_DK");
    LANGUAGE_MAP.put("de", "de_DE");
    LANGUAGE_MAP.put("el", "el_GR");
    LANGUAGE_MAP.put("eo", "eo_EO");
    LANGUAGE_MAP.put("et", "et_EE");
    LANGUAGE_MAP.put("es", "es_LA");
    LANGUAGE_MAP.put("eu", "eu_ES");
    LANGUAGE_MAP.put("fa", "fa_IR");
    LANGUAGE_MAP.put("fi", "fi_FI");
    LANGUAGE_MAP.put("fil", "tl_PH");
    LANGUAGE_MAP.put("fo", "fo_FO");
    LANGUAGE_MAP.put("fr", "fr_FR");
    LANGUAGE_MAP.put("fy", "fy_NL");
    LANGUAGE_MAP.put("ga", "ga_IE");
    LANGUAGE_MAP.put("gl", "gl_ES");
    LANGUAGE_MAP.put("gu", "gu_IN");
    LANGUAGE_MAP.put("he", "he_IL");
    LANGUAGE_MAP.put("hi", "hi_IN");
    LANGUAGE_MAP.put("hr", "hr_HR");
    LANGUAGE_MAP.put("hu", "hu_HU");
    LANGUAGE_MAP.put("hy", "hy_AM");
    LANGUAGE_MAP.put("id", "id_ID");
    LANGUAGE_MAP.put("in", "id_ID");
    LANGUAGE_MAP.put("is", "is_IS");
    LANGUAGE_MAP.put("it", "it_IT");
    LANGUAGE_MAP.put("iw", "he_IL");
    LANGUAGE_MAP.put("ja", "ja_JP");
    LANGUAGE_MAP.put("ka", "ka_GE");
    LANGUAGE_MAP.put("km", "km_KH");
    LANGUAGE_MAP.put("kn", "kn_IN");
    LANGUAGE_MAP.put("ko", "ko_KR");
    LANGUAGE_MAP.put("ku", "ku_TR");
    LANGUAGE_MAP.put("la", "la_VA");
    LANGUAGE_MAP.put("lv", "lv_LV");
    LANGUAGE_MAP.put("mk", "mk_MK");
    LANGUAGE_MAP.put("ml", "ml_IN");
    LANGUAGE_MAP.put("mr", "mr_IN");
    LANGUAGE_MAP.put("ms", "ms_MY");
    LANGUAGE_MAP.put("nb", "nb_NO");
    LANGUAGE_MAP.put("ne", "ne_NP");
    LANGUAGE_MAP.put("nl", "nl_NL");
    LANGUAGE_MAP.put("nn", "nn_NO");
    LANGUAGE_MAP.put("pa", "pa_IN");
    LANGUAGE_MAP.put("pl", "pl_PL");
    LANGUAGE_MAP.put("ps", "ps_AF");
    LANGUAGE_MAP.put("pt", "pt_BR");
    LANGUAGE_MAP.put("ro", "ro_RO");
    LANGUAGE_MAP.put("ru", "ru_RU");
    LANGUAGE_MAP.put("sk", "sk_SK");
    LANGUAGE_MAP.put("sl", "sl_SI");
    LANGUAGE_MAP.put("sq", "sq_AL");
    LANGUAGE_MAP.put("sr", "sr_RS");
    LANGUAGE_MAP.put("sv", "sv_SE");
    LANGUAGE_MAP.put("sw", "sw_KE");
    LANGUAGE_MAP.put("ta", "ta_IN");
    LANGUAGE_MAP.put("te", "te_IN");
    LANGUAGE_MAP.put("th", "th_TH");
    LANGUAGE_MAP.put("tl", "tl_PH");
    LANGUAGE_MAP.put("tr", "tr_TR");
    LANGUAGE_MAP.put("uk", "uk_UA");
    LANGUAGE_MAP.put("ur", "ur_PK");
    LANGUAGE_MAP.put("vi", "vi_VN");
    LANGUAGE_MAP.put("zh", "zh_CN");
    LOCALE_MAP.put("es_ES", "es_ES");
    LOCALE_MAP.put("fr_CA", "fr_CA");
    LOCALE_MAP.put("pt_PT", "pt_PT");
    LOCALE_MAP.put("zh_TW", "zh_TW");
    LOCALE_MAP.put("zh_HK", "zh_HK");
    LOCALE_MAP.put("fb_HA", "fb_HA");
  }

  LocaleMapper() {
  }

  public static String getSystemLocale() {
    Locale loc = Resources.getSystem().getConfiguration().locale;
    String language = loc.getLanguage();
    String country = loc.getCountry();
    String localeString = String.format("%s_%s", language, country);
    if (LOCALE_MAP.containsKey(localeString)) {
      return (String) LOCALE_MAP.get(localeString);
    } else {
      String result = (String) LANGUAGE_MAP.get(language);
      return result != null ? result : "en_US";
    }
  }
}
