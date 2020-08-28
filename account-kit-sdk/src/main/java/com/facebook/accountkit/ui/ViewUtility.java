package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;

import com.facebook.accountkit.custom.R.attr;
import com.facebook.accountkit.custom.R.color;
import com.facebook.accountkit.custom.R.dimen;

final class ViewUtility {
  private static final double TEXT_COLOR_CONTRAST_THRESHOLD = 1.5D;

  ViewUtility() {
  }

  static void applyThemeAttributes(Context context, UIManager uiManager, View view) {
    if (context != null && view != null) {
      if (view instanceof Button) {
        applyButtonThemeAttributes(context, uiManager, (Button) view);
      } else if (view instanceof EditText) {
        applyInputThemeAttributes(context, uiManager, (EditText) view);
      } else if (view instanceof ProgressBar) {
        applyProgressBarThemeAttributes(context, uiManager, (ProgressBar) view);
      } else if (view instanceof CountryCodeSpinner) {
        applySpinnerThemeAttributes(context, uiManager, (CountryCodeSpinner) view);
      } else if (view instanceof TextView) {
        applyTextViewThemeAttributes(context, uiManager, (TextView) view);
      } else if (view instanceof ViewGroup) {
        ViewGroup viewGroup = (ViewGroup) view;
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; ++i) {
          applyThemeAttributes(context, uiManager, viewGroup.getChildAt(i));
        }
      }

    }
  }

  static void applyThemeBackground(Context context, UIManager uiManager, View view) {
    if (context != null && view != null) {
      if (uiManager instanceof SkinManager) {
        applySkinThemedBackground(context, (SkinManager) uiManager, view);
      } else {
        applyLegacyThemedBackground(context, view);
      }

    }
  }

  private static void applySkinThemedBackground(Context context, SkinManager skinManager, View view) {
    Drawable background = skinManager.hasBackgroundImage() ? getDrawable(context.getResources(), skinManager.getBackgroundImageResId()) : new ColorDrawable(ContextCompat.getColor(context, color.com_accountkit_default_skin_background));
    if (skinManager.hasBackgroundImage()) {
      if (view instanceof AspectFrameLayout) {
        ((AspectFrameLayout) view).setAspectWidth(((Drawable) background).getIntrinsicWidth());
        ((AspectFrameLayout) view).setAspectHeight(((Drawable) background).getIntrinsicHeight());
      }

      ((Drawable) background).setColorFilter(skinManager.getTintColor(), Mode.SRC_ATOP);
    }

    setBackground(view, (Drawable) background);
  }

  private static Drawable getLegacyThemedBackground(Context context, View view) {
    Theme theme = context.getTheme();
    TypedValue drawableValue = new TypedValue();
    theme.resolveAttribute(attr.com_accountkit_background, drawableValue, true);
    Drawable drawable = drawableValue.resourceId == 0 ? new ColorDrawable(getColor((Context) context, attr.com_accountkit_background_color, -1)) : getDrawable(context.getResources(), drawableValue.resourceId);
    if (drawableValue.resourceId > 0) {
      if (view instanceof AspectFrameLayout) {
        ((AspectFrameLayout) view).setAspectWidth(((Drawable) drawable).getIntrinsicWidth());
        ((AspectFrameLayout) view).setAspectHeight(((Drawable) drawable).getIntrinsicHeight());
      }

      int color = getColor((Context) context, attr.com_accountkit_background_color, -1);
      applyThemeColor(context, (Drawable) drawable, color);
    }

    return (Drawable) drawable;
  }

  private static void applyLegacyThemedBackground(Context context, View view) {
    Drawable drawable = getLegacyThemedBackground(context, view);
    setBackground(view, drawable);
  }

  static void applyThemeColor(Context context, Drawable drawable, @ColorInt int color) {
    if (context != null && drawable != null) {
      drawable.setColorFilter(color, Mode.SRC_ATOP);
    }
  }

  static void applyThemeColor(Context context, ImageView imageView, @ColorInt int color) {
    if (context != null && imageView != null) {
      imageView.setColorFilter(color, Mode.SRC_ATOP);
    }
  }

  static int getDimensionPixelSize(Context context, int size) {
    return (int) TypedValue.applyDimension(1, (float) size, context.getResources().getDisplayMetrics());
  }

  static boolean isTablet(Context context) {
    boolean xlarge = (context.getResources().getConfiguration().screenLayout & 15) == 4;
    boolean large = (context.getResources().getConfiguration().screenLayout & 15) == 3;
    return xlarge || large;
  }

  static void showKeyboard(View view) {
    if (view != null && view.getContext() != null) {
      if (view.requestFocus()) {
        ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
      }

    }
  }

  static void hideKeyboard(Activity activity) {
    View rootView = activity.findViewById(16908290);
    if (rootView != null) {
      View focusView = rootView.findFocus();
      if (focusView != null) {
        focusView.clearFocus();
      }

      ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(rootView.getWindowToken(), 1);
    }
  }

  static boolean useLegacy(UIManager uiManager) {
    return !(uiManager instanceof SkinManager);
  }

  static boolean isSkin(UIManager uiManager, SkinManager.Skin skin) {
    return uiManager instanceof SkinManager && ((SkinManager) uiManager).getSkin() == skin;
  }

  private static void applyButtonThemeAttributes(Context context, UIManager uiManager, Button button) {
    int enabledFilledColor;
    int enabledBorderColor;
    int pressedFilledColor;
    int pressedBorderColor;
    int primaryColor;
    int disabledColor;
    if (button instanceof WhatsAppButton) {
      primaryColor = ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_background);
      disabledColor = ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_border);
      enabledFilledColor = primaryColor;
      enabledBorderColor = disabledColor;
      pressedFilledColor = primaryColor;
      pressedBorderColor = disabledColor;
    } else if (!useLegacy(uiManager)) {
      primaryColor = getPrimaryColor(context, uiManager);
      enabledFilledColor = primaryColor;
      enabledBorderColor = primaryColor;
      pressedFilledColor = isSkin(uiManager, SkinManager.Skin.TRANSLUCENT) ? 0 : primaryColor;
      pressedBorderColor = primaryColor;
    } else {
      enabledFilledColor = getButtonColor(context, uiManager);
      enabledBorderColor = getColor(context, attr.com_accountkit_button_border_color, enabledFilledColor);
      pressedFilledColor = getColor(context, attr.com_accountkit_button_pressed_background_color, -3355444);
      pressedBorderColor = getColor(context, attr.com_accountkit_button_pressed_border_color, pressedFilledColor);
    }

    int disabledFilledColor;
    int disabledBorderColor;
    if (button instanceof WhatsAppButton) {
      primaryColor = ContextCompat.getColor(context, color.com_accountkit_whatsapp_disable_background);
      disabledColor = ContextCompat.getColor(context, color.com_accountkit_whatsapp_disable_border);
      disabledFilledColor = primaryColor;
      disabledBorderColor = disabledColor;
    } else if (!useLegacy(uiManager)) {
      primaryColor = getPrimaryColor(context, uiManager);
      disabledColor = ((SkinManager) uiManager).getDisabledColor(primaryColor);
      disabledFilledColor = isSkin(uiManager, SkinManager.Skin.TRANSLUCENT) ? 0 : disabledColor;
      disabledBorderColor = isSkin(uiManager, SkinManager.Skin.TRANSLUCENT) ? primaryColor : disabledColor;
    } else {
      disabledFilledColor = getColor(context, attr.com_accountkit_button_disabled_background_color, -3355444);
      disabledBorderColor = getColor(context, attr.com_accountkit_button_disabled_border_color, disabledFilledColor);
    }

    setBackground(button, getButtonBackgroundDrawable(context, enabledFilledColor, enabledBorderColor, pressedFilledColor, pressedBorderColor, disabledFilledColor, disabledBorderColor));
    ColorStateList buttonTextColor = button instanceof WhatsAppButton ? getWhatsAppButtonTextColorStateList(context, uiManager) : getButtonTextColorStateList(context, uiManager);
    button.setTextColor(buttonTextColor);
    Drawable[] drawables = button.getCompoundDrawables();
    if (drawables.length >= 4) {
      Drawable[] var11 = drawables;
      int var12 = drawables.length;

      for (int var13 = 0; var13 < var12; ++var13) {
        Drawable drawable = var11[var13];
        if (drawable != null) {
          DrawableCompat.setTintList(DrawableCompat.wrap(drawable), buttonTextColor);
        }
      }
    }

    if (button instanceof WhatsAppButton) {
      ((WhatsAppButton) button).imageSpanColorReset();
    }

  }

  private static Drawable getButtonBackgroundDrawable(Context context, int enabledFilledColor, int enabledBorderColor, int pressedFilledColor, int pressedBorderColor, int disabledFilledColor, int disabledBorderColor) {
    StateListDrawable background = new StateListDrawable();
    if (VERSION.SDK_INT >= 21) {
      background.addState(new int[]{-16842910}, new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{enabledFilledColor}), getInputBackgroundDrawable(context, disabledFilledColor, disabledBorderColor), (Drawable) null));
      background.addState(new int[0], new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{disabledFilledColor}), getInputBackgroundDrawable(context, enabledFilledColor, enabledBorderColor), (Drawable) null));
    } else {
      background.addState(new int[]{-16842910}, getInputBackgroundDrawable(context, disabledFilledColor, disabledBorderColor));
      background.addState(new int[]{16842919}, getInputBackgroundDrawable(context, pressedFilledColor, pressedBorderColor));
      background.addState(new int[0], getInputBackgroundDrawable(context, enabledFilledColor, enabledBorderColor));
    }

    return background;
  }

  @ColorInt
  static int getButtonTextColor(Context context, UIManager uiManager) {
    int buttonTextColor;
    if (!useLegacy(uiManager)) {
      buttonTextColor = ((SkinManager) uiManager).getTextColor();
    } else {
      buttonTextColor = getColor(context, attr.com_accountkit_button_text_color, -16777216);
    }

    return buttonTextColor;
  }

  private static ColorStateList getButtonTextColorStateList(Context context, UIManager uiManager) {
    int[][] states = new int[][]{{-16842910}, {16842919}, new int[0]};
    int[] buttonTextColors;
    if (!useLegacy(uiManager)) {
      int textColor = ((SkinManager) uiManager).getTextColor();
      buttonTextColors = new int[]{textColor, textColor, textColor};
    } else {
      buttonTextColors = new int[]{getColor(context, attr.com_accountkit_button_disabled_text_color, -3355444), getColor(context, attr.com_accountkit_button_pressed_text_color, -12303292), getColor(context, attr.com_accountkit_button_text_color, -16777216)};
    }

    return new ColorStateList(states, buttonTextColors);
  }

  private static ColorStateList getWhatsAppButtonTextColorStateList(Context context, UIManager uiManager) {
    int[][] states = new int[][]{{-16842910}, {16842919}, new int[0]};
    int[] buttonTextColors;
    if (!useLegacy(uiManager)) {
      int textColor = ((SkinManager) uiManager).getTextColor();
      buttonTextColors = new int[]{ContextCompat.getColor(context, color.com_accountkit_whatsapp_disable_text), ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_text), ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_text)};
    } else {
      buttonTextColors = new int[]{ContextCompat.getColor(context, color.com_accountkit_whatsapp_disable_text), ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_text), ContextCompat.getColor(context, color.com_accountkit_whatsapp_enable_text)};
    }

    return new ColorStateList(states, buttonTextColors);
  }

  private static void applyTextViewThemeAttributes(Context context, UIManager uiManager, TextView textView) {
    int textColor = useLegacy(uiManager) ? getColor(context, attr.com_accountkit_text_color, ContextCompat.getColor(context, 17170433)) : ((SkinManager) uiManager).getTextColor();
    textView.setTextColor(textColor);
    textView.setLinkTextColor(textColor);
  }

  static boolean doesTextColorContrast(Context context, UIManager uiManager) {
    Theme theme;
    if (uiManager.getThemeId() != -1) {
      theme = context.getResources().newTheme();
      theme.setTo(context.getTheme());
      theme.applyStyle(uiManager.getThemeId(), true);
    } else {
      theme = context.getTheme();
    }

    int textColor = useLegacy(uiManager) ? getColor(theme, attr.com_accountkit_text_color, ContextCompat.getColor(context, 17170433)) : ((SkinManager) uiManager).getTextColor();
    int dominantColor = useLegacy(uiManager) ? getColor((Theme) theme, attr.com_accountkit_background_color, -1) : ((SkinManager) uiManager).getTintColor();
    double contrast = ColorUtils.calculateContrast(textColor | -16777216, dominantColor | -16777216);
    return contrast >= 1.5D;
  }

  private static void applyInputThemeAttributes(Context context, UIManager uiManager, EditText input) {
    if (!useLegacy(uiManager)) {
      input.setTextColor(((SkinManager) uiManager).getTextColor());
    }

    if (isSkin(uiManager, SkinManager.Skin.CONTEMPORARY)) {
      int primaryColor = getPrimaryColor(context, uiManager);
      Drawable draw = DrawableCompat.wrap(input.getBackground()).mutate();
      DrawableCompat.setTint(draw, primaryColor);
      setBackground(input, draw);
      input.setTextColor(((SkinManager) uiManager).getTextColor());
    } else {
      applyInputThemeAttributes(context, uiManager, (View) input);
    }

  }

  private static void applyInputThemeAttributes(Context context, UIManager uiManager, View input) {
    int fillColor;
    if (useLegacy(uiManager)) {
      fillColor = getColor(context, attr.com_accountkit_input_background_color, -3355444);
      setBackground(input, getInputBackgroundDrawable(context, fillColor, getColor(context, attr.com_accountkit_input_border_color, fillColor)));
    } else if (isSkin(uiManager, SkinManager.Skin.TRANSLUCENT)) {
      fillColor = getPrimaryColor(context, uiManager);
      setBackground(input, getInputBackgroundDrawable(context, 0, fillColor));
    } else {
      fillColor = ((SkinManager) uiManager).getDisabledColor(getPrimaryColor(context, uiManager));
      setBackground(input, getInputBackgroundDrawable(context, fillColor, fillColor));
    }

  }

  private static void applyProgressBarThemeAttributes(Context context, UIManager uiManager, ProgressBar progressBar) {
    Drawable drawable = progressBar.getIndeterminateDrawable();
    int color;
    if (useLegacy(uiManager)) {
      color = getColor(context, attr.com_accountkit_icon_color, -16777216);
    } else {
      color = getPrimaryColor(context, uiManager);
    }

    applyThemeColor(context, drawable, color);
  }

  private static void applySpinnerThemeAttributes(Context context, UIManager uiManager, CountryCodeSpinner spinner) {
    ViewGroup viewParent = (ViewGroup) spinner.getParent();
    ImageView countryCodeImage = (ImageView) viewParent.getChildAt(1);
    View underline = viewParent.getChildAt(2);
    Drawable arrowImage = DrawableCompat.wrap(countryCodeImage.getDrawable()).mutate();
    if (isSkin(uiManager, SkinManager.Skin.CONTEMPORARY)) {
      underline.setVisibility(0);
      setBackground(underline, new ColorDrawable(getPrimaryColor(context, uiManager)));
      DrawableCompat.setTint(arrowImage, getPrimaryColor(context, uiManager));
    } else if (!isSkin(uiManager, SkinManager.Skin.TRANSLUCENT) && !isSkin(uiManager, SkinManager.Skin.CLASSIC)) {
      underline.setVisibility(8);
      DrawableCompat.setTint(arrowImage, getColor(context, attr.com_accountkit_input_accent_color, -16777216));
      applyInputThemeAttributes(context, uiManager, (View) viewParent);
    } else {
      underline.setVisibility(8);
      DrawableCompat.setTint(arrowImage, ((SkinManager) uiManager).getTextColor());
      applyInputThemeAttributes(context, uiManager, (View) viewParent);
    }

  }

  @ColorInt
  static int getButtonColor(Context context, UIManager uiManager) {
    int buttonColor;
    if (uiManager instanceof SkinManager) {
      buttonColor = ((SkinManager) uiManager).getPrimaryColor();
    } else {
      buttonColor = getColor(context, attr.com_accountkit_button_background_color, -3355444);
    }

    return buttonColor;
  }

  @ColorInt
  static int getPrimaryColor(Context context, UIManager uiManager) {
    int color;
    if (uiManager instanceof SkinManager) {
      color = ((SkinManager) uiManager).getPrimaryColor();
    } else {
      color = getColor(context, attr.com_accountkit_primary_color, -3355444);
    }

    return color;
  }

  @ColorInt
  static int getColor(Context context, @AttrRes int id, int defaultValue) {
    return getColor(context.getTheme(), id, defaultValue);
  }

  @ColorInt
  static int getColor(Theme theme, @AttrRes int id, int defaultValue) {
    TypedValue colorValue = new TypedValue();
    return theme.resolveAttribute(id, colorValue, true) ? colorValue.data : defaultValue;
  }

  private static Drawable getDrawable(Resources resources, int id) {
    return VERSION.SDK_INT < 22 ? resources.getDrawable(id) : resources.getDrawable(id, (Theme) null);
  }

  private static Drawable getInputBackgroundDrawable(Context context, @ColorInt int fillColor, @ColorInt int borderColor) {
    GradientDrawable drawable = new GradientDrawable();
    Resources resources = context.getResources();
    drawable.setColor(fillColor);
    drawable.setCornerRadius(resources.getDimension(dimen.com_accountkit_input_corner_radius));
    drawable.setStroke(resources.getDimensionPixelSize(dimen.com_accountkit_input_border), borderColor);
    return drawable;
  }

  static void setBackground(View view, Drawable drawable) {
    if (VERSION.SDK_INT < 16) {
      view.setBackgroundDrawable(drawable);
    } else {
      view.setBackground(drawable);
    }

  }
}
