package com.hao.lib.pluginManager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.hao.lib.interfaces.OnNextListener;
import com.hao.lib.net.http.HttpWriteFileQuest;
import com.hao.lib.pluginManager.bean.DPluginInfoBean;
import com.hao.lib.pluginManager.bean.PluginConfig;
import com.hao.lib.pluginManager.utils.PluginUtils;
import com.hao.lib.services.SDownloadService;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author：raohaohao
 * @date: 2023/2/27
 * @description: 下载插件管理器 功能包括控制下载，控制加载本地插件文件的选择
 */
public class DownloadPluginManager {

    private static final String TAG = DownloadPluginManager.class.getSimpleName();
    private static DownloadPluginManager instance = new DownloadPluginManager();

    private DownloadFileListener mDownloadFileListener;
    private ExecutorService newSingleThreadExecutor;

    private Gson gson = new Gson();
    private Context mContext;

    public static DownloadPluginManager getInstance() {
        return instance;
    }

    public DownloadPluginManager init(Context context) {
        this.mContext = context;
        return instance;
    }

    public DownloadPluginManager setDownloadFileListener(DownloadFileListener mDownloadFileListener) {
        this.mDownloadFileListener = mDownloadFileListener;
        return instance;
    }

    /**
     * 检查是否有新的插件管理器
     *
     * @return
     */
    public DownloadPluginManager checkNewVersionByManager() {
        //检查manager apk版本
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(() -> {
            try {
                //找到manager apk 文件地址
                File managerFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_manager_name);
                if (managerFile.exists()) {
                    checkManagerVersionByOnLine(managerFile);
                } else {
                    copyManagerForAssets();
                    File assetsManagerFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_manager_name);
                    checkManagerVersionByOnLine(assetsManagerFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return instance;
    }

    /**
     * 检查是否有新的 插件
     *
     * @return
     */
    public DownloadPluginManager checkNewVersionByPlugin() {
        //检查plugin 包版本
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(() -> {
            try {
                File pluginFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
                if (pluginFile.exists()) {
                    //获取线上插件版本
                    checkPluginVersionByOnLine(pluginFile);
                } else {
                    copyPluginForAssets();
                    File assetsPluginFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
                    checkPluginVersionByOnLine(assetsPluginFile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return instance;
    }

    /**
     * plugin
     * 与线上版本检测
     *
     * @param pluginFile
     */
    private void checkPluginVersionByOnLine(File pluginFile) {
        getConfigFile(ServerConfig.config_file_url, dPluginInfoBean -> {
            if (dPluginInfoBean == null) {
                //未获取到线上版本
                //加载当前file 文件
                if (mDownloadFileListener != null) {
                    Log.e(TAG, "插件获取不到线上版本 加载旧文件");
                    mDownloadFileListener.loadPluginZip(pluginFile);
                }
                return;
            }
            try {
                PluginConfig pluginConfig = PluginUtils.getPluginInfoFromPluginUnpackedDir(pluginFile);
                String localVersion = pluginConfig.UUID_NickName;
                String onlineVersionName = dPluginInfoBean.getPlugin_version_name();
                if (onlineVersionName.compareToIgnoreCase(localVersion) > 0) {
                    //有新版本
                    downloadPluginFile(dPluginInfoBean);
                } else {
                    if (mDownloadFileListener != null) {
                        mDownloadFileListener.loadPluginZip(pluginFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * manager
     * 与线上版本检测
     *
     * @param managerFile
     */
    private void checkManagerVersionByOnLine(File managerFile) {
        getConfigFile(ServerConfig.config_file_url, dPluginInfoBean -> {
            if (dPluginInfoBean == null) {
                //未获取到线上版本
                //加载当前file 文件
                if (mDownloadFileListener != null) {
                    Log.e(TAG, "manager获取不到线上版本 加载旧文件");
                    mDownloadFileListener.loadManagerFile(managerFile);
                }
                return;
            }
            String onlineVersionName = dPluginInfoBean.getManager_version_name();
            //获取本地manager apk 版本
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(managerFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            //线上包与本地包比较
            if (onlineVersionName.compareToIgnoreCase(info.versionName) > 0) {
                //有新版本
                downloadManagerFile(dPluginInfoBean);
            } else {
                //当前就是最新版本 直接加载
                if (mDownloadFileListener != null) {
                    mDownloadFileListener.loadManagerFile(managerFile);
                }
            }
        });
    }

    /**
     * 先从assets里面把manager拷贝出来
     */
    private void copyManagerForAssets() {
        try {
            File managerAssetsFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_manager_name);
            InputStream is = mContext.getAssets().open(PluginConstant.str_manager_name);
            FileUtils.copyInputStreamToFile(is, managerAssetsFile);
        } catch (Exception e) {
            throw new RuntimeException("从assets中复制apk出错", e);
        }
    }


    /**
     * 先从assets里面把manager拷贝出来
     */
    private void copyPluginForAssets() {
        try {
            File managerAssetsFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
            InputStream is = mContext.getAssets().open(PluginConstant.str_plugin_name);
            FileUtils.copyInputStreamToFile(is, managerAssetsFile);
        } catch (Exception e) {
            throw new RuntimeException("从assets中复制apk出错", e);
        }
    }

    /**
     * 插件管理器下载完成
     */
    public void downLoadManagerSuccess() {
        if (mDownloadFileListener != null) {
            File managerFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_manager_name);
            if (managerFile.exists()) {
                mDownloadFileListener.downLoadManagerSuccess(managerFile);
            }
        }
    }

    /**
     * 插件下载完成
     */
    public void downLoadPluginSuccess() {
        //插件下载完需要覆盖掉当前使用的插件包
        File newPluginFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_u_downloadPlugin_name);
        if (!newPluginFile.exists()) {
            Log.e(TAG, "新插件文件未找到！");
            return;
        }
        File oldPluginFile = new File(PluginConstant.strDownloadDir + PluginConstant.str_plugin_name);
        try {
            FileUtils.copyFile(newPluginFile, oldPluginFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "新插件文件拷贝失败！");
        }
        if (mDownloadFileListener != null) {
            mDownloadFileListener.downLoadPluginSuccess();
        }
    }

    /**
     * 下载服务端配置插件信息 包括加载器 和 插件zip
     *
     * @param url
     */
    public void getConfigFile(final String url, OnNextListener<DPluginInfoBean> listener) {
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(() -> {
            try {
                String result = HttpWriteFileQuest.getTxtFileContent(url);
                if (TextUtils.isEmpty(result)) {
                    throw new Exception("未获取到版本信息  ");
                }
                Log.e(TAG, result);
                DPluginInfoBean dPluginInfoBean = gson.fromJson(result, DPluginInfoBean.class);
                if (dPluginInfoBean == null) {
                    return;
                }
                listener.onNext(dPluginInfoBean);
            } catch (Exception e) {
                listener.onNext(null);
                e.printStackTrace();
            }
        });
    }

    /**
     * 下载manager文件
     *
     * @param dPluginInfoBean
     */
    public void downloadManagerFile(DPluginInfoBean dPluginInfoBean) {
        String manager_url = dPluginInfoBean.getManager_url();
        if (TextUtils.isEmpty(manager_url)) {
            Log.e(TAG, "下载manager文件地址不存在");
            return;
        }
        if (!manager_url.contains("http://")) {
            manager_url = ServerConfig.config_host + manager_url;
        }
        SDownloadService.startDownloadManager(mContext, manager_url);
    }

    /**
     * 下载plugin文件
     *
     * @param dPluginInfoBean
     */
    public void downloadPluginFile(DPluginInfoBean dPluginInfoBean) {
        String plugin_url = dPluginInfoBean.getPlugin_url();
        if (TextUtils.isEmpty(plugin_url)) {
            Log.e(TAG, "下载plugin文件地址不存在");
            return;
        }
        if (!plugin_url.contains("http://")) {
            plugin_url = ServerConfig.config_host + plugin_url;
        }
        SDownloadService.startDownloadPlugin(mContext, plugin_url);
    }
}
