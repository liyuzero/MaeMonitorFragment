package com.yu.bundles.monitorfragment.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yu.bundles.monitorfragment.MAELifecycleListener;
import com.yu.bundles.monitorfragment.MAEMonitorFragment;

/**
 * Created by liyu on 2018/1/2.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if(getIntent().getBooleanExtra("isForResult", true)){
            final Intent intent = new Intent();
            intent.putExtra("info", "no msg");
            setResult(-1, intent);

            EditText editText = findViewById(R.id.callback_edit);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    intent.putExtra("info", s.toString());
                }
            });

            findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "生命周期已设置监听，请退出当前页面", Toast.LENGTH_SHORT).show();
            MAEMonitorFragment.getInstance(this).setLifecycleListener(new MAELifecycleListener() {
                @Override
                public void onDestroy() {
                    super.onDestroy();
                    Toast.makeText(getApplicationContext(), "TestActivity onDestroy...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
