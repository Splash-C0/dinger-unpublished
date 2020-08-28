package com.facebook.accountkit.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.facebook.accountkit.custom.R.styleable;

public final class AspectFrameLayout extends FrameLayout {
  private int aspectHeight;
  private int aspectWidth;
  private Point displaySize;

  public AspectFrameLayout(Context context) {
    super(context);
  }

  public AspectFrameLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.init(context, attrs);
  }

  public AspectFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs);
  }

  @TargetApi(21)
  public AspectFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    this.init(context, attrs);
  }

  private void init(Context context, @Nullable AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, styleable.AspectFrameLayout);

    try {
      this.aspectWidth = a.getDimensionPixelSize(styleable.AspectFrameLayout_com_accountkit_aspect_width, 0);
      this.aspectHeight = a.getDimensionPixelSize(styleable.AspectFrameLayout_com_accountkit_aspect_height, 0);
    } finally {
      a.recycle();
    }

  }

  public float getAspectHeight() {
    return (float) this.aspectHeight;
  }

  public void setAspectHeight(int aspectHeight) {
    if (this.aspectHeight != aspectHeight) {
      this.aspectHeight = aspectHeight;
      this.requestLayout();
    }
  }

  public float getAspectWidth() {
    return (float) this.aspectWidth;
  }

  public void setAspectWidth(int aspectWidth) {
    if (this.aspectWidth != aspectWidth) {
      this.aspectWidth = aspectWidth;
      this.requestLayout();
    }
  }

  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Point displaySize = new Point();
    ((WindowManager) this.getContext().getSystemService("window")).getDefaultDisplay().getSize(displaySize);
    this.displaySize = displaySize;
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (this.aspectWidth != 0 && this.aspectHeight != 0 && this.displaySize != null) {
      int scaledHeight = this.displaySize.x * this.aspectHeight / this.aspectWidth;
      int width;
      int height;
      if (scaledHeight > this.displaySize.y) {
        width = this.displaySize.x;
        height = scaledHeight;
      } else {
        width = this.displaySize.y * this.aspectWidth / this.aspectHeight;
        height = this.displaySize.y;
      }

      super.onMeasure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(height, 1073741824));
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}
