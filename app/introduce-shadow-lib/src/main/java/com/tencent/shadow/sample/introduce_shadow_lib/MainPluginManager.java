package com.tencent.shadow.sample.introduce_shadow_lib;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class MainPluginManager {

    private static PluginManager sPluginManager;
    public static File pluginFile;

    public static PluginManager getPluginManager() {
        return sPluginManager;
    }

    public static File getPluginFile() {
        return pluginFile;
    }

    //加载插件管理apk
    public static void loadPluginManager(File apk) {
        if (apk == null) {
            return;
        }
        if (sPluginManager == null) {
            sPluginManager = getPluginManager(apk);
        }
    }

    public static PluginManager getPluginManager(File apk) {
        final FixedPathPmUpdater fixedPathPmUpdater = new FixedPathPmUpdater(apk);
        File tempPm = fixedPathPmUpdater.getLatest();
        if (tempPm != null) {
            return new DynamicPluginManager(fixedPathPmUpdater);
        }
        return null;
    }

    public static void setPluginFile(File file) {
        pluginFile = file;
    }
}
