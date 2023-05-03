package com.hi.mydemo.tools.download;

import com.hi.mydemo.tools.file.ZipArchiver;
import com.hi.mydemo.tools.load.PluginApk;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class Task implements StatusListener {
    public enum TaskType {
        DownLoad, UnZip
    }

    private Task nextTask = null;
    private TaskType type;
    private PluginApk apk;
    private StatusListener listener;

    public Task(TaskType type, PluginApk pluginApk, StatusListener listener) {
        this.type = type;
        this.apk = pluginApk;
        this.listener = listener;
    }

    public void startRun() {
        if (this.type == TaskType.DownLoad) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    download();
                }
            }).start();
        } else if (this.type == TaskType.UnZip) {
            unzip();
        }
    }

    private void download() {
        boolean writeComplete = false;
        listener.onStart(Task.this);
        try {
            URL myURL = new URL(apk.httpUrl());
            URLConnection conn = myURL.openConnection();
            conn.connect();
            int contentLength = conn.getContentLength();
            InputStream is = conn.getInputStream();
            if (is == null) {
                listener.onFail(Task.this, "stream is null");
                return;
            }

            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(apk.pluginPath());
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;

                //计算当前下载进度
                int progress = (int) (100 * downLoadFileSize / contentLength);
                listener.onProgress(progress, downLoadFileSize, contentLength);
                //更新进度条
            } while (true);
            is.close();
            writeComplete = true;
        } catch (Exception ex) {
            writeComplete = false;
            listener.onFail(Task.this, ex.getMessage());
        }

        if (writeComplete) {
            //下载完成，并返回保存的文件路径
            listener.onFinish(Task.this, apk.pluginPath());
        } else {
            listener.onFail(Task.this, "download error！");
        }
    }

    private void unzip() {
        ZipArchiver.doUnArchiver(apk, this);
    }

    public Task getNextTask() {
        return nextTask;
    }

    public Task appendNextTask(Task nextTask) {
        if (this.nextTask == null) {
            this.nextTask = nextTask;
        } else {
            this.nextTask.appendNextTask(nextTask);
        }
        return this;
    }

    public TaskType getType() {
        return type;
    }

    public PluginApk getApk() {
        return apk;
    }

    @Override
    public void onStart(Task task) {
        if (this.listener != null) {
            this.listener.onStart(this);
        }
    }

    @Override
    public void onProgress(int progress, long currentLength, long totalLength) {
        if (this.listener != null) {
            this.listener.onProgress(progress, currentLength, totalLength);
        }
    }

    @Override
    public void onFinish(Task task, String info) {
        if (this.listener != null) {
            this.listener.onFinish(this, info);
        }
    }

    @Override
    public void onFail(Task task, String errorInfo) {
        if (this.listener != null) {
            this.listener.onFail(this, errorInfo);
        }
    }

}
