package com.tf.push;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class MainActivity extends Activity implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private RtmpCamera1 rtmpCamera1;
    private Button button;
    private Button switchCamera;
    private Button pullBut;
    private EditText urlpush;
    private EditText urlpull;
    SurfaceView surfaceViewTop;
    private String[] options = new String[]{":fullscreen", ":network-caching=350"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
        init();

//        rtmpCamera1 = new RtmpCamera1(this, this);
        rtmpCamera1 = new RtmpCamera1(surfaceViewTop, this);
        rtmpCamera1.setReTries(10);
        rtmpCamera1.setLogs(true);
        rtmpCamera1.enableAutoFocus();
        surfaceViewTop.getHolder().addCallback(this);



    }
    private void init(){
        surfaceViewTop = findViewById(R.id.surfaceView_top);
        button =  findViewById(R.id.b_start_stop);
        pullBut = findViewById(R.id.start_pull);
        switchCamera = findViewById(R.id.switch_camera);
        pullBut = findViewById(R.id.start_pull);
        urlpull = findViewById(R.id.url_pull);
        urlpush = findViewById(R.id.url_push);
        switchCamera.setOnClickListener(this);
        button.setOnClickListener(this);
        pullBut.setOnClickListener(this);
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            button.setText(getResources().getString(R.string.start_button));
        }
        rtmpCamera1.stopPreview();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() ==  R.id.b_start_stop) {
            if (!rtmpCamera1.isStreaming()) {
                if (rtmpCamera1.isRecording()
                        || rtmpCamera1.prepareVideo(640, 480, 30, 1200 * 1024, 0, 90) &&  rtmpCamera1.prepareAudio()) {
                    button.setText(R.string.stop_button);
                    rtmpCamera1.startStream(urlpush.getText().toString());
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                button.setText(R.string.start_button);
                rtmpCamera1.stopStream();
            }
        } else if ( R.id.switch_camera == view.getId()) {

            try {
                rtmpCamera1.switchCamera();
            } catch (CameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtmpCamera1.reTry(5000, reason)) {
                    Toast.makeText(MainActivity.this, "Retry", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                            .show();
                    rtmpCamera1.stopStream();
                    button.setText(R.string.start_button);
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    @Override
    public void onConnectionStartedRtmp(@NonNull String s) {
        Log.d("@TF@", "onConnectionStartedRtmp: "+s);
    }
}
