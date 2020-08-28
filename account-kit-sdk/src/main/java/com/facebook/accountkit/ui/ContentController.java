package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

interface ContentController {
  ContentFragment getBottomFragment();

  void setBottomFragment(@Nullable ContentFragment var1);

  ContentFragment getCenterFragment();

  void setCenterFragment(@Nullable ContentFragment var1);

  @Nullable
  View getFocusView();

  TitleFragmentFactory.TitleFragment getFooterFragment();

  void setFooterFragment(@Nullable TitleFragmentFactory.TitleFragment var1);

  TitleFragmentFactory.TitleFragment getHeaderFragment();

  void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment var1);

  LoginFlowState getLoginFlowState();

  @Nullable
  ContentFragment getTextFragment();

  void setTextFragment(@Nullable ContentFragment var1);

  @Nullable
  ContentFragment getTopFragment();

  void setTopFragment(@Nullable ContentFragment var1);

  boolean isTransient();

  void onResume(Activity var1);

  void onPause(Activity var1);

  void onActivityResult(int var1, int var2, Intent var3);
}
