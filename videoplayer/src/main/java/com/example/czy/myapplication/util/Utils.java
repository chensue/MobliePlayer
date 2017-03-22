package com.example.czy.myapplication.util;

import android.content.Context;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 工具类
 * Created by CZY on 2017/1/31.
 */
public class Utils {

    private StringBuilder mformatbulider;
    private Formatter mformatter;

    public Utils(){
        mformatbulider=new StringBuilder();
        mformatter=new Formatter(mformatbulider, Locale.getDefault());
    }

    /***
     * 毫秒转格式
     *
     * @param timeMs
     */
    public String stringfortime(int timeMs) {
        int totalsecond = timeMs / 1000;
        int second = totalsecond % 60;
        int minutes = (totalsecond / 60) % 60;

        int hour=totalsecond/3600;

        mformatbulider.setLength(0);
        if(hour>0){
            return mformatter.format("%d:%02d:%02d",hour,minutes,second).toString();
        }else {
            return mformatter.format("%02d:%02d",minutes,second).toString();
        }
    }

    /***
     * 得到系统时间
     * @return
     */
    public String getSystemTime(){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 根据手机的分辨率从dp单位转成为px
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px单位转成为dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置时间格式
     *
     * @param tv          TextView
     * @param millisecond 毫秒数
     */
    public static void updateTimeFormat(TextView tv, int millisecond) {
        int second = millisecond / 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        tv.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", hh, mm, ss));
    }

}
