package com.hao.lib.pluginManager;

import android.os.Environment;

import com.hao.lib.BuildConfig;

/**
 * @author：atar
 * @date: 2021/6/5
 * @description:
 */
public class PluginConstant {

    //存放host 地址key
    public static final String HOST_KEY = "HOST_KEY";
    //保存配置文件总版本key
    public static final String SAVE_CONFIG_FILE_VERSION_KEY = "SAVE_CONFIG_FILE_VERSION_KEY";
    //保存配置文件内容key
    public static final String SAVE_CONFIG_FILE_CONTENT_KEY = "SAVE_CONFIG_FILE_CONTENT_KEY";
    //下载文件目录
    public static final String strDownloadDir = Environment.getExternalStorageDirectory() +
            "/.cache/download_plugin_apk/com_hao_shadowProject/";

    //下载到本地加载器 文件名称
    public static final String str_manager_name = !BuildConfig.DEBUG ? "shadow-manager-release.apk" : "shadow-manager-debug.apk";
    //实际使用到的 本地插件 文件名称
    public static final String str_plugin_name = !BuildConfig.DEBUG ? "plugin-release.zip" : "plugin-debug.zip";

    // 下载到本地插件 文件名称 临时文件
    public static final String str_u_downloadPlugin_name = "plugin-debug0.zip";

    //下载管理器标志
    public static final int DOWNLOAD_MANAGER = 0x900009;
    //下载插件标志
    public static final int DOWNLOAD_PLUGIN = 0x900010;


}
