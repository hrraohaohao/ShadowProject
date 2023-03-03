package com.hao.lib.net.http;


import android.util.Log;

import com.hao.lib.net.HttpRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author：atar
 * @date: 2021/6/5
 * @description: 主要用于客户端本地服务网络请求
 */
public class HttpWriteFileQuest {
    private static final String TAG = HttpWriteFileQuest.class.getSimpleName();

    /**
     * 读取服务端指定文件工具
     *
     * @param FileUrl
     * @return
     */
    public static String getTxtFileContent(String FileUrl) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(FileUrl);
            httpConnection = HttpRequest.getHttpURLConnection(url, 5000);
            HttpRequest.setConHead(httpConnection);
            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                InputStream instream = httpConnection.getInputStream();
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    // 分行读取
                    while ((line = buffreader.readLine()) != null) {
                        Log.i(TAG, line);
                        result.append(line);
                    }
                    inputreader.close();
                    instream.close();
                    buffreader.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConnection != null)
                httpConnection.disconnect();// 关闭连接
        }
        return result.toString();
    }
}
