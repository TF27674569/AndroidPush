package com.fun.push;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import androidx.core.content.ContextCompat;

public class PushManager {

    private Context mContext;

    private RtmpCamera1 mCamera;

    @SuppressLint("StaticFieldLeak")
    private static final PushManager sInstance = new PushManager();

    public static PushManager getInstance() {
        return sInstance;
    }

    private PushManager() {

    }


    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null!");
        }
        mContext = context.getApplicationContext();
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void createCamera(ConnectCheckerRtmp connectCheckerRtmp) {
        check();
        mCamera = new RtmpCamera1(mContext, connectCheckerRtmp);
        // 重试次数
        mCamera.setReTries(10);
        // 开启日志
        mCamera.setLogs(true);
        // 自动对焦
        mCamera.enableAutoFocus();
    }

    public boolean isStreaming(){
        check();
        return mCamera.isStreaming();
    }

    public void push(String pushUrl) {
        check();

        if (!isStreaming()) {
            if (mCamera.isRecording()|| mCamera.prepareVideo(640, 480, 30, 1200 * 1024, 0, 90) &&
                    mCamera.prepareAudio()) {
                mCamera.startStream(pushUrl);
            }
        }
    }

    private void check() {
        if (mContext == null) {
            throw new NullPointerException("must be call init!");
        }
    }

    public void stop() {
        if (isStreaming()) {
            mCamera.stopStream();
        }
    }
}
