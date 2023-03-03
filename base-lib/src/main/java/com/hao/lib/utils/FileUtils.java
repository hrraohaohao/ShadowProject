package com.hao.lib.utils;

import android.util.Log;
import com.hao.lib.net.HttpRequest;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {

    /**
     * 获取文件长度
     */
    public static int getUrlFileSize(String fileUrl) {
        int fileLength = 0;
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(fileUrl);
            httpConnection = HttpRequest.getHttpURLConnection(url, 10000);
            HttpRequest.setConHead(httpConnection);
            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();
            if (responseCode <= 400) {
                fileLength = httpConnection.getContentLength();// 设置下载长度
            }

        } catch (Exception e) {
            Log.e("FileUtils", e.getMessage());
        } finally {
            if (httpConnection != null)
                httpConnection.disconnect();// 关闭连接
        }
        return fileLength;
    }
}
