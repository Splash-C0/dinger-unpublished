package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

abstract class ViewStateFragment extends Fragment {
  public static final String TAG = ViewStateFragment.class.getSimpleName();
  protected static final String UI_MANAGER_KEY;
  private static final String VIEW_STATE_KEY;

  static {
    VIEW_STATE_KEY = TAG + ".VIEW_STATE_KEY";
    UI_MANAGER_KEY = TAG + ".UI_MANAGER_KEY";
  }

  private final Bundle viewState = new Bundle();

  ViewStateFragment() {
  }

  @Nullable
  protected UIManager getUIManager() {
    return (UIManager) this.viewState.get(UI_MANAGER_KEY);
  }

  protected Bundle getViewState() {
    return this.viewState;
  }

  public void onCreate(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      this.viewState.putAll(savedInstanceState.getBundle(VIEW_STATE_KEY));
    }

    if (!this.viewState.containsKey(UI_MANAGER_KEY)) {
      throw new RuntimeException("You must supply a UIManager to " + TAG);
    } else {
      super.onCreate(savedInstanceState);
      this.setRetainInstance(true);
    }
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View view = this.getView();
    if (view != null) {
      this.onViewReadyWithState(view, this.viewState);
    }

  }

  public void onSaveInstanceState(Bundle outState) {
    outState.putBundle(VIEW_STATE_KEY, this.viewState);
    super.onSaveInstanceState(outState);
  }

  protected void onViewReadyWithState(View view, Bundle viewState) {
  }
}
