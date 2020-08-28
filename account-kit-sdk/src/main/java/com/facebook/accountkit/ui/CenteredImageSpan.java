package com.facebook.accountkit.ui;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class CenteredImageSpan extends ImageSpan {
  public CenteredImageSpan(Drawable drawable) {
    super(drawable);
  }

  public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
    Drawable drawable = this.getDrawable();
    Rect rect = drawable.getBounds();
    if (fm != null) {
      FontMetricsInt pfm = paint.getFontMetricsInt();
      fm.ascent = pfm.ascent;
      fm.descent = pfm.descent;
      fm.top = pfm.top;
      fm.bottom = pfm.bottom;
    }

    return rect.right;
  }
}
