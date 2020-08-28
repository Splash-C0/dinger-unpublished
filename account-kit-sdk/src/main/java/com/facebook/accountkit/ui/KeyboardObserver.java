package com.facebook.accountkit.ui;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.annotation.Nullable;

final class KeyboardObserver {
  private static final int MINIMUM_KEYBOARD_HEIGHT_DP;

  static {
    MINIMUM_KEYBOARD_HEIGHT_DP = 100 + (VERSION.SDK_INT >= 21 ? 48 : 0);
  }

  private final Rect lastViewVisibleFrame = new Rect();
  private final Rect lastRootViewVisibleFrame = new Rect();
  private final Rect rootViewVisibleFrame = new Rect();
  private boolean didCalculateVisibleFrame = false;
  private KeyboardObserver.OnVisibleFrameChangedListener onVisibleFrameChangedListener;

  public KeyboardObserver(View view) {
    this.configureGlobalObserver(view);
  }

  public void setOnVisibleFrameChangedListener(@Nullable KeyboardObserver.OnVisibleFrameChangedListener onVisibleFrameChangedListener) {
    this.onVisibleFrameChangedListener = onVisibleFrameChangedListener;
    if (this.didCalculateVisibleFrame && onVisibleFrameChangedListener != null) {
      onVisibleFrameChangedListener.onVisibleFrameChanged(this.lastViewVisibleFrame);
    }

  }

  private void configureGlobalObserver(final View view) {
    if (view != null) {
      final View rootView = view.getRootView();
      if (rootView != null) {
        OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {
          public void onGlobalLayout() {
            KeyboardObserver.this.checkVisibleFrame(view, rootView);
          }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        this.checkVisibleFrame(view, rootView);
      }
    }
  }

  private void checkVisibleFrame(View view, View rootView) {
    int minimumKeyboardHeight = ViewUtility.getDimensionPixelSize(rootView.getContext(), MINIMUM_KEYBOARD_HEIGHT_DP);
    rootView.getWindowVisibleDisplayFrame(this.rootViewVisibleFrame);
    int viewHeight = rootView.getHeight();
    int visibleHeight = this.rootViewVisibleFrame.bottom - this.rootViewVisibleFrame.top;
    boolean keyboardIsVisible = viewHeight - visibleHeight >= minimumKeyboardHeight;
    if (keyboardIsVisible && !this.rootViewVisibleFrame.equals(this.lastRootViewVisibleFrame)) {
      this.lastRootViewVisibleFrame.set(this.rootViewVisibleFrame);
      view.getGlobalVisibleRect(this.lastViewVisibleFrame);
      this.didCalculateVisibleFrame = true;
      if (this.onVisibleFrameChangedListener != null) {
        this.onVisibleFrameChangedListener.onVisibleFrameChanged(this.lastViewVisibleFrame);
      }
    }

  }

  public interface OnVisibleFrameChangedListener {
    void onVisibleFrameChanged(Rect var1);
  }
}
