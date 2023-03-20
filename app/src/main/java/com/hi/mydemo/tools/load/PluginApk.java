package com.hi.mydemo.tools.load;

import android.content.Context;

import com.creek.router.CreekRouter;
import com.hi.mydemo.MyApplication;
import com.hi.mydemo.tools.activity.ActivityLauncher;


public class PluginApk extends PluginInfo implements Load {

    private String fileName, group, httpUrl, routeTable;
    private boolean isLoadSuccess = false;
    private String homePagePath;

    public PluginApk(String fileName, String group, String httpUrl, String routeTable) {
        this.fileName = fileName;
        this.group = group;
        this.httpUrl = httpUrl;
        this.routeTable = routeTable;
    }

    @Override
    public String fileName() {
        return fileName;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String httpUrl() {
        return httpUrl;
    }

    @Override
    public String routeTable() {
        return routeTable;
    }

    @Override
    public boolean loadPlugin(Context context) {
        if (isLoadSuccess) {
            return true;
        }
        if (dexClassLoader() == null || resources() == null) {
            return false;
        }
        try {
            dexClassLoader().loadClass(routeTable()).newInstance();
        } catch (Exception e) {
            return false;
        }
        CreekRouter.methodRun("Mail_Plugin_Init", context, MyApplication.app, unArchiverPath());

        homePagePath = CreekRouter.methodRun("Mail_Plugin_Get_First_Page_Path");

        isLoadSuccess = true;
        return true;
    }

    @Override
    public void launchHomePage(Context context) {
        if (!isLoadSuccess || homePagePath == null || homePagePath.length() == 0) {
            return;
        }
        ActivityLauncher.startActivity(context, homePagePath, group());
    }

}
