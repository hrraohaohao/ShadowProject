package com.hao.plugin_app.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BaseApplication extends Application {
    public static Application application = null;
    public static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = this.getApplicationContext();
        Log.e("BaseApplication", "onCreate 我是新版本++++++++");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
