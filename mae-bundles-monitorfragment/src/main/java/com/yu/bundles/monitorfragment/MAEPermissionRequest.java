package com.yu.bundles.monitorfragment;

import android.content.Intent;

/**
 * Created by liyu on 2018/1/18.
 */

public interface MAEPermissionRequest {
    void setLifecycleListener(MAELifecycleListener lifecycleListener);
    void startActivityForResult(Intent intent, int requestCode, MAEActivityResultListener resultListener);
    void requestPermission(String[] permissions, MAEPermissionCallback maePermissionCallback);
    void requestPermission(String[] permissions, MAEPermissionCallback maePermissionCallback, String explain);
}
