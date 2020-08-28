package com.facebook.accountkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public final class NotifyingEditText extends AppCompatEditText {
  private OnKeyListener onSoftKeyListener;
  private NotifyingEditText.PasteListener pasteListener;

  public NotifyingEditText(Context context) {
    super(context);
  }

  public NotifyingEditText(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public NotifyingEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setOnSoftKeyListener(OnKeyListener onSoftKeyListener) {
    this.onSoftKeyListener = onSoftKeyListener;
  }

  public boolean onTextContextMenuItem(int id) {
    boolean result = super.onTextContextMenuItem(id);
    switch (id) {
      case 16908322:
        if (this.pasteListener != null) {
          this.pasteListener.onTextPaste();
        }
      default:
        return result;
    }
  }

  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    return new NotifyingEditText.NotifyingInputConnection(super.onCreateInputConnection(outAttrs), true);
  }

  public void setPasteListener(NotifyingEditText.PasteListener pasteHandler) {
    this.pasteListener = pasteHandler;
  }

  public interface PasteListener {
    void onTextPaste();
  }

  private class NotifyingInputConnection extends InputConnectionWrapper {
    public NotifyingInputConnection(InputConnection target, boolean mutable) {
      super(target, mutable);
    }

    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
      if (NotifyingEditText.this.onSoftKeyListener != null) {
        boolean handled = NotifyingEditText.this.onSoftKeyListener.onKey(NotifyingEditText.this, 67, new KeyEvent(0, 67));
        handled = NotifyingEditText.this.onSoftKeyListener.onKey(NotifyingEditText.this, 67, new KeyEvent(1, 67)) || handled;
        if (handled) {
          return true;
        }
      }

      return super.deleteSurroundingText(beforeLength, afterLength);
    }

    public boolean sendKeyEvent(KeyEvent event) {
      return NotifyingEditText.this.onSoftKeyListener != null && NotifyingEditText.this.onSoftKeyListener.onKey(NotifyingEditText.this, event.getKeyCode(), event) || super.sendKeyEvent(event);
    }
  }
}
