package com.facebook.accountkit.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.facebook.accountkit.AccountKit;

public final class AccountKitInitProvider extends ContentProvider {
  public AccountKitInitProvider() {
  }

  public boolean onCreate() {
    AccountKitController.initialize(this.getContext(), (AccountKit.InitializeCallback) null);
    return true;
  }

  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return null;
  }

  public String getType(Uri uri) {
    return null;
  }

  public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }
}
