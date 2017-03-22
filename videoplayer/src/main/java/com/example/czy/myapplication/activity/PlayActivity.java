package com.example.czy.myapplication.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.view.MyMediaController;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 视频播放
 */
public class PlayActivity extends Activity  implements Runnable{
    public static  final String TAG = "PlayActivity";

    @Bind(R.id.surface_view)
    protected VideoView mVideoView;
    private MediaController mMediaController;
    private MyMediaController myMediaController;

    private static final int TIME = 0;
    private static final int BATTERY = 1;

    /**
     * 确定视频是横屏播放还是竖屏播放，true为横屏，false为竖屏
     */
    private boolean isLandScape;

    /**
     * 是否需要切换大小屏播放，本地视屏不需要切换，默认为true 需要
     */
    private boolean isNeedChangeScape;

    /**
     * 屏幕宽度（竖屏或横屏状态下）
     */
//    private int screenWidth;

    /**
     * 屏幕高度（竖屏或横屏状态下）
     */
//    private int screenHeight;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME:
                    myMediaController.setTime(msg.obj.toString());
                    break;
                case BATTERY:
                    myMediaController.setBattery(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = PlayActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        //设置视频解码监听
        toggleHideyBar();

        Vitamio.isInitialized(getApplicationContext());

        setContentView(R.layout.activity_play);

        ButterKnife.bind(this);

        //得到播放地址
        Uri uri = getIntent().getData();

        //为videoView设置视频路径
        //        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //        videoView.setVideoPath(path + "/00.mp4");
        mVideoView.setVideoURI(uri);

//        mVideoView.setVideoPath(path1);
        mMediaController = new MediaController(this);
        myMediaController = new MyMediaController(this,mVideoView,this);
        //5秒后隐藏
        mMediaController.show(5000);
        //mVideoView.setMediaController(mMediaController);
        mVideoView.setMediaController(myMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
        mVideoView.requestFocus();
        isLandScape = getIntent().getBooleanExtra("isLandScape",true);
        if(isLandScape)
        {//SCREEN_ORIENTATION_LANDSCAPE
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            landSacpePlay();
        }else
        {//SCREEN_ORIENTATION_PORTRAIT
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            portraitPlay();
        }
        isNeedChangeScape = getIntent().getBooleanExtra("isNeedChangeScape",true);
        if(!isNeedChangeScape)
        {
            //iv_screenSwitch.setVisibility(View.GONE);
        }
        registerBoradcastReceiver();
        new Thread(this).start();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        screenWidth = getResources().getDisplayMetrics().widthPixels;
//        screenHeight = getResources().getDisplayMetrics().heightPixels;
        if(isNeedChangeScape)
        {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                landSacpePlay();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                portraitPlay();
            }
        }else
        {
            //不需要进行横竖屏切换
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 横屏播放
     */
    private void landSacpePlay()
    {
//        setSystemUiHide();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        setVideoViewScale(screenWidth,screenHeight);
//        setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        iv_screenSwitch.setImageResource(R.drawable.exit_full_screen);
        //        ll_volumeControl.setVisibility(View.VISIBLE);
    }

    /**
     * 竖屏播放
     */
    private void portraitPlay()
    {
        //        setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(PlayActivity.this, 240f));
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        setVideoViewScale(screenWidth,screenHeight);
//        setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        iv_screenSwitch.setImageResource(R.drawable.full_screen);
//        setSystemUiVisible();
    }

    /**
     * 设置布局大小
     *
     * @param width  宽度
     * @param height 高度
     */
    private void setVideoViewScale(int width, int height) {
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mVideoView.setLayoutParams(layoutParams);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        //当屏幕切换时 设置全屏
//        if (mVideoView != null){
//            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//        }
//        super.onConfigurationChanged(newConfig);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(batteryBroadcastReceiver);
        } catch (IllegalArgumentException ex) {

        }
    }

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);
                //把它转成百分比
                //tv.setText("电池电量为"+((level*100)/scale)+"%");
                Message msg = new Message();
                msg.obj = (level*100)/scale+"";
                msg.what = BATTERY;
                mHandler.sendMessage(msg);
            }
        }
    };

    public void registerBoradcastReceiver() {
        //注册电量广播监听
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, intentFilter);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            //时间读取线程
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String str = sdf.format(new Date());
            Message msg = new Message();
            msg.obj = str;
            msg.what = TIME;
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 隐藏虚拟按键
     */
    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (get_current_ui_flags)
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

}