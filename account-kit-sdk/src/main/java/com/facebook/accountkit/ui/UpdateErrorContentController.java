package com.facebook.accountkit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.UpdateFlowBroadcastReceiver;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;

final class UpdateErrorContentController extends ErrorContentController {
  private UpdateErrorContentController.BottomFragment bottomFragment;

  UpdateErrorContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new UpdateErrorContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof UpdateErrorContentController.BottomFragment) {
      this.bottomFragment = (UpdateErrorContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
    }
  }

  public static final class BottomFragment extends ContentFragment {
    public BottomFragment() {
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_error_bottom, container, false);
    }

    LoginFlowState getLoginFlowState() {
      return LoginFlowState.ERROR;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      View startOverButton = view.findViewById(id.com_accountkit_start_over_button);
      if (startOverButton != null) {
        startOverButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            Intent intent = (new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.RETRY);
            LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
          }
        });
      }

    }
  }
}
