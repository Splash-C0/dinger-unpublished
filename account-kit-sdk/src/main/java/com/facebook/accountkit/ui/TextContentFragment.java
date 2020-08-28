package com.facebook.accountkit.ui;

import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;

abstract class TextContentFragment extends ContentFragment {
  private static final String CONTENT_PADDING_BOTTOM_KEY = "contentPaddingBottom";
  private static final String CONTENT_PADDING_TOP_KEY = "contentPaddingTop";
  private TextContentFragment.NextButtonTextProvider nextButtonTextProvider;
  private TextView textView;

  TextContentFragment() {
  }

  protected abstract Spanned getText(String var1);

  public int getContentPaddingBottom() {
    return this.getViewState().getInt("contentPaddingBottom", 0);
  }

  public void setContentPaddingBottom(int contentPaddingBottom) {
    this.getViewState().putInt("contentPaddingBottom", contentPaddingBottom);
    this.updateContentPadding();
  }

  public int getContentPaddingTop() {
    return this.getViewState().getInt("contentPaddingTop", 0);
  }

  public void setContentPaddingTop(int contentPaddingTop) {
    this.getViewState().putInt("contentPaddingTop", contentPaddingTop);
    this.updateContentPadding();
  }

  protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(layout.com_accountkit_fragment_phone_login_text, container, false);
  }

  public void setNextButtonTextProvider(TextContentFragment.NextButtonTextProvider nextButtonTextProvider) {
    this.nextButtonTextProvider = nextButtonTextProvider;
  }

  protected void onViewReadyWithState(View view, Bundle viewState) {
    super.onViewReadyWithState(view, viewState);
    this.textView = (TextView) view.findViewById(id.com_accountkit_text);
    if (this.textView != null) {
      this.textView.setMovementMethod(new CustomLinkMovement(new CustomLinkMovement.OnURLClickedListener() {
        public void onURLClicked(String url) {
        }
      }));
    }

    this.updateContentPadding();
    this.updateText();
  }

  public void onStart() {
    super.onStart();
    this.updateText();
  }

  private void updateContentPadding() {
    if (this.textView != null) {
      int contentPaddingTop = this.getContentPaddingTop();
      int contentPaddingBottom = this.getContentPaddingBottom();
      this.textView.setPadding(this.textView.getPaddingLeft(), contentPaddingTop, this.textView.getPaddingRight(), contentPaddingBottom);
    }
  }

  void updateText() {
    if (this.textView != null && this.nextButtonTextProvider != null) {
      if (this.getActivity() != null) {
        this.textView.setText(this.getText(this.nextButtonTextProvider.getNextButtonText()));
      }
    }
  }

  public interface NextButtonTextProvider {
    String getNextButtonText();
  }
}
