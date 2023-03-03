package com.tencent.shadow.manager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.loader.PluginServiceConnection;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PluginManager extends FastPluginManager {

    private final String TAG = PluginManager.class.getName();
    private static final Logger mLogger = LoggerFactory.getLogger(PluginManager.class);

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;


    public PluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "shadow-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName() {
        return "com.tencent.shadow.sample.introduce_shadow_lib.MainPluginProcessService";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        Log.i(TAG, "我是新版本manager+++++++++");
        if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            onStartActivity(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_CALL_SERVICE) {
            callPluginService(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_PRELOAD_PLUGIN) {
            preLoadPlugin(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_UPDATE_PLUGIN) {
            updatePlugin(context, bundle, callback);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    private void updatePlugin(Context context, Bundle bundle, EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        executorService.execute(() -> {
            try {
                InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                loadPluginLoaderAndRuntime(installedPlugin.UUID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> {
                    callback.onCloseLoadingView();
                    callback.onEnterComplete();
                });
            }
        });
    }


    /**
     * 预加载插件
     *
     * @param context
     * @param bundle
     * @param callback
     */
    private void preLoadPlugin(Context context, Bundle bundle, EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        preLoadPlugin(context, pluginZipPath, partKey, callback);
    }


    /**
     * 预加载插件
     *
     * @param context
     * @param partKey
     * @param callback
     */
    private void preLoadPlugin(Context context, String pluginZipPath, String partKey, EnterCallback callback) {
        executorService.execute(() -> {
            try {
                InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                loadPlugin(installedPlugin.UUID, partKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> {
                    callback.onCloseLoadingView();
                    callback.onEnterComplete();
                });
            }
        });
    }

    /**
     * 插件化启动activity 页面
     *
     * @param context
     * @param bundle
     * @param callback
     */
    private void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);
        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }
        executorService.execute(() -> {
            try {
                if (mPluginLoader == null) {
                    InstalledPlugin plugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                    loadPlugin(plugin.UUID, partKey);
                }

                Map map = mPluginLoader.getLoadedPlugin();

                if (!map.containsKey(partKey)) {
                    InstalledPlugin plugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                    loadPlugin(plugin.UUID, partKey);
                }

                Intent pluginIntent = new Intent();
                pluginIntent.setClassName(
                        context.getPackageName(),
                        className
                );
                if (extras != null) {
                    pluginIntent.replaceExtras(extras);
                }
                startPluginActivity(context, pluginIntent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> {
                    callback.onCloseLoadingView();
                    callback.onEnterComplete();
                });
            }
        });
    }


    /**
     * 启动插件服务
     *
     * @param context
     * @param bundle
     * @param callback
     */
    private void callPluginService(final Context context, Bundle bundle, EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_SERVICE_CLASSNAME);
        callPluginService(context, pluginZipPath, partKey, className, callback);
    }


    /**
     * binder服务绑定
     *
     * @param context
     * @param partKey
     * @param className
     * @param callback
     */
    private void callPluginService(final Context context, String pluginZipPath, String partKey, String className, EnterCallback callback) {
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(context.getPackageName(), className);
        executorService.execute(() -> {
            try {
                if (mPluginLoader == null) {
                    InstalledPlugin plugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                    loadPlugin(plugin.UUID, partKey);
                }

                Map map = mPluginLoader.getLoadedPlugin();

                if (!map.containsKey(partKey)) {
                    InstalledPlugin plugin = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的
                    loadPlugin(plugin.UUID, partKey);
                }

                Intent pluginIntent1 = new Intent();
                pluginIntent1.setClassName(context.getPackageName(), className);

                boolean callSuccess = mPluginLoader.bindPluginService(pluginIntent1, new PluginServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        Log.i(TAG, "onServiceConnected +++++");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        throw new RuntimeException("onServiceDisconnected");
                    }
                }, Service.BIND_AUTO_CREATE);
                if (!callSuccess) {
                    throw new RuntimeException("bind service失败 className==" + className);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> {
                    callback.onCloseLoadingView();
                    callback.onEnterComplete();
                });
            }
        });
    }
}
