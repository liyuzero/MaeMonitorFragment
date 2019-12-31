package com.yu.bundles.monitorfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

/**
 * @author: liyu10
 * @description:
 * @create: 18-12-7
 **/
class MaeInstallApkUtils {
    private static final int REQUEST_CODE = 211221;
    private File file;

    MaeInstallApkUtils(File file) {
        this.file = file;
    }

    void installApk(FragmentActivity activity) {
        //兼容8.0
        if(Build.VERSION.SDK_INT >= 26) {
            boolean hasInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                Toast.makeText(activity, activity.getString(R.string.mae_bundles_monitor_install_apk_tips), Toast.LENGTH_SHORT).show();
                Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                activity.startActivityForResult(intent, REQUEST_CODE);
                return;
            }
        }
        install(activity);
    }

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if(Build.VERSION.SDK_INT >= 26) {
                boolean hasInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
                if(hasInstallPermission) {
                    install(activity);
                }
            }
        }
    }

    private void install(Activity activity) {
        Intent installIntent = new Intent();
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setAction(Intent.ACTION_VIEW);
        final Uri uri;
        //兼容7.0
        if(Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() +".mae.yu.monitor.fileprovider", file);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivity(installIntent);
    }

}
