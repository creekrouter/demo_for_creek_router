package com.hi.mydemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hi.mydemo.tools.activity.ActivityLauncher;
import com.hi.mydemo.tools.download.StatusListener;
import com.hi.mydemo.tools.download.Task;
import com.hi.mydemo.tools.load.PluginApk;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, StatusListener {


    private String mailUrl = "http://81.70.151.252/download/files/email.apk";
    private Task mHeadTask = null;

    private final int KEY_START = 100;
    private final int KEY_PROGRESS = 200;
    private final int KEY_FINISH = 300;
    private final int KEY_FAIL = 400;


    private ProgressDialog mDialog;
    EditText editText;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Task task = null;
            switch (msg.what) {
                case KEY_START:
                    task = (Task) msg.obj;
                    if (task.getType() == Task.TaskType.DownLoad) {
                        mDialog.setTitle("下载:" + task.getApk().fileName());
                    } else {
                        mDialog.setTitle("解压:" + task.getApk().fileName());
                    }
                    mDialog.setMax(100);
                    mDialog.setProgress(0);
                    mDialog.show();
                    break;
                case KEY_PROGRESS:
                    mDialog.setProgress(msg.arg1);
                    break;
                case KEY_FINISH:
                    task = (Task) msg.obj;
                    if (task.getNextTask() == null) {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "插件下载、解压完成!", Toast.LENGTH_SHORT).show();
                    } else {
                        task.getNextTask().startRun();
                    }
                    break;
                case KEY_FAIL:
                    mDialog.dismiss();
                    String info = (String) msg.obj;
                    Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.button_1).setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);

        editText = findViewById(R.id.http_add);

        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setTitle("");
        mDialog.setMax(100);
        mDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.setCancelable(false);
        mDialog.setProgress(0);
    }

    private void initData() {
        mailUrl = editText.getText().toString().trim();
        PluginApk.init(this);
        PluginApk mailApk = new PluginApk("mail.apk", "mail", mailUrl, "com.creek.router.init.Plugin_Initializer_MailCore");
        ActivityLauncher.allPlugins.put(mailApk.group(), mailApk);
        mHeadTask = new Task(Task.TaskType.DownLoad, mailApk, this)
                .appendNextTask(new Task(Task.TaskType.UnZip, mailApk, this));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_1:
                initData();
                mHeadTask.startRun();
                break;
            case R.id.button_2:
                if (mHeadTask == null) {
                    initData();
                }
                PluginApk loginApk = ActivityLauncher.allPlugins.get("mail");
                if (loginApk != null && loginApk.loadPlugin(this)) {
                    loginApk.launchHomePage(this);
                }
                break;
        }
    }

    @Override
    public void onStart(Task task) {
        Message msg = Message.obtain();
        msg.what = KEY_START;
        msg.obj = task;
        handler.sendMessage(msg);
    }

    @Override
    public void onProgress(int progress, long currentLength, long totalLength) {
        Message msg = Message.obtain();
        msg.what = KEY_PROGRESS;
        msg.arg1 = progress;
        msg.obj = (currentLength / 1000) + "/" + (totalLength / 1000);
        handler.sendMessage(msg);
    }

    @Override
    public void onFinish(Task task, String info) {
        Message msg = Message.obtain();
        msg.what = KEY_FINISH;
        msg.obj = task;
        handler.sendMessage(msg);
    }

    @Override
    public void onFail(Task task, String errorInfo) {
        Message msg = Message.obtain();
        msg.what = KEY_FAIL;
        if (task.getType() == Task.TaskType.DownLoad) {
            msg.obj = "下载错误：" + errorInfo;
        } else {
            msg.obj = "解压错误：" + errorInfo;
        }
        handler.sendMessage(msg);
    }
}