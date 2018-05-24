package com.yu.bundles.monitorfragment.demo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yu.bundles.monitorfragment.MAEActivityResultListener;
import com.yu.bundles.monitorfragment.MAEMonitorFragment;
import com.yu.bundles.monitorfragment.MAEPermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("植入Fragment实现回调功能")
                .setItems(new CharSequence[]{"回调式权限申请",
                        "startActivityForResult回调",
                        "Activity和Fragment的生命周期回调"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                MAEMonitorFragment.getInstance(MainActivity.this).requestPermission(new String[]{Manifest.permission.GET_ACCOUNTS}, new MAEPermissionCallback() {
                                    @Override
                                    public void onPermissionApplySuccess() {
                                        Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                                        Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case 1:
                                MAEMonitorFragment.getInstance(MainActivity.this).startActivityForResult(new Intent(MainActivity.this,
                                        TestActivity.class).putExtra("isForResult", true), 0, new MAEActivityResultListener() {
                                    @Override
                                    public void onActivityResult(int i, int i1, Intent intent) {
                                        if(i == 0 && i1 == -1 && intent != null){
                                            Toast.makeText(MainActivity.this, intent.getStringExtra("info"), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, TestActivity.class).putExtra("isForResult", false));
                                break;
                        }
                    }
                }).show();
    }
}
