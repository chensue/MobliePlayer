package com.example.czy.myapplication.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.activity.PlayActivity;

/**
 * 音量变化广播接收器
 * Created by CZY on 2017/1/31.
 */
public class VolumeReceiver extends BroadcastReceiver {

//    private ImageView iv_volume;
//
//    private SeekBar seekBar_volume;

    /**
     * 音频管理器
     */
    private AudioManager audioManager;
    /**
     * 整个控制面板显示
     */
//    private LinearLayout ll_volumeControl;

//    private final static int HANDLER_VOLUME = 0;
    private VolumeCenterShow show;
    //ImageView iv_volume, SeekBar seekBar_volume,LinearLayout ll_volumeControl,
    public VolumeReceiver(Context context,VolumeCenterShow show) {
//        this.iv_volume = iv_volume;
//        this.seekBar_volume = seekBar_volume;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        this.ll_volumeControl = ll_volumeControl;
        this.show = show;
    }

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg)
//        {
//            super.handleMessage(msg);
//            ll_volumeControl.setVisibility(View.GONE);
//        }
//    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
//            handler.removeMessages(HANDLER_VOLUME);
//            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            if (volume == 0) {
//                iv_volume.setImageResource(R.drawable.mute);
//            } else {
//                iv_volume.setImageResource(R.drawable.volume);
//            }
//            seekBar_volume.setProgress(volume);
//            ll_volumeControl.setVisibility(View.VISIBLE);
//            handler.sendEmptyMessageDelayed(HANDLER_VOLUME, 1000);
            show.show();
        }
    }

    public interface VolumeCenterShow
    {
        public void show();
    }

}
