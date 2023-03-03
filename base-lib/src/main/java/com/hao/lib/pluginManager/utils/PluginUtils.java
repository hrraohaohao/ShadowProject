package com.hao.lib.pluginManager.utils;

import com.hao.lib.pluginManager.bean.PluginConfig;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;

/**
 * @author raohaohao
 * @version 1.0
 * @data 2023/3/1
 */
public class PluginUtils {
    private static final String CONFIG_FILENAME = "config.json";

    /**
     * 解析plugin.zip config信息
     *
     * @param pluginUnpackDir
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static PluginConfig getPluginInfoFromPluginUnpackedDir(File pluginUnpackDir)
            throws IOException, JSONException {
        if (pluginUnpackDir == null) {
            throw new NullPointerException("Zip 包不存在");
        }
        ZipFile zipFile = new ZipFile(pluginUnpackDir);
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(CONFIG_FILENAME));
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder("");
        String lineStr;
        try {
            while ((lineStr = br.readLine()) != null) {
                stringBuilder.append(lineStr).append("\n");
            }
        } finally {
            br.close();
        }
        String versionJson = stringBuilder.toString();
        return PluginConfig.parseFromJson(versionJson, pluginUnpackDir);
    }
}
