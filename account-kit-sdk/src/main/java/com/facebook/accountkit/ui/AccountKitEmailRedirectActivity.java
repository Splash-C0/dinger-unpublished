package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AccountKitEmailRedirectActivity extends Activity {
  public AccountKitEmailRedirectActivity() {
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = new Intent(this, AccountKitActivity.class);
    intent.putExtra("url", this.getIntent().getDataString());
    intent.addFlags(335544320);
    this.startActivity(intent);
  }
}
