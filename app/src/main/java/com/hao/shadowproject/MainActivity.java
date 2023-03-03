package com.hao.shadowproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.hao.lib.pluginManager.DownloadPluginManager;
import com.hao.lib.pluginManager.ImplDownloadFileListener;
import com.hao.shadowproject.service.PluginAppService;
import com.hao.shadowproject.service.PluginInitService;
import com.hao.shadowproject.utils.PermissionCheckUtils;
import com.tencent.shadow.sample.introduce_shadow_lib.MainPluginManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //此处没有权限会自动申请权限
            if (PermissionCheckUtils.hasPermission(this, 10001,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                init();
            } else {
                init();
            }
            return;
        }
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001:
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    init();
                } else {
                    init();
                }
                break;
        }
    }

    private void init() {
        checkVersion();
        preloadPlugin();
    }

    private void checkVersion() {
        DownloadPluginManager.getInstance()
                .init(this)
                .checkNewVersionByManager()
                .checkNewVersionByPlugin()
                .setDownloadFileListener(new ImplDownloadFileListener() {
                    @Override
                    public void loadManagerFile(File manager) {
                        MainPluginManager.loadPluginManager(manager);
                        PluginInitService.startPluginInitService(MainActivity.this, PluginInitService.REQUEST_LOAD_MANAGER);
                        Log.i(TAG, "manager  加载...");
                    }

                    @Override
                    public void downLoadManagerSuccess(File manager) {
                        Log.i(TAG, "manager 新版本下载成功 ...");
                        MainPluginManager.loadPluginManager(manager);
                        PluginInitService.startPluginInitService(MainActivity.this, PluginInitService.REQUEST_LOAD_MANAGER);
                    }

                    @Override
                    public void loadPluginZip(File plugin) {
                        Log.i(TAG, "插件 加载...");
                        PluginInitService.startPluginInitService(MainActivity.this, PluginInitService.REQUEST_NEW_PLUGIN);
                    }

                    @Override
                    public void downLoadPluginSuccess() {
                        Log.i(TAG, "插件新版本下载成功...");
                        PluginInitService.startPluginInitService(MainActivity.this, PluginInitService.REQUEST_NEW_PLUGIN);
                    }
                });
    }

    private void preloadPlugin() {
        //预加载...
        PluginAppService.startPluginAppService(this, PluginAppService.REQUEST_PRELOAD_PLUGIN);
    }


    public void toPlugin(View view) {

        PluginAppService.startPluginAppService(this, PluginAppService.REQUEST_TO_ACTIVITY);

    }
}