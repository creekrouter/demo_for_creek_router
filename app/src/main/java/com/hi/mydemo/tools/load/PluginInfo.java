package com.hi.mydemo.tools.load;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public abstract class PluginInfo {
    private static String baseOptDir;
    private static String downloadDir;
    private static String baseUnzipDir;
    private static ClassLoader classLoader;

    private DexClassLoader dexClassLoader;
    private Resources resources;
    private DisplayMetrics displayMetrics;
    private Configuration configuration;


    public static void init(Context context) {
        baseOptDir = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
        downloadDir = context.getDir("download", Context.MODE_PRIVATE).getAbsolutePath();
        baseUnzipDir = context.getDir("unzip", Context.MODE_PRIVATE).getAbsolutePath();
        classLoader = context.getClassLoader();
    }


    public abstract String fileName();

    public abstract String group();


    public abstract String httpUrl();

    public abstract String routeTable();

    public final String optimizedDir() {
        return baseOptDir + "/" + fileName();
    }

    public final String downloadDir() {
        return downloadDir;
    }

    public final String pluginPath() {
        return downloadDir() + "/" + fileName();
    }

    public final String unArchiverPath() {
        return baseUnzipDir + "/" + fileName();
    }

    public final String libSoPath() {
        return unArchiverPath() + "/lib/arm64-v8a";
    }

    protected final ClassLoader getClassLoader() {
        return classLoader;
    }

    public final Resources resources() {
        if (resources != null) {
            return resources;
        }
        File apk = new File(pluginPath());
        if (apk.exists()) {
            try {
                AssetManager manager = AssetManager.class.newInstance();
                Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
                addAssetPath.invoke(manager, pluginPath());
                resources = new Resources(manager, displayMetrics, configuration);
            } catch (Exception e) {
                resources = null;
            }
        }
        return resources;
    }

    public final DexClassLoader dexClassLoader() {
        if (dexClassLoader != null) {
            return dexClassLoader;
        }
        File apk = new File(pluginPath());
        File unzip = new File(libSoPath());
        if (apk.exists() && unzip.exists()) {
            dexClassLoader = new DexClassLoader(pluginPath(), optimizedDir(), libSoPath(), getClassLoader());
            return dexClassLoader;
        }
        return null;
    }

}
