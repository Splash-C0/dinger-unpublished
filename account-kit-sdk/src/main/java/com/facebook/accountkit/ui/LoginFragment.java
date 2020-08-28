package com.facebook.accountkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract class LoginFragment extends ViewStateFragment {
  LoginFragment() {
  }

  protected abstract View createView(LayoutInflater var1, ViewGroup var2, Bundle var3);

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    if (view == null) {
      view = this.createView(inflater, container, savedInstanceState);
    }

    ViewUtility.applyThemeAttributes(this.getActivity(), this.getUIManager(), view);
    return view;
  }
}
