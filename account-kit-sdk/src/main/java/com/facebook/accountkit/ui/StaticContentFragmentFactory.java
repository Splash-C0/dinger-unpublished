package com.facebook.accountkit.ui;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.facebook.accountkit.custom.R.attr;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;

final class StaticContentFragmentFactory {
  private static final String LAYOUT_RESOURCE_ID_KEY = "layoutResourceId";
  private static final String LOGIN_FLOW_STATE_KEY = "loginFlowState";

  StaticContentFragmentFactory() {
  }

  static StaticContentFragmentFactory.StaticContentFragment create(@NonNull UIManager uiManager, LoginFlowState loginFlowState, int layoutResourceId) {
    StaticContentFragmentFactory.StaticContentFragment fragment = create(uiManager, loginFlowState);
    fragment.getViewState().putInt("layoutResourceId", layoutResourceId);
    return fragment;
  }

  static StaticContentFragmentFactory.StaticContentFragment create(@NonNull UIManager uiManager, LoginFlowState loginFlowState) {
    StaticContentFragmentFactory.StaticContentFragment fragment = new StaticContentFragmentFactory.StaticContentFragment();
    Bundle viewState = fragment.getViewState();
    viewState.putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
    viewState.putString("loginFlowState", loginFlowState.name());
    return fragment;
  }

  public static final class StaticContentFragment extends ContentFragment {
    public StaticContentFragment() {
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(this.getViewState().getInt("layoutResourceId", layout.com_accountkit_fragment_static_content), container, false);
    }

    LoginFlowState getLoginFlowState() {
      return LoginFlowState.valueOf(this.getViewState().getString("loginFlowState", LoginFlowState.NONE.name()));
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      View iconView = view.findViewById(id.com_accountkit_icon_view);
      if (iconView != null) {
        int color = ViewUtility.useLegacy(this.getUIManager()) ? ViewUtility.getColor((Context) this.getActivity(), attr.com_accountkit_icon_color, -1) : ViewUtility.getPrimaryColor(this.getActivity(), this.getUIManager());
        if (iconView instanceof ImageView) {
          ImageView iconImgView = (ImageView) iconView;
          ViewUtility.applyThemeColor(this.getActivity(), (ImageView) iconImgView, color);
          String loginState = viewState.getString("loginFlowState");
          if (iconImgView.getDrawable() instanceof Animatable) {
            ((Animatable) iconImgView.getDrawable()).start();
          }
        } else {
          ViewUtility.applyThemeColor(this.getActivity(), (Drawable) iconView.getBackground(), color);
        }
      }

    }
  }
}
