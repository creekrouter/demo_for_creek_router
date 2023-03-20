package com.hi.mydemo.tools.download;

import com.hi.mydemo.tools.file.FileTool;
import com.hi.mydemo.tools.file.ZipArchiver;
import com.hi.mydemo.tools.load.PluginApk;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            download();
        } else if (this.type == TaskType.UnZip) {
            unzip();
        }
    }

    private void download() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request.Builder builder = new Request.Builder()
                .get()
                .url(apk.httpUrl())
                .addHeader("Accept-Encoding", "identity")
                .addHeader("User-Agent", "okhttp/3.10.0[AWV/v1.0;AD/Android]");
        final Request request = builder.build();

        Call call = client.newCall(request);
        onStart(this);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(Task.this, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    File file = new File(apk.pluginPath());
                    FileTool.writeFile(file, response.body().byteStream(), response.body().contentLength(), Task.this);
                } else {
                    onFail(Task.this, "后台response code 错误");
                }

            }
        });

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
