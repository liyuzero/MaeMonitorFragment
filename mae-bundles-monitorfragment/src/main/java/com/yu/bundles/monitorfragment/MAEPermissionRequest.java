package com.yu.bundles.monitorfragment;

import android.content.DialogInterface;
import android.content.Intent;

import java.io.File;

/**
 * Created by liyu on 2018/1/18.
 */

public interface MAEPermissionRequest {
    void setLifecycleListener(MAELifecycleListener lifecycleListener);
    void startActivityForResult(Intent intent, int requestCode, MAEActivityResultListener resultListener);
    void requestPermission(String[] permissions, MAEPermissionCallback maePermissionCallback);
    void requestPermission(String[] permissions, MAEPermissionCallback maePermissionCallback, String explain);
    void requestPermissionWithFailDialog(String[] permissions, String failInfo, DialogInterface.OnClickListener onFailClickListener, MAEPermissionCallback callBack);
    void requestPermissionWithFailDialog(String[] permissions, String failInfo, DialogInterface.OnClickListener onFailClickListener, MAEPermissionCallback callBack, String explain);
    void installApkFile(File file);
}
