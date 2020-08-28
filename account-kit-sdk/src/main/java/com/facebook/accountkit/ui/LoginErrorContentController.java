package com.facebook.accountkit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;

final class LoginErrorContentController extends ErrorContentController {
  private static final LoginFlowState LOGIN_FLOW_STATE;

  static {
    LOGIN_FLOW_STATE = LoginFlowState.ERROR;
  }

  private final LoginFlowState returnState;
  private LoginErrorContentController.BottomFragment bottomFragment;

  LoginErrorContentController(LoginFlowState returnState, AccountKitConfiguration configuration) {
    super(configuration);
    this.returnState = returnState;
  }

  LoginFlowState getReturnState() {
    return this.returnState;
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new LoginErrorContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof LoginErrorContentController.BottomFragment) {
      this.bottomFragment = (LoginErrorContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.bottomFragment.getViewState().putInt(LoginErrorContentController.BottomFragment.RETURN_LOGIN_FLOW_STATE, this.returnState.ordinal());
    }
  }

  public static final class BottomFragment extends ContentFragment {
    private static final String RETURN_LOGIN_FLOW_STATE;

    static {
      RETURN_LOGIN_FLOW_STATE = TAG + ".RETURN_LOGIN_FLOW_STATE";
    }

    public BottomFragment() {
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_error_bottom, container, false);
    }

    LoginFlowState getLoginFlowState() {
      return LoginErrorContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected void onViewReadyWithState(View view, final Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      View startOverButton = view.findViewById(id.com_accountkit_start_over_button);
      if (startOverButton != null) {
        startOverButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            Intent intent = new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE);
            intent.putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.ERROR_RESTART);
            intent.putExtra(LoginFlowBroadcastReceiver.EXTRA_RETURN_LOGIN_FLOW_STATE, (Integer) viewState.get(LoginErrorContentController.BottomFragment.RETURN_LOGIN_FLOW_STATE));
            LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
          }
        });
      }

    }
  }
}
