package com.hao.shadowproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hao.lib.pluginManager.PluginConstant;
import com.tencent.shadow.sample.introduce_shadow_lib.MainPluginManager;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author raohaohao
 * @version 1.0
 * @data 2023/3/1
 */
public class PluginInitService extends Service {
    private static final String REQUEST_MODE = "request_mode";
    public static final int REQUEST_LOAD_MANAGER = 0x000001;
    public static final int REQUEST_NEW_PLUGIN = 0x000002;

    private Executor newSingleThreadExecutor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(REQUEST_MODE)) {
            int requestMode = intent.getIntExtra(REQUEST_MODE, 0);
            switch (requestMode) {
                case REQUEST_LOAD_MANAGER:
                    File managerFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_manager_name);
                    if (managerFile.exists()) {
                        MainPluginManager.loadPluginManager(managerFile);
                    }
                    break;
                case REQUEST_NEW_PLUGIN:
                    newSingleThreadExecutor.execute(new InCycleRunnable());
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class InCycleRunnable implements Runnable {

        @Override
        public void run() {
            while (MainPluginManager.getPluginManager() == null) {
                try {
                    Log.i("InCycleRunnable", "初始化内旋中...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            File newPluginFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
            if (newPluginFile.exists()) {
                MainPluginManager.setPluginFile(newPluginFile);
            }
        }
    }


    public static void startPluginInitService(Context context, int requestMode) {
        Intent intent = new Intent(context, PluginInitService.class);
        intent.putExtra(REQUEST_MODE, requestMode);
        context.startService(intent);
    }

}
