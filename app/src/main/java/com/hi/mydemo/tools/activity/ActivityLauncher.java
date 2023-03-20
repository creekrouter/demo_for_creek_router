package com.hi.mydemo.tools.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.creek.router.annotation.CreekMethod;
import com.hi.mydemo.tools.load.PluginApk;

import java.util.HashMap;
import java.util.Map;


public class ActivityLauncher {
    public static Map<String, PluginApk> allPlugins = new HashMap<>();
    public static final String hostGroup = "host";

    public final static String KEY_ACTIVITY_ANNOTATION = "activity_annotation";
    public static final String KEY_PLUGIN_GROUP_FROM = "plugin_group_from";
    public static final String KEY_PLUGIN_GROUP_TO = "plugin_group_to";

    public static void startActivity(Context context, String activityPath, String toGroup) {
        startActivity(context, activityPath, toGroup, null, 0);
    }

    @CreekMethod(path = "host_tools_launchActivity")
    public static void startActivity(Context context, String activityPath, String toGroup, Intent intent, int requestCode) {
        if (context == null) {
            return;
        }
        if (intent == null) {
            intent = new Intent();
        }
        ComponentName newComponent = new ComponentName(context, PluginActivity.class);
        intent.setComponent(newComponent);
        intent.putExtra(KEY_ACTIVITY_ANNOTATION, activityPath);
        intent.putExtra(KEY_PLUGIN_GROUP_FROM, hostGroup);
        intent.putExtra(KEY_PLUGIN_GROUP_TO, toGroup);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @CreekMethod(path = "host_tools_launchActivity2")
    public static void startActivityForResult(Fragment fragment, String activityPath, String toGroup, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), PluginActivity.class);
        intent.putExtra(KEY_ACTIVITY_ANNOTATION, activityPath);
        intent.putExtra(KEY_PLUGIN_GROUP_FROM, hostGroup);
        intent.putExtra(KEY_PLUGIN_GROUP_TO, toGroup);
        fragment.startActivityForResult(intent, requestCode);

    }
}
