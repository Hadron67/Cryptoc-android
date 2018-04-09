package com.hadroncfy.project4;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class MetaData {
    public static String getMetaData(Context ctx, String name, String def){
        try {
            ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return def;
        }
    }
    public static String getEncryptedExtensionName(Context ctx){
        return getMetaData(ctx, "encryptedExtensionName", "enc");
    }
}
