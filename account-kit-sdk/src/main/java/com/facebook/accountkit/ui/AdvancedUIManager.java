package com.facebook.accountkit.ui;

import android.app.Fragment;

import androidx.annotation.Nullable;

/**
 * @deprecated
 */
public interface AdvancedUIManager extends UIManagerStub {
  /**
   * @deprecated
   */
  @Nullable
  Fragment getActionBarFragment(LoginFlowState var1);

  /**
   * @deprecated
   */
  void setAdvancedUIManagerListener(AdvancedUIManager.AdvancedUIManagerListener var1);

  /**
   * @deprecated
   */
  @Deprecated
  public interface AdvancedUIManagerListener extends UIManager.UIManagerListener {
  }
}
