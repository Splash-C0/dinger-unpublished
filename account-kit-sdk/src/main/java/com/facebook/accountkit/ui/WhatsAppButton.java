package com.facebook.accountkit.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.facebook.accountkit.custom.R.drawable;

public class WhatsAppButton extends AppCompatButton {
  private final int ICON_SIZE_DP = 20;

  public WhatsAppButton(Context context) {
    super(context);
    this.init();
  }

  public WhatsAppButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.init();
  }

  public WhatsAppButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init();
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    this.imageSpanColorReset();
  }

  public void imageSpanColorReset() {
    CharSequence buttonText = this.getText();
    if (buttonText instanceof SpannedString) {
      SpannedString buttonSpannable = (SpannedString) buttonText;
      ImageSpan[] imageSpans = (ImageSpan[]) buttonSpannable.getSpans(0, buttonSpannable.length(), ImageSpan.class);
      ImageSpan[] var4 = imageSpans;
      int var5 = imageSpans.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        ImageSpan imageSpan = var4[var6];
        if (imageSpan.getDrawable() != null) {
          DrawableCompat.setTint(imageSpan.getDrawable(), this.getCurrentTextColor());
        }
      }
    }

  }

  private void init() {
    int imageStartIndex = this.getText().toString().indexOf(9711);
    if (imageStartIndex != -1) {
      Spannable span = new SpannableString(this.getText());
      Drawable whatsAppLogo = DrawableCompat.wrap(ContextCompat.getDrawable(this.getContext(), drawable.ic_button_icon_whatsapp));
      int[] iconSizes = this.getIconBoundary();
      whatsAppLogo.setBounds(0, 0, iconSizes[0], iconSizes[1]);
      ImageSpan imageSpan = new CenteredImageSpan(whatsAppLogo);
      span.setSpan(imageSpan, imageStartIndex, imageStartIndex + 1, 17);
      this.setText(span);
      this.imageSpanColorReset();
    }

  }

  private int[] getIconBoundary() {
    int height = (int) (20.0F * this.getResources().getDisplayMetrics().density);
    return new int[]{4 * height, height};
  }
}
