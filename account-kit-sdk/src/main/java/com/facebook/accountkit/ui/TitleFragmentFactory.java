package com.facebook.accountkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.internal.Utility;

final class TitleFragmentFactory {
  TitleFragmentFactory() {
  }

  public static TitleFragmentFactory.TitleFragment create(@NonNull UIManager uiManager) {
    TitleFragmentFactory.TitleFragment titleFragment = new TitleFragmentFactory.TitleFragment();
    titleFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
    return titleFragment;
  }

  public static TitleFragmentFactory.TitleFragment create(@NonNull UIManager uiManager, int titleResourceId, @Nullable String... args) {
    TitleFragmentFactory.TitleFragment titleFragment = create(uiManager);
    titleFragment.setTitleResourceId(titleResourceId, args);
    return titleFragment;
  }

  public static class TitleFragment extends LoginFragment {
    private static final String TITLE_KEY = "title";
    private static final String TITLE_RESOURCE_ARGS_KEY = "titleResourceArgs";
    private static final String TITLE_RESOURCE_ID_KEY = "titleResourceId";
    protected TextView titleView;

    public TitleFragment() {
    }

    private String getTitle() {
      return this.getViewState().getString("title");
    }

    public void setTitle(@Nullable String title) {
      this.getViewState().putString("title", title);
      this.updateTitleView();
    }

    @Nullable
    private String[] getTitleResourceArgs() {
      return this.getViewState().getStringArray("titleResourceArgs");
    }

    private int getTitleResourceId() {
      return this.getViewState().getInt("titleResourceId");
    }

    public void setTitleResourceId(int titleResourceId, @Nullable String... args) {
      Bundle viewState = this.getViewState();
      viewState.putInt("titleResourceId", titleResourceId);
      viewState.putStringArray("titleResourceArgs", args);
      this.updateTitleView();
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_title, container, false);
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.titleView = (TextView) view.findViewById(id.com_accountkit_title);
      this.updateTitleView();
    }

    private void updateTitleView() {
      if (this.titleView != null) {
        String title = this.getTitle();
        int titleResourceId = 0;
        if (Utility.isNullOrEmpty(title)) {
          titleResourceId = this.getTitleResourceId();
          String[] titleResourceArgs = this.getTitleResourceArgs();
          if (titleResourceId > 0 && titleResourceArgs != null && titleResourceArgs.length != 0 && this.getActivity() != null) {
            title = this.getString(titleResourceId, (Object[]) titleResourceArgs);
            titleResourceId = 0;
          }
        }

        if (!Utility.isNullOrEmpty(title)) {
          this.titleView.setText(title);
          this.titleView.setVisibility(0);
        } else if (titleResourceId > 0) {
          this.titleView.setText(titleResourceId);
          this.titleView.setVisibility(0);
        } else {
          this.titleView.setVisibility(8);
        }

      }
    }
  }
}
