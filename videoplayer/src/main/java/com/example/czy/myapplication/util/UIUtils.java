package com.example.czy.myapplication.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import com.example.czy.myapplication.MyApplication;

/**
 * Created by Administrator on 2017-2-16.
 */
public class UIUtils
{
    public static Context getContext()
    {
        return MyApplication.context;
    }

    public static int getColor(int resId)
    {
        return getContext().getResources().getColor(resId);
    }

    public static View getXmlView(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }

    /**
     * 保证runnable对象的run方法是运行在主线程当中
     *
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        if (isInMainThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    private static boolean isInMainThread() {
        //当前线程的id
        int tid = android.os.Process.myTid();
        if (tid == MyApplication.mainThreadId) {
            return true;
        }
        return false;
    }

    public static Handler getHandler() {
        return MyApplication.handler;
    }

}
