package com.getsmartkart.smartkart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Nathan on 2017-03-18.
 */

// Massive thanks to this stackoverflow post: https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions
public class PermsUtil {

    private static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.VIBRATE};

    public static void getPermissions(Activity activity) {
        int PERMISSION_ALL = 1;
        if(!hasPermissions(activity, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean checkPermission(Activity activity){
        if(!hasPermissions(activity, PERMISSIONS)){
            return true;
        }
        return false;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
