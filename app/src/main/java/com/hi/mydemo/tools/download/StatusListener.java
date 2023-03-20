package com.hi.mydemo.tools.download;

public interface StatusListener {
    void onStart(Task task);

    void onProgress(int progress, long currentLength, long totalLength);

    void onFinish(Task task,String info);

    void onFail(Task task,String errorInfo);
}
