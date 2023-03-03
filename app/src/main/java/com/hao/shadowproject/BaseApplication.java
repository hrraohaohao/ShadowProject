package com.hao.shadowproject;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.webkit.WebView;

import com.hao.app_bridge_lib.BasicConfig;
import com.hao.app_bridge_lib.HostProvider;
import com.hao.shadowproject.utils.ProcessUtils;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.DynamicRuntime;
import com.tencent.shadow.sample.introduce_shadow_lib.AndroidLoggerFactory;


/**
 * @author raohaohao
 * @date 2018/11/25
 */

public class BaseApplication extends Application {

    private static String TAG = BaseApplication.class.getName();
    public boolean isLog = BuildConfig.DEBUG;

    public static BaseApplication application = null;
    public static Context context = null;

    public static BaseApplication getApp() {
        return application;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        application = this;
        context = this.getApplicationContext();

        //跨进程的都需要两次初始化（宿主和插件pps）
        LoggerFactory.setILoggerFactory(new AndroidLoggerFactory());
        if (ProcessUtils.isProcess(this, ":plugin")) {
            //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
            //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
            //因此这里恢复加载上一次的runtime
            DynamicRuntime.recoveryRuntime(this);
        }
        HostProvider.init(this);
        detectNonSdkApiUsageOnAndroidP();
        setWebViewDataDirectorySuffix();
    }

    /**
     * 初始化Config的配置
     */
    private void initConfig() {
        BasicConfig.isLog = isLog;
        BasicConfig.test = "哈哈哈哈哈哈哈哈哈哈哈";
    }


    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    private static void setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        WebView.setDataDirectorySuffix(Application.getProcessName());
    }
}
