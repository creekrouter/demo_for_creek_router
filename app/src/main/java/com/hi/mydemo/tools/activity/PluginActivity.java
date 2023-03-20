package com.hi.mydemo.tools.activity;


import static com.hi.mydemo.tools.activity.ActivityLauncher.KEY_ACTIVITY_ANNOTATION;
import static com.hi.mydemo.tools.activity.ActivityLauncher.KEY_PLUGIN_GROUP_FROM;
import static com.hi.mydemo.tools.activity.ActivityLauncher.KEY_PLUGIN_GROUP_TO;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.creek.router.CreekRouter;
import com.creek.router.Filters;
import com.creek.router.annotation.CreekClass;
import com.creek.router.annotation.CreekMethod;
import com.hi.mydemo.R;
import com.hi.mydemo.tools.load.PluginApk;

import java.util.HashMap;
import java.util.Map;


@CreekClass(path = "plugin_helper_PluginActivity")
public class PluginActivity extends FragmentActivity {

    public PluginProxy proxy;
    private boolean mInit = false;
    private PluginApk apk;
    private Object realInstance;
    private String class_annotate, groupTo;

    protected boolean beforeOnCreate(Intent intent) {
        if (intent == null) {
            return false;
        }
//        class_annotate = intent.getStringExtra(KEY_ACTIVITY_ANNOTATION);
        if (class_annotate == null || class_annotate.length() == 0) {
            return false;
        }

        String groupFrom = intent.getStringExtra(KEY_PLUGIN_GROUP_FROM);
        if (groupFrom == null || groupFrom.length() == 0) {
            return false;
        }

        if (groupTo == null || groupTo.length() == 0) {
            return false;
        }

        apk = ActivityLauncher.allPlugins.get(groupTo);
        if (apk == null) {
            return false;
        }

        if (realInstance == null) {
            return false;
        }
        proxy = CreekRouter.create(PluginProxy.class, realInstance, groupFrom, groupTo);
        if (proxy == null) {
            return false;
        }

        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null) {
            class_annotate = getIntent().getStringExtra(KEY_ACTIVITY_ANNOTATION);
            groupTo = getIntent().getStringExtra(KEY_PLUGIN_GROUP_TO);
            Map<Filters, String> filterMapTo = new HashMap<>();
            filterMapTo.put(Filters.Group, groupTo);
            realInstance = CreekRouter.getBean(class_annotate, filterMapTo);
        }
        super.onCreate(savedInstanceState);
        RelativeLayout rootView = new RelativeLayout(this);
        setContentView(rootView);

        mInit = beforeOnCreate(getIntent());
        if (mInit) {
            proxy.attach(this);
            proxy.rootView(rootView);
            proxy.resources(apk.resources());
            setPluginTheme();
            proxy.onCreate(savedInstanceState);
        } else {
            TextView tv = new TextView(this);
            tv.setText("Load Error");
            tv.setTextSize(50);
            rootView.addView(tv);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mInit) {
            proxy.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInit) {
            proxy.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInit) {
            proxy.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mInit) {
            proxy.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInit) {
            proxy.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInit) {
            proxy.onBackPressed();
        }
    }

    private void setPluginTheme() {
        Resources.Theme theme = apk.resources().newTheme();
        theme.setTo(getBaseContext().getTheme());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTheme(theme);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mInit) {
            return apk.dexClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        if (mInit) {
            return apk.resources();
        }
        return super.getResources();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mInit) {
            proxy.onWindowFocusChanged(hasFocus);
        } else {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mInit) {
            proxy.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @CreekMethod(path = "host_get_plugin_activity_instance")
    public Object getPluginActivity() {
        return realInstance;
    }
}