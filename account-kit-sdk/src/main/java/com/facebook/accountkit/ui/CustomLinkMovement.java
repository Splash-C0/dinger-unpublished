package com.facebook.accountkit.ui;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

final class CustomLinkMovement extends LinkMovementMethod {
  private final CustomLinkMovement.OnURLClickedListener listener;

  public CustomLinkMovement(CustomLinkMovement.OnURLClickedListener listener) {
    this.listener = listener;
  }

  public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    if (event.getAction() == 1) {
      int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
      int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();
      Layout layout = widget.getLayout();
      int line = layout.getLineForVertical(y);
      int off = layout.getOffsetForHorizontal(line, (float) x);
      URLSpan[] link = (URLSpan[]) buffer.getSpans(off, off, URLSpan.class);
      if (link.length != 0) {
        String url = link[0].getURL();
        this.listener.onURLClicked(url);
      }
    }

    return super.onTouchEvent(widget, buffer, event);
  }

  interface OnURLClickedListener {
    void onURLClicked(String var1);
  }
}
