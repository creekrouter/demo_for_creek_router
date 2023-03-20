package com.hi.mydemo.tools.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.creek.router.annotation.CreekMethod;

public interface PluginProxy {
    @CreekMethod(path = "plugin_activity_attache")
    void attach(Activity proxyActivity);

    @CreekMethod(path = "plugin_activity_root_view")
    void rootView(RelativeLayout rootView);


    @CreekMethod(path = "plugin_activity_resources")
    void resources(Resources res);


    @CreekMethod(path = "plugin_activity_setContentView_view")
    void setContentView(View view);

    @CreekMethod(path = "plugin_activity_setContentView_id")
    void setContentView(int layoutResID);

    @CreekMethod(path = "plugin_activity_findViewById")
    View findViewById(int id);

    /**
     * 生命周期
     */
    @CreekMethod(path = "plugin_activity_onCreate")
    void onCreate(Bundle saveInstanceState);

    @CreekMethod(path = "plugin_activity_onStart")
    void onStart();

    @CreekMethod(path = "plugin_activity_onResume")
    void onResume();

    @CreekMethod(path = "plugin_activity_onPause")
    void onPause();

    @CreekMethod(path = "plugin_activity_onStop")
    void onStop();

    @CreekMethod(path = "plugin_activity_onDestroy")
    void onDestroy();

    @CreekMethod(path = "plugin_activity_onSaveInstanceState")
    void onSaveInstanceState(Bundle outState);

    @CreekMethod(path = "plugin_activity_onTouchEvent")
    boolean onTouchEvent(MotionEvent event);

    @CreekMethod(path = "plugin_activity_onBackPressed")
    void onBackPressed();

    @CreekMethod(path = "plugin_activity_onWindowFocusChanged")
    void onWindowFocusChanged(boolean hasFocus);


    @CreekMethod(path = "plugin_activity_onActivityResult")
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
