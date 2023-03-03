package com.hao.lib.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hao.lib.download.DownLoadFileBean;
import com.hao.lib.download.DownLoadFileManager;
import com.hao.lib.interfaces.HandlerListener;
import com.hao.lib.pluginManager.DownloadPluginManager;
import com.hao.lib.pluginManager.PluginConstant;

import org.jetbrains.annotations.Nullable;


public class SDownloadService extends Service {

    private String TAG = SDownloadService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                int type = intent.getIntExtra(HANDLER_KEY, 0);
                switch (type) {
                    case DOWN_LOAD_PLUGIN_TYPE: //下载插件
                        String pluginUrl = intent.getStringExtra(DOWN_LOAD_PLUGIN_CONTENT_KEY);
                        if (TextUtils.isEmpty(pluginUrl)) {
                            break;
                        }
                        DownLoadFileManager.getInstance().downLoad(null, msg -> {
                            switch (msg.what) {
                                case DownLoadFileBean.DOWLOAD_FLAG_FAIL:
                                    Log.e(TAG, pluginUrl + "---" + "插件下载失败");
                                    break;
                                case DownLoadFileBean.DOWLOAD_FLAG_SUCCESS:
                                    Log.i(TAG, pluginUrl + "---" + "插件下载成功");
                                    DownloadPluginManager.getInstance().downLoadPluginSuccess();
                                    break;
                                case DownLoadFileBean.DOWLOAD_FLAG_ING:
                                    break;
                            }
                        }, PluginConstant.DOWNLOAD_PLUGIN, pluginUrl, 1, true, PluginConstant.str_u_downloadPlugin_name, PluginConstant.strDownloadDir);
                        break;
                    case DOWN_LOAD_PLUGIN_MANAGER_TYPE: //下载插件管理
                        String managerUrl = intent.getStringExtra(DOWN_LOAD_PLUGIN_MANAGER_KEY);
                        if (TextUtils.isEmpty(managerUrl)) {
                            break;
                        }
                        DownLoadFileManager.getInstance().downLoad(null, msg -> {
                            switch (msg.what) {
                                case DownLoadFileBean.DOWLOAD_FLAG_FAIL:
                                    Log.e(TAG, managerUrl + "---" + "插件管理器下载失败");
                                    break;
                                case DownLoadFileBean.DOWLOAD_FLAG_SUCCESS:
                                    Log.i(TAG, managerUrl + "---" + "插件管理器下载成功");
                                    DownloadPluginManager.getInstance().downLoadManagerSuccess();
                                    break;
                                case DownLoadFileBean.DOWLOAD_FLAG_ING:
                                    break;
                            }
                        }, PluginConstant.DOWNLOAD_MANAGER, managerUrl, 1, true, PluginConstant.str_manager_name, PluginConstant.strDownloadDir);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private static String HANDLER_KEY = "HANDLER_KEY";
    //下载插件
    private static final int DOWN_LOAD_PLUGIN_TYPE = 1001;
    private static final String DOWN_LOAD_PLUGIN_CONTENT_KEY = "DOWN_LOAD_PLUGIN_CONTENT_KEY";
    //下载插件集
    private static final int DOWN_LOAD_PLUGIN_MANAGER_TYPE = 1002;
    private static final String DOWN_LOAD_PLUGIN_MANAGER_KEY = "DOWN_LOAD_PLUGIN_CONTENT_LIST_KEY";


    //下载插件
    public static void startDownloadPlugin(Context context, String content) {
        try {
            Intent intent = new Intent(context, SDownloadService.class);
            intent.putExtra(HANDLER_KEY, DOWN_LOAD_PLUGIN_TYPE);
            intent.putExtra(DOWN_LOAD_PLUGIN_CONTENT_KEY, content);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //下载多个插件
    public static void startDownloadManager(Context context, String content) {
        try {
            Intent intent = new Intent(context, SDownloadService.class);
            intent.putExtra(HANDLER_KEY, DOWN_LOAD_PLUGIN_MANAGER_TYPE);
            intent.putExtra(DOWN_LOAD_PLUGIN_MANAGER_KEY, content);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
