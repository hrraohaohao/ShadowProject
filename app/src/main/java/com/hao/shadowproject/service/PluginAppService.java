package com.hao.shadowproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.hao.app_bridge_lib.PluginBridgeHolder;
import com.hao.app_bridge_lib.PluginBridgeInterface;
import com.hao.app_bridge_lib.plugin.PluginNameConstant;
import com.hao.lib.pluginManager.PluginConstant;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.sample.introduce_shadow_lib.Constant;
import com.tencent.shadow.sample.introduce_shadow_lib.MainPluginManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author raohaohao
 * @version 1.0
 * @data 2023/2/13
 */
public class PluginAppService extends Service implements PluginBridgeInterface {

    public static final int REQUEST_PRELOAD_PLUGIN = 0x000001;
    public static final int REQUEST_TO_ACTIVITY = 0x000002;
    public static final int REQUEST_TO_SERVICE = 0x000003;
    private static final String REQUEST_MODE = "request_mode";
    private HashSet<Integer> existSet;
    private Executor newSingleThreadExecutor = Executors.newSingleThreadExecutor();

    private LinkedList hisList = new LinkedList(); //操作记录

    @Override
    public void onCreate() {
        super.onCreate();
        existSet = new HashSet<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(REQUEST_MODE)) {
            int requestMode = intent.getIntExtra(REQUEST_MODE, 0);
            if (MainPluginManager.getPluginManager() == null || MainPluginManager.getPluginFile() == null) {
                ///操作记录 内旋等待Manager 和 插件加载更新完成
                hisList.add(intent);
                newSingleThreadExecutor.execute(new InCycleRunnable());
                return super.onStartCommand(intent, flags, startId);
            }
            switch (requestMode) {
                case REQUEST_PRELOAD_PLUGIN:
                    if (existSet.contains(REQUEST_PRELOAD_PLUGIN)) {
                        return super.onStartCommand(intent, flags, startId);
                    }
                    existSet.add(REQUEST_PRELOAD_PLUGIN);
                    preloadPlugin();
                    break;
                case REQUEST_TO_ACTIVITY:
                    if (existSet.contains(REQUEST_TO_ACTIVITY)) {
                        return super.onStartCommand(intent, flags, startId);
                    }
                    existSet.add(REQUEST_TO_ACTIVITY);
                    toActivity();
                    break;
                case REQUEST_TO_SERVICE:
                    toService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class InCycleRunnable implements Runnable {

        @Override
        public void run() {
            while (MainPluginManager.getPluginManager() == null || MainPluginManager.getPluginFile() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("内旋结束", "指令分发中..");
            Intent intent;
            while ((intent = (Intent) hisList.poll()) != null) {
                onStartCommand(intent, 0, 0);
            }
        }
    }


    /**
     * 启动插件页面
     */
    private void toActivity() {
        PluginBridgeHolder.instances.put(PluginNameConstant.PLUGIN_APP, this);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, PluginNameConstant.PLUGIN_APP);
        bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, "com.hao.plugin_app.MainActivity");
        long s = System.currentTimeMillis();
        MainPluginManager.getPluginManager().enter(this, Constant.FROM_ID_START_ACTIVITY, bundle, new EnterCallback() {
            @Override
            public void onShowLoadingView(View view) {

            }

            @Override
            public void onCloseLoadingView() {

            }

            @Override
            public void onEnterComplete() {
                long d = System.currentTimeMillis();
                Log.e("插件activity启动时间：", String.valueOf(d - s));
                existSet.remove(REQUEST_TO_ACTIVITY);
            }
        });
    }

    /**
     * 启动插件service
     */
    private void toService() {
        PluginBridgeHolder.instances.put(PluginNameConstant.PLUGIN_APP, this);
    }

    /**
     * 预加载插件
     */
    private void preloadPlugin() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, PluginNameConstant.PLUGIN_APP);
        long s = System.currentTimeMillis();
        MainPluginManager.getPluginManager().enter(this, Constant.FROM_ID_PRELOAD_PLUGIN, bundle, new EnterCallback() {
            @Override
            public void onShowLoadingView(View view) {

            }

            @Override
            public void onCloseLoadingView() {

            }

            @Override
            public void onEnterComplete() {
                long d = System.currentTimeMillis();
                Log.e("插件预加载时间：", String.valueOf(d - s));
                existSet.remove(REQUEST_PRELOAD_PLUGIN);
            }
        });
    }

    public static void startPluginAppService(Context context, int requestMode) {
        Intent intent = new Intent(context, PluginAppService.class);
        intent.putExtra(REQUEST_MODE, requestMode);
        context.startService(intent);
    }


    /**
     * 插件对宿主通信
     *
     * @param context
     * @param formId  业务诉求
     * @param bundle  业务描述
     * @return
     */
    @Override
    public Object event(Context context, long formId, Bundle bundle) {
        Log.i("插件对宿主通信", "收到啦~   ");
        return null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
