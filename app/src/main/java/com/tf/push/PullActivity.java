package com.tf.push;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;

import androidx.annotation.Nullable;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

public class PullActivity extends Activity implements NodePlayerDelegate {

    NodePlayerView player_view;

    private NodePlayer np;

    EditText url_pull;
    Button start_pull;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull);
        player_view = findViewById(R.id.play_surface);
        url_pull = findViewById(R.id.url_pull);
        start_pull = findViewById(R.id.start_pull);

        //        拉流
        np = new NodePlayer(this);
        player_view.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        player_view.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFill);
        np.setPlayerView(player_view);
        np.setNodePlayerDelegate(this);
        np.setHWEnable(true);

        findViewById(R.id.start_pull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!np.isPlaying()){
                    np.setInputUrl(url_pull.getText().toString());
                    np.start();
                    start_pull.setText(getString(R.string.stop_player));
                }else {
                    np.stop();
                    start_pull.setText(getString(R.string.start_player));
                }
            }
        });

    }


    @Override
    public void onEventCallback(NodePlayer player, int event, String msg) {
        switch (event) {
            case 1000:
                // 正在连接视频
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PullActivity.this, "Connecting", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 1001:
                // 视频连接成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PullActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 1002:
                // 视频连接失败 流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止
//                nodePlayer.stopPlay();
                break;
            case 1003:
                // 视频开始重连,自动重连总开关
//                nodePlayer.stopPlay();
                Log.d("@TF@", "onEventCallback: 1003");
                break;
            case 1004:
                // 视频播放结束
                Log.d("@TF@", "onEventCallback: 1004");
                break;
            case 1005:
                // 网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                Log.d("@TF@", "onEventCallback: 1005");
                break;
            case 1006:
                //RTMP连接播放超时
                Log.d("@TF@", "onEventCallback: 1006");
                break;
            case 1100:
                // 播放缓冲区为空
//				System.out.println("NetStream.Buffer.Empty");
                Log.d("@TF@", "onEventCallback: 1100");
                break;
            case 1101:
                // 播放缓冲区正在缓冲数据,但还没达到设定的bufferTime时长
//				System.out.println("NetStream.Buffer.Buffering");
                Log.d("@TF@", "onEventCallback: 1101");
                break;
            case 1102:
                // 播放缓冲区达到bufferTime时长,开始播放.
                // 如果视频关键帧间隔比bufferTime长,并且服务端没有在缓冲区间内返回视频关键帧,会先开始播放音频.直到视频关键帧到来开始显示画面.
//				System.out.println("NetStream.Buffer.Full");
                Log.d("@TF@", "onEventCallback: 1102");
                break;
            case 1103:
//				System.out.println("Stream EOF");
                // 客户端明确收到服务端发送来的 StreamEOF 和 NetStream.Play.UnpublishNotify时回调这里
                // 注意:不是所有云cdn会发送该指令,使用前请先测试
                // 收到本事件，说明：此流的发布者明确停止了发布，或者因其网络异常，被服务端明确关闭了流
                // 本sdk仍然会继续在1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                Log.d("@TF@", "onEventCallback: 1103");
                break;
            case 1104:
                //解码后得到的视频高宽值 格式为:{width}x{height}
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 停止播放
         */
        np.stop();

        /**
         * 释放资源
         */
        np.release();
    }
}
