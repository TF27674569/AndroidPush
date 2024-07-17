package com.tf.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fun.push.PushManager;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LunchActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        PushManager.getInstance().init(this);

        if (!PushManager.getInstance().hasPermission()) {
            Log.d("@TF@", "hasPermission: false" );
            return;
        }
        PushManager.getInstance().createCamera(new ConnectCheckerRtmp() {
            @Override
            public void onConnectionStartedRtmp(@NonNull String s) {
                Log.d("@TF@", "onConnectionStartedRtmp: " + s);
            }

            @Override
            public void onConnectionSuccessRtmp() {
                Log.d("@TF@", "onConnectionSuccessRtmp: " );
            }

            @Override
            public void onConnectionFailedRtmp(@NonNull String s) {
                Log.d("@TF@", "onConnectionFailedRtmp: "  + s);
            }

            @Override
            public void onNewBitrateRtmp(long l) {
//                Log.d("@TF@", "onNewBitrateRtmp: "  + l);
            }

            @Override
            public void onDisconnectRtmp() {
                Log.d("@TF@", "onDisconnectRtmp: "  );
            }

            @Override
            public void onAuthErrorRtmp() {
                Log.d("@TF@", "onAuthErrorRtmp: "  );
            }

            @Override
            public void onAuthSuccessRtmp() {
                Log.d("@TF@", "onAuthSuccessRtmp: "  );
            }
        });



        if (PushManager.getInstance().isStreaming()) {
            Log.d("@TF@", "isStreaming: 正在推送"  );
            return;
        }

    }

    public void pushStream(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

    public void pullStream(View view) {
        startActivity(new Intent(this,PullActivity.class));
    }

    public void startPush(View view) {
        if (PushManager.getInstance().isStreaming()) {
            Toast.makeText(this, "正在推流", Toast.LENGTH_SHORT).show();
            return;
        }

        PushManager.getInstance().push("rtmp://192.168.0.11:1935/live/demo");
    }
    public void stopPush(View view) {
        if (!PushManager.getInstance().isStreaming()) {
            Toast.makeText(this, "还未开始推流", Toast.LENGTH_SHORT).show();
            return;
        }
        PushManager.getInstance().stop();
    }
}
