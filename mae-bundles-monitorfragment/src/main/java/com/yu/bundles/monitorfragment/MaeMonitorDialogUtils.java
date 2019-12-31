package com.yu.bundles.monitorfragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * @author: liyu10
 * @description:
 * @create: 18-12-6
 **/
class MaeMonitorDialogUtils {
    private WeakReference<Activity> weakReference;
    private AlertDialog permissionDialog;

    MaeMonitorDialogUtils(Activity activity) {
        weakReference = new WeakReference<>(activity);
    }

    private boolean isActivityValid() {
        Activity activity = weakReference.get();
        if(activity == null) {
            return false;
        }
        return !activity.isFinishing();
    }

    void createPermissionDialog(final Context context, String content, final DialogInterface.OnClickListener onCancelClickListener) {
        if (permissionDialog != null && permissionDialog.isShowing()) {
            return;
        }
        if(!isActivityValid()) {
            return;
        }
        if(TextUtils.isEmpty(content)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(weakReference.get());
        builder.setCancelable(false);
        builder.setMessage(content);
        builder.setNegativeButton("取消", onCancelClickListener);
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isActivityValid()) {
                    return;
                }
                jump2PackInfoAty(context);
            }
        });
        permissionDialog = builder.show();
    }

    private void jump2PackInfoAty(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    void dismissDialog() {
        if(permissionDialog != null && permissionDialog.isShowing()) {
            permissionDialog.dismiss();
        }
    }

    boolean isShowing() {
        return permissionDialog != null && permissionDialog.isShowing();
    }
}
