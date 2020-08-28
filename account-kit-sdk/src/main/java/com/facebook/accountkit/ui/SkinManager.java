package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Parcel;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public final class SkinManager extends BaseUIManager {
  public static final Creator<SkinManager> CREATOR = new Creator<SkinManager>() {
    public SkinManager createFromParcel(Parcel source) {
      return new SkinManager(source);
    }

    public SkinManager[] newArray(int size) {
      return new SkinManager[size];
    }
  };
  private static final double MAXIMUM_TINT_INTENSITY = 0.85D;
  private static final double MINIMUM_TINT_INTENSITY = 0.55D;
  private static final double DISABLED_COLOR_ALPHA = 0.25D;
  private final SkinManager.Skin skin;
  @ColorInt
  private final int primaryColor;
  @DrawableRes
  private final int backgroundImage;
  private final SkinManager.Tint tint;
  private final double tintIntensity;

  public SkinManager(SkinManager.Skin skin, @ColorInt int primaryColor, @DrawableRes int backgroundImage, SkinManager.Tint tint, double tintIntensity) {
    super(-1);
    this.skin = skin;
    this.primaryColor = primaryColor;
    this.backgroundImage = backgroundImage;
    if (this.hasBackgroundImage()) {
      this.tint = tint;
      this.tintIntensity = Math.min(0.85D, Math.max(0.55D, tintIntensity));
    } else {
      this.tint = SkinManager.Tint.WHITE;
      this.tintIntensity = 0.55D;
    }

  }

  public SkinManager(SkinManager.Skin skin, @ColorInt int primaryColor) {
    this(skin, primaryColor, -1, SkinManager.Tint.WHITE, 0.55D);
  }

  private SkinManager(Parcel source) {
    super(source);
    this.skin = SkinManager.Skin.values()[source.readInt()];
    this.primaryColor = source.readInt();
    this.backgroundImage = source.readInt();
    this.tint = SkinManager.Tint.values()[source.readInt()];
    this.tintIntensity = source.readDouble();
  }

  public SkinManager.Skin getSkin() {
    return this.skin;
  }

  public boolean hasBackgroundImage() {
    return this.backgroundImage >= 0;
  }

  @DrawableRes
  int getBackgroundImageResId() {
    return this.backgroundImage;
  }

  public SkinManager.Tint getTint() {
    return this.tint;
  }

  public double getTintIntensity() {
    return this.tintIntensity;
  }

  @ColorInt
  int getDisabledColor(@ColorInt int color) {
    int backgroundColor;
    switch (this.tint) {
      case WHITE:
        backgroundColor = -1;
        break;
      case BLACK:
      default:
        backgroundColor = -16777216;
    }

    double r = 0.25D * (double) Color.red(color) + 0.75D * (double) Color.red(backgroundColor);
    double g = 0.25D * (double) Color.green(color) + 0.75D * (double) Color.green(backgroundColor);
    double b = 0.25D * (double) Color.blue(color) + 0.75D * (double) Color.blue(backgroundColor);
    return Color.rgb((int) r, (int) g, (int) b);
  }

  @ColorInt
  public int getPrimaryColor() {
    return this.primaryColor;
  }

  @ColorInt
  int getTintColor() {
    int tintInt;
    switch (this.tint) {
      case WHITE:
        tintInt = Color.argb((int) (255.0D * this.tintIntensity), 255, 255, 255);
        break;
      case BLACK:
      default:
        tintInt = Color.argb((int) (255.0D * this.tintIntensity), 0, 0, 0);
    }

    return tintInt;
  }

  @ColorInt
  int getTextColor() {
    int textColor;
    switch (this.getTint()) {
      case WHITE:
      default:
        textColor = -16777216;
        break;
      case BLACK:
        textColor = -1;
    }

    return textColor;
  }

  @Nullable
  public Fragment getBodyFragment(LoginFlowState state) {
    return super.getBodyFragment(state);
  }

  @Nullable
  public ButtonType getButtonType(LoginFlowState state) {
    return super.getButtonType(state);
  }

  @Nullable
  public Fragment getFooterFragment(LoginFlowState state) {
    return super.getFooterFragment(state);
  }

  @Nullable
  public Fragment getHeaderFragment(LoginFlowState state) {
    return super.getHeaderFragment(state);
  }

  @Nullable
  public TextPosition getTextPosition(LoginFlowState state) {
    return super.getTextPosition(state);
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.skin.ordinal());
    dest.writeInt(this.primaryColor);
    dest.writeInt(this.backgroundImage);
    dest.writeInt(this.tint.ordinal());
    dest.writeDouble(this.tintIntensity);
  }

  public static enum Tint {
    WHITE,
    BLACK;

    private Tint() {
    }
  }

  public static enum Skin {
    NONE,
    CLASSIC,
    CONTEMPORARY,
    TRANSLUCENT;

    private Skin() {
    }
  }
}
