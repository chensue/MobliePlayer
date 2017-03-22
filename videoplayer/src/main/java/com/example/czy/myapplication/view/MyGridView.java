package com.example.czy.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 在开发中用到了需要ScrollView嵌套GridView的情况，由于这两款控件都自带滚动条，
 * 当他们碰到一起的时候便会出问题，即GridView会显示不全，为了解决这个问题
 * 这个自定义控件只是重写了GridView的onMeasure方法，
 * 使其不会出现滚动条，ScrollView嵌套ListView也是同样的道理
 * Created by Administrator on 2017-2-20.
 */
public class MyGridView extends GridView
{
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyGridView(Context context) {
        super(context);
    }
    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
