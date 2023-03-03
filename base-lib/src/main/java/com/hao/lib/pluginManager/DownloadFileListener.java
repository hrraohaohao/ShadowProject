package com.hao.lib.pluginManager;

import java.io.File;

/**
 * @author：atar
 * @date: 2021/6/5
 * @description: 每次启动加载插件前检查是否有新的
 */
public interface DownloadFileListener {

    void loadManagerFile(File manager);

    void loadPluginZip(File plugin);

    void downLoadManagerSuccess(File manager);

    void downLoadPluginSuccess();
}
