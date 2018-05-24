package com.yu.bundles.monitorfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by liyu on 2017/11/28.
 *
 * 该类提供钩子方法，而不是设置为接口形式，避免使用该类时需要重写以下所有生命周期的监听方法
 */

public abstract class MAELifecycleListener {
    public void onCreate(@Nullable Bundle savedInstanceState){}
    public void onStart(){}
    public void onResume(){}
    public void onPause(){}
    public void onStop(){}
    public void onDestroy(){}
    public void onSaveInstanceState(@NonNull Bundle outState) {}
}
