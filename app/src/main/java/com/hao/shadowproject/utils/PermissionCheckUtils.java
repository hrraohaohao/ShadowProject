package com.hao.shadowproject.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionCheckUtils {

    public static boolean hasPermission(Activity activity, int permissionRequestCode, String... manifestPermission) {
        int flag = 0;
        try {
            if (activity != null && manifestPermission != null && manifestPermission.length > 0) {
                for (int i = 0; i < manifestPermission.length; i++) {
                    if (ContextCompat.checkSelfPermission(activity, manifestPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                        flag++;
                    }
                }
            }
            if (flag > 0) {
                ActivityCompat.requestPermissions(activity, manifestPermission, permissionRequestCode);
            }
        } catch (Exception e) {

        }
        return flag == 0;
    }
}
