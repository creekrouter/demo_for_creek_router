package com.hi.mydemo.tools.file;


import com.hi.mydemo.tools.download.StatusListener;

import net.lingala.zip4j.progress.ProgressMonitor;

public class MyProgressMonitor extends ProgressMonitor {

    private StatusListener listener;
    private int lastPercentDone = 0;

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateWorkCompleted(long workCompleted) {
        super.updateWorkCompleted(workCompleted);
        if (listener != null && getPercentDone() != lastPercentDone) {
            lastPercentDone = getPercentDone();
            listener.onProgress(lastPercentDone, workCompleted, getTotalWork());
            if (lastPercentDone >= 100) {
                listener.onFinish(null, "");
            }
        }
    }
}
