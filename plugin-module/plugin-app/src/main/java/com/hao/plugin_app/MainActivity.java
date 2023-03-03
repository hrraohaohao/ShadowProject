package com.hao.plugin_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.hao.app_bridge_lib.BasicConfig;
import com.hao.app_bridge_lib.PluginBridgeHolder;
import com.hao.app_bridge_lib.plugin.PluginNameConstant;

public class MainActivity extends Activity {
    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void test(View view) {
        String test = BasicConfig.test;
        Log.i(TAG, test);
        PluginBridgeHolder.instances.get(PluginNameConstant.PLUGIN_APP).event(this, 1000, null);
    }
}