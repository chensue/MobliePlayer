package com.example.czy.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.czy.myapplication.R;

/**
 * Created by Administrator on 2017-3-16.
 */
public class LoadingImageView extends ImageView
{
    public LoadingImageView(Context context)
    {
        this(context, null);
    }

    public LoadingImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(+R.anim.loading_anim);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //获取ImageView背景,此时已被编译成AnimationDrawable
        AnimationDrawable anim = (AnimationDrawable)this.getBackground();
        //开始动画
        anim.start();
    }

    public void stopAnim()
    {
        AnimationDrawable anim = (AnimationDrawable)this.getBackground();
        if (anim.isRunning()) {
            //如果正在运行,就停止
            anim.stop();
        }
    }

}
