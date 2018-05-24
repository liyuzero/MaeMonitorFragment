package com.yu.bundles.monitorfragment;

import java.util.List;

/**
 * Created by liyu on 2017/11/28.
 */

public interface MAEPermissionCallback {
    void onPermissionApplySuccess();

    /**
     * @param notGrantedPermissions，       没有被用户允许的权限
     * @param shouldShowRequestPermissions 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
     *                                     如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
     */
    void onPermissionApplyFailure(List<String> notGrantedPermissions, List<Boolean> shouldShowRequestPermissions);
}
