package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitUpdateResult;
import com.facebook.accountkit.custom.R.dimen;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.internal.AccountKitController;

public final class AccountKitUpdateActivity extends AccountKitActivityBase implements UIManager.UIManagerListener {
  private static final IntentFilter UPDATE_FLOW_BROADCAST_RECEIVER_FILTER = UpdateStateStackManager.getIntentFilter();
  private String finalUpdateState;
  private AccountKitUpdateResult.UpdateResult result;
  private UpdateStateStackManager updateStateStackManager;

  public AccountKitUpdateActivity() {
    this.result = AccountKitUpdateResult.UpdateResult.CANCELLED;
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.uiManager.setUIManagerListener(this);
    this.updateStateStackManager = new UpdateStateStackManager(this, this.configuration);
    AccountKitController.onUpdateActivityCreate(this, savedInstanceState);
    LocalBroadcastManager.getInstance(this).registerReceiver(this.updateStateStackManager, UPDATE_FLOW_BROADCAST_RECEIVER_FILTER);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case 16908332:
        this.onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case 4:
        this.onBackPressed();
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  protected void onPause() {
    super.onPause();
    ContentController contentController = this.updateStateStackManager.getContentController();
    if (contentController != null) {
      contentController.onPause(this);
    }

  }

  public void onBackPressed() {
    if (this.updateStateStackManager.getContentController() == null) {
      super.onBackPressed();
    } else {
      this.updateStateStackManager.popState();
    }

  }

  protected void onSaveInstanceState(Bundle outState) {
    AccountKitController.onUpdateActivitySaveInstanceState(this, outState);
    super.onSaveInstanceState(outState);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ContentController contentController = this.updateStateStackManager.getContentController();
    if (contentController != null) {
      contentController.onActivityResult(requestCode, resultCode, data);
    }

  }

  protected void onResume() {
    super.onResume();
    ContentController contentController = this.updateStateStackManager.getContentController();
    if (contentController != null) {
      contentController.onResume(this);
    }

  }

  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(this.updateStateStackManager);
    super.onDestroy();
    AccountKitController.onUpdateActivityDestroy(this);
  }

  public void onBack() {
    this.onBackPressed();
  }

  public void onCancel() {
    this.sendCancelResult();
  }

  void sendResult() {
    int resultCode = this.result == AccountKitUpdateResult.UpdateResult.SUCCESS ? -1 : 0;
    this.sendResult(resultCode, new AccountKitUpdateResultImpl(this.finalUpdateState, this.error, false));
  }

  void sendCancelResult() {
    this.sendResult(0, new AccountKitUpdateResultImpl((String) null, (AccountKitError) null, true));
  }

  void setFinalUpdateState(String finalUpdateState) {
    this.finalUpdateState = finalUpdateState;
  }

  void setUpdateResult(AccountKitUpdateResult.UpdateResult result) {
    this.result = result;
  }

  void updateUI(UpdateFlowState updateFlowState, ContentController toContentController) {
    Object headerFragment;
    if (updateFlowState != UpdateFlowState.CODE_INPUT_ERROR && updateFlowState != UpdateFlowState.PHONE_NUMBER_INPUT_ERROR) {
      headerFragment = BaseUIManager.getDefaultHeaderFragment(this.uiManager, updateFlowState);
    } else {
      headerFragment = toContentController.getHeaderFragment();
    }

    Fragment contentCenterFragment = BaseUIManager.getDefaultBodyFragment(this.uiManager, updateFlowState);
    Fragment footerFragment = BaseUIManager.getDefaultFooterFragment(this.uiManager);
    Fragment contentTopFragment = toContentController.getTopFragment();
    ContentFragment contentTextFragment = toContentController.getTextFragment();
    ContentFragment contentBottomFragment = toContentController.getBottomFragment();
    if (contentTextFragment != null) {
      int contentPaddingTopResourceId = dimen.com_accountkit_vertical_spacer_small_height;
      int contentPaddingTop = this.getResources().getDimensionPixelSize(contentPaddingTopResourceId);
      int contentPaddingBottom = 0;
      if (contentTextFragment instanceof TextContentFragment) {
        TextContentFragment textContentFragment = (TextContentFragment) contentTextFragment;
        textContentFragment.setContentPaddingTop(contentPaddingTop);
        textContentFragment.setContentPaddingBottom(0);
      }
    }

    this.ensureNextButton(toContentController);
    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
    this.replace(transaction, id.com_accountkit_header_fragment, (Fragment) headerFragment);
    this.replace(transaction, id.com_accountkit_content_top_fragment, contentTopFragment);
    this.replace(transaction, id.com_accountkit_content_top_text_fragment, (Fragment) null);
    this.replace(transaction, id.com_accountkit_content_center_fragment, contentCenterFragment);
    this.replace(transaction, id.com_accountkit_content_bottom_text_fragment, contentTextFragment);
    if (!ViewUtility.isSkin(this.uiManager, SkinManager.Skin.CONTEMPORARY)) {
      this.replace(transaction, id.com_accountkit_content_bottom_fragment, contentBottomFragment);
      this.replace(transaction, id.com_accountkit_footer_fragment, footerFragment);
    }

    transaction.addToBackStack((String) null);
    ViewUtility.hideKeyboard(this);
    transaction.commit();
    toContentController.onResume(this);
  }

  private void sendResult(int resultCode, AccountKitUpdateResultImpl loginResult) {
    Intent data = new Intent();
    data.putExtra("account_kit_update_result", loginResult);
    this.setResult(resultCode, data);
    this.finish();
  }
}
