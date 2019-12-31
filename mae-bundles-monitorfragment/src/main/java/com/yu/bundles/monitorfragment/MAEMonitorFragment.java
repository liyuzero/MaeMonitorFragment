package com.yu.bundles.monitorfragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.AppOpsManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liyu on 2017/11/28.
 *
 * 该Fragment提供生命周期监听和权限申请方法，因为二者实现原理一致，因此实现方法统一到该Fragment内
 */

public class MAEMonitorFragment extends Fragment implements MAEPermissionRequest{
    private static final String MONITOR_FRAGMENT_TAG = "YU_MONITOR_FRAGMENT_TAG";
    private MAELifecycleListener lifecycleListener;
    private MAEActivityResultListener resultListener;
    private MaeMonitorDialogUtils mMonitorDialogUtils;
    private MaeInstallApkUtils mInstallApkUtils;
    private static int mTargetSDKVersion = 0;

    public static MAEPermissionRequest getInstance(Fragment fragment){
        if(fragment != null && fragment.isAdded() && fragment.getActivity() != null){
            return getInstance(fragment.getActivity());
        } else {
            return new MAEMonitorFragment();
        }
    }

    public static MAEPermissionRequest getInstance(FragmentActivity activity){
        if(activity != null && !activity.isFinishing()){
            initTargetSDKVersion(activity.getApplicationInfo());
            FragmentManager manager = activity.getSupportFragmentManager();
            MAEMonitorFragment fragment = (MAEMonitorFragment) manager.findFragmentByTag(MONITOR_FRAGMENT_TAG);
            if(fragment == null){
                fragment = new MAEMonitorFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(fragment, MONITOR_FRAGMENT_TAG)
                        .commitAllowingStateLoss();
                manager.executePendingTransactions();
            }
            return fragment;
        } else {
            return new MAEMonitorFragment();
        }
    }

    private static void initTargetSDKVersion(ApplicationInfo applicationInfo) {
        if (mTargetSDKVersion == 0 && applicationInfo != null) {
            mTargetSDKVersion = applicationInfo.targetSdkVersion;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(lifecycleListener != null){
            lifecycleListener.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMonitorDialogUtils != null && mMonitorDialogUtils.isShowing() && getActivity() != null) {
            if (mPreForbiddenPermissions == null || hasPermission(getActivity(), mPreForbiddenPermissions)) {
                mMonitorDialogUtils.dismissDialog();
            }
        }
        if(lifecycleListener != null){
            lifecycleListener.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lifecycleListener != null){
            lifecycleListener.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(lifecycleListener != null){
            lifecycleListener.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(lifecycleListener != null){
            lifecycleListener.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(lifecycleListener != null){
            lifecycleListener.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lifecycleListener != null){
            lifecycleListener.onDestroy();
        }
    }

    @Override
    public void setLifecycleListener(MAELifecycleListener lifecycleListener) {
        this.lifecycleListener = lifecycleListener;
    }

    //权限相关
    private static final int PERMISSION_REQUEST_CODE = 20;
    private MAEPermissionCallback maePermissionCallback;

    @Override
    public void requestPermission(String[] permissions, MAEPermissionCallback maePermissionCallback) {
        requestPermission(permissions, maePermissionCallback, null);
    }

    @Override
    public void requestPermissionWithFailDialog(String[] permissions, String failInfo, DialogInterface.OnClickListener onFailClickListener, MAEPermissionCallback callBack) {
        requestPermissionWithFailDialog(permissions, failInfo, onFailClickListener, callBack, null);
    }

    private String[] mPreForbiddenPermissions;

    @Override
    public void requestPermissionWithFailDialog(String[] permissions, final String failInfo, final DialogInterface.OnClickListener onFailClickListener,
                                                final MAEPermissionCallback callBack, String explain) {
        requestPermission(permissions, new MAEPermissionCallback() {
            @Override
            public void onPermissionApplySuccess() {
                if (callBack != null) {
                    callBack.onPermissionApplySuccess();
                }
            }

            @Override
            public void onPermissionApplyFailure(List<String> notGrantedPermissions, List<Boolean> shouldShowRequestPermissions) {
                boolean isNotAskAgain = false;
                for (Boolean b : shouldShowRequestPermissions) {
                    isNotAskAgain |= !b;
                }
                if (isNotAskAgain || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mTargetSDKVersion < Build.VERSION_CODES.O)) {
                    //点击设置，添加权限返回界面后，使得dialog消失时使用
                    mPreForbiddenPermissions = notGrantedPermissions.toArray(new String[0]);
                    if(mMonitorDialogUtils == null) {
                        mMonitorDialogUtils = new MaeMonitorDialogUtils(getActivity());
                    }
                    mMonitorDialogUtils.createPermissionDialog(getActivity(), failInfo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点击了取消按钮，dialog默认已经消失
                            mPreForbiddenPermissions = null;
                            if (onFailClickListener != null) {
                                onFailClickListener.onClick(dialog, which);
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                    return;
                }
                if (callBack != null) {
                    callBack.onPermissionApplyFailure(notGrantedPermissions, shouldShowRequestPermissions);
                }
            }
        }, explain);
    }

    /*
    * 权限申请
    * 外部调用
    *
    * @param permissions    权限组
    * @param maePermissionCallback  权限申请回调
    * */
    @Override
    public void requestPermission(String[] permissions, final MAEPermissionCallback maePermissionCallback, String explain){
        if(permissions == null || maePermissionCallback == null || getContext() == null){
            return ;
        }
        final List<String> permissionsList = getPermissionsList(getActivity(), permissions);
        this.maePermissionCallback = maePermissionCallback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mTargetSDKVersion < Build.VERSION_CODES.O) {
            if (hasPermission(getContext(), permissions)) {
                maePermissionCallback.onPermissionApplySuccess();
            } else {
                onPermissionFail(maePermissionCallback, permissionsList);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionsList.size() > 0) {
            if (!TextUtils.isEmpty(explain) && getActivity() != null && !getActivity().isFinishing()) {
                // 向用户解释权限
                showExplainDialogAndRequestPermission(permissionsList, maePermissionCallback, explain);
            } else {
                int size = permissionsList.size();
                requestPermissions(permissionsList.toArray(new String[size]), PERMISSION_REQUEST_CODE);
            }
        } else {
            if (hasPermission(getContext(), permissions)) {
                maePermissionCallback.onPermissionApplySuccess();
            } else {
                onPermissionFail(maePermissionCallback, permissionsList);
            }
        }
    }

    private void onPermissionFail(MAEPermissionCallback maePermissionCallback, List<String> permissionsList) {
        List<Boolean> list = new LinkedList<>();
        for (int i = 0; i < permissionsList.size(); i++) {
            list.add(false);
        }
        maePermissionCallback.onPermissionApplyFailure(permissionsList, list);
    }

    private void showExplainDialogAndRequestPermission(final List<String> permissionsList, final MAEPermissionCallback maePermissionCallback, String explain){
        if(getActivity() == null) {
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(explain);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int size = permissionsList.size();
                    requestPermissions(permissionsList.toArray(new String[size]), PERMISSION_REQUEST_CODE);
                } else {
                    maePermissionCallback.onPermissionApplySuccess();
                }
            }
        });
        dialogBuilder.create().show();
    }

    /*
    * 判断是否拥有权限
    * */
    private boolean hasPermission(@NonNull Context context, @NonNull String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) return false;

            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (op != null) {
                result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
                if (result != AppOpsManagerCompat.MODE_ALLOWED) return false;
            }
        }
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode != PERMISSION_REQUEST_CODE || getContext() == null){
            return ;
        }

        boolean isGranted = true;
        int size = permissions.length;
        List<String> notGrantedPermissions = new ArrayList<>();
        List<Boolean> shouldShowRequestPermissions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                notGrantedPermissions.add(permissions[i]);
                shouldShowRequestPermissions.add(shouldShowRequestPermissionRationale(permissions[i]));
            }
        }

        /* 国产手机无论是获取权限成功还是失败，这个结果都会有可能不可靠，需重新确认权限，详情参考
         * https://github.com/yanzhenjie/AndPermission/blob/master/README-CN.md#%E5%9B%BD%E4%BA%A7%E6%89%8B%E6%9C%BA%E9%80%82%E9%85%8D%E6%96%B9%E6%A1%88
         */
        if (isGranted && maePermissionCallback != null && hasPermission(getContext(), permissions)) {
            maePermissionCallback.onPermissionApplySuccess();
        } else if (!isGranted && maePermissionCallback != null) { //  权限被拒绝
            if(hasPermission(getContext(), permissions)){
                maePermissionCallback.onPermissionApplySuccess();
            } else {
                maePermissionCallback.onPermissionApplyFailure(notGrantedPermissions, shouldShowRequestPermissions);
            }
        }
    }

    /**
     * 添加要求权限列表
     *
     * @param permissions 权限列表
     * @return true 表示需要解释，false不需要
     */
    private static List<String> getPermissionsList(Activity activity, String[] permissions) {
        List<String> permissionsList = new ArrayList<>();
        for (String permission: permissions){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        return permissionsList;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, MAEActivityResultListener resultListener){
        this.resultListener = resultListener;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mInstallApkUtils != null) {
            mInstallApkUtils.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
        if (resultListener != null){
            resultListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void installApkFile(File file) {
        if (mInstallApkUtils == null) {
            mInstallApkUtils = new MaeInstallApkUtils(file);
        }
        mInstallApkUtils.installApk(getActivity());
    }
}
