package com.skinfotech.ekinch.utils.filepicker.activity;

import static com.skinfotech.ekinch.utils.EkInchConstant.RC_READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.utils.easypermissions.AfterPermissionGranted;
import com.skinfotech.ekinch.utils.easypermissions.AppSettingsDialog;
import com.skinfotech.ekinch.utils.easypermissions.EasyPermissions;
import com.skinfotech.ekinch.utils.filepicker.FolderListHelper;

import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2016/10/12
 * Time: 16:21
 */

public abstract class BaseActivity extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks {

    private static final String TAG = BaseActivity.class.getName();

    protected FolderListHelper mFolderHelper;
    protected boolean isNeedFolderList;
    public static final String IS_NEED_FOLDER_LIST = "isNeedFolderList";

    protected abstract void permissionGranted();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNeedFolderList = getIntent().getBooleanExtra(IS_NEED_FOLDER_LIST, false);
        if (isNeedFolderList) {
            mFolderHelper = new FolderListHelper();
            mFolderHelper.initFolderListView(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        readExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void readExternalStorage() {
        boolean isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"/*, "android.permission.MANAGE_EXTERNAL_STORAGE"*/);
        if (isGranted) {
            permissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.vw_rationale_storage),
                    RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"/*, "android.permission.MANAGE_EXTERNAL_STORAGE"*/);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        permissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // If Permission permanently denied, ask user again
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            if (perms.contains("android.permission.MANAGE_EXTERNAL_STORAGE")) {
//                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(1).build().show();
//
//            } else
                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(0).build().show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
                permissionGranted();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set toolbar data
     *
     * @param toolbar toolbar instance
     * @param title   title to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(Toolbar toolbar, String title) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.hippo_ic_arrow_back);

            ab.setTitle("");

            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setText(title);
        }
        return getSupportActionBar();
    }
}
