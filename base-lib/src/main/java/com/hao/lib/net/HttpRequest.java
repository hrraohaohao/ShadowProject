package com.hao.lib.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * *****************************************************************************************
 * Http网络请求
 *
 * @author: Atar
 * @createTime:2014年5月27日下午7:56:48
 * @modifyTime:
 * @version: 1.0.0
 * @description: *****************************************************************************************
 */
public class HttpRequest {
    public static int connectTimeOut = 30000; // 连接超时时间毫秒

    @SuppressLint("InlinedApi")
    public static boolean IsUsableNetWork(Context myContext) {
        boolean netSataus = false;
        if (myContext != null) {
            try {
                ConnectivityManager conMan = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMan != null) {
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != null) {
                        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                        // 移动网络
                        if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
                            netSataus = true;
                        }
                    }
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != null) {
                        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                        // wifi连接
                        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                            netSataus = true;
                        }
                    }
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != null) {
                        State blueTooth = conMan.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState();
                        // 蓝牙连接
                        if (blueTooth == State.CONNECTED || blueTooth == State.CONNECTING) {
                            netSataus = true;
                        }
                    }
                }
            } catch (Exception e) {
                netSataus = false;
            }
        }
        return netSataus;
    }

    /**
     * 根据code抛出异常
     *
     * @param statusCode
     * @author :Atar
     * @createTime:2014-12-10上午10:31:18
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public static void throwExceptionByCode(int statusCode) {
        // ShowLog.i(TAG, "statusCode---->" + statusCode);
        if (statusCode == 400) {
            String err = ExceptionEnum.HttpRequestFalse400.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse400(err, new Throwable());
        } else if (statusCode == 401) {
            String err = ExceptionEnum.HttpRequestFalse401.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse401(err, new Throwable());
        } else if (statusCode == 403) {
            String err = ExceptionEnum.HttpRequestFalse403.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse403(err, new Throwable());
        } else if (statusCode == 404) {
            String err = ExceptionEnum.HttpRequestFalse404.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse404(err, new Throwable());
        } else if (statusCode == 405) {
            String err = ExceptionEnum.HttpRequestFalse405.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse405(err, new Throwable());
        } else if (statusCode == 502) {
            String err = ExceptionEnum.HttpRequestFalse502.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse502(err, new Throwable());
        } else if (statusCode == 503) {
            String err = ExceptionEnum.HttpRequestFalse503.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse503(err, new Throwable());
        } else if (statusCode == 504) {
            String err = ExceptionEnum.HttpRequestFalse504.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse504(err, new Throwable());
        } else if (statusCode == 500) {
            String err = ExceptionEnum.HttpRequestFalse500.class.getSimpleName();
            throw (ExceptionEnum.RefelectException) new ExceptionEnum.HttpRequestFalse500(err, new Throwable());
        }
    }

    @SuppressWarnings("static-access")
    public static HttpURLConnection getHttpURLConnection(URL url, int connectTimeOut) {
        try {
            if ("https".equals(url.getProtocol())) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setDefaultHostnameVerifier(hnv);
                https.setHostnameVerifier(hnv);
                https.setDefaultSSLSocketFactory(mSSLSocketFactory);
                https.setSSLSocketFactory(mSSLSocketFactory);
                https.setConnectTimeout(3 * connectTimeOut);
                https.setReadTimeout(3 * connectTimeOut);
                return https;
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(connectTimeOut);
                httpURLConnection.setReadTimeout(connectTimeOut);
                return httpURLConnection;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 信任所有host
     */
    public static HostnameVerifier hnv = new HostnameVerifier() {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 设置https
     *
     * @author :Atar
     * @createTime:2015-9-17下午4:57:39
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressLint("TrulyRandom")
    public static void trustAllHosts() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            if (mSSLSocketFactory == null) {
                mSSLSocketFactory = sc.getSocketFactory();
            }
            HttpsURLConnection.setDefaultHostnameVerifier(hnv);
            HttpsURLConnection.setDefaultSSLSocketFactory(mSSLSocketFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSLSocketFactory mSSLSocketFactory;

    /**
     * 设置请求头
     *
     * @param httpConnection
     */

    public static void setConHead(HttpURLConnection httpConnection) {
        httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        httpConnection.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        httpConnection.setRequestProperty("Accept-Encoding", "aa");
        httpConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        httpConnection.setRequestProperty("Keep-Alive", "300");
        httpConnection.setRequestProperty("Connection", "keep-alive");
        httpConnection.setRequestProperty("Cache-Control", "max-age=0");
    }

    /**
     * netType: 1:移动网络 2:wifi连接 3:蓝牙连接
     *
     * @param myContext
     * @return
     */
    public static int getNetWorkType(Context myContext) {
        int netType = 0;
        if (myContext != null) {
            try {
                ConnectivityManager conMan = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMan != null) {
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != null) {
                        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                        // 移动网络
                        if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
                            netType = 1;
                        }
                    }
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != null) {
                        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                        // wifi连接
                        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                            netType = 2;
                        }
                    }
                    if (conMan.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH) != null && conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != null) {
                        State blueTooth = conMan.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState();
                        // 蓝牙连接
                        if (blueTooth == State.CONNECTED || blueTooth == State.CONNECTING) {
                            netType = 3;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        return netType;
    }

    /**
     * netType: 1:移动网络 2:wifi连接 3:蓝牙连接
     *
     * @param context
     * @return
     */
    public static String getNetTypeString(Context context) {
        if (getNetWorkType(context) == 1) {
            return "手机网络";
        } else if (getNetWorkType(context) == 2) {
            return "wifi连接";
        } else if (getNetWorkType(context) == 3) {
            return "蓝牙连接";
        } else {
            return "无网络";
        }
    }
}
