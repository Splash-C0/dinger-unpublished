package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.os.Parcel;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.facebook.accountkit.AccountKitError;

/**
 * @deprecated
 */
@Deprecated
public class AdvancedUIManagerWrapper extends BaseUIManager {
  public static final Creator<AdvancedUIManagerWrapper> CREATOR = new Creator<AdvancedUIManagerWrapper>() {
    public AdvancedUIManagerWrapper createFromParcel(Parcel source) {
      return new AdvancedUIManagerWrapper(source);
    }

    public AdvancedUIManagerWrapper[] newArray(int size) {
      return new AdvancedUIManagerWrapper[size];
    }
  };
  private final AdvancedUIManager advancedUIManager;

  public AdvancedUIManagerWrapper(AdvancedUIManager advancedUIManager, @StyleRes int themeId) {
    super(themeId);
    this.advancedUIManager = advancedUIManager;
  }

  public AdvancedUIManagerWrapper(Parcel source) {
    super(source);
    this.advancedUIManager = (AdvancedUIManager) source.readParcelable(this.getClass().getClassLoader());
  }

  /**
   * @deprecated
   */
  @Deprecated
  public AdvancedUIManager getAdvancedUIManager() {
    return this.advancedUIManager;
  }

  @Nullable
  public Fragment getBodyFragment(LoginFlowState state) {
    Fragment fragment = this.advancedUIManager.getBodyFragment(state);
    if (fragment == null) {
      fragment = super.getBodyFragment(state);
    }

    return fragment;
  }

  @Nullable
  public ButtonType getButtonType(LoginFlowState state) {
    return this.advancedUIManager.getButtonType(state);
  }

  @Nullable
  public Fragment getFooterFragment(LoginFlowState state) {
    Fragment fragment = this.advancedUIManager.getFooterFragment(state);
    if (fragment == null) {
      fragment = super.getFooterFragment(state);
    }

    return fragment;
  }

  @Nullable
  public Fragment getHeaderFragment(LoginFlowState state) {
    Fragment fragment = this.advancedUIManager.getHeaderFragment(state);
    if (fragment == null) {
      fragment = super.getHeaderFragment(state);
    }

    return fragment;
  }

  @Nullable
  public TextPosition getTextPosition(LoginFlowState state) {
    return this.advancedUIManager.getTextPosition(state);
  }

  public void setUIManagerListener(UIManager.UIManagerListener listener) {
    throw new RuntimeException("Use setAdvancedUIManagerListener");
  }

  public void onError(AccountKitError error) {
    this.advancedUIManager.onError(error);
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.advancedUIManager, flags);
  }
}
