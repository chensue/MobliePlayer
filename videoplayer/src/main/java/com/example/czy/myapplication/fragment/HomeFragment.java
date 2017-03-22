package com.example.czy.myapplication.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.activity.MainActivity;
import com.example.czy.myapplication.adapter.MainGridViewAdapter;
import com.example.czy.myapplication.bean.MainMovieBean;
import com.example.czy.myapplication.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-2-16.
 */
public class HomeFragment extends Fragment
{
    private final static String TAG = HomeFragment.class.getClass().getSimpleName();
    @Bind(R.id.viewPage)
    ViewPager viewPage;
    @Bind(R.id.imgDescription)
    TextView imgDescription;
    @Bind(R.id.ll_point_group)
    LinearLayout ll_point_group;

    @Bind(R.id.gridview_movie)
    MyGridView gridview_movie;

    @Bind(R.id.gridview_mv)
    MyGridView gridview_mv;

    @Bind(R.id.scrollview)
    ScrollView scrollview;

    private ArrayList images = new ArrayList<ImageView>();

    private int[] imgesId = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4};
    private String[] imgDescriptions = {"Facebooks Audience Network", "新一代Mac Pro发布", "全国大学生珠三角论坛", "Maple 2016"};

    private int presentPostion = 0;
    private final int OPTION_TYPE_AUTO = 1, OPTION_TYPE_POINT = 2;
    private static int pointClickPosition = 0; //point点击的位置
    private boolean isDraging = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int option = msg.what;
            switch (option) {
                case OPTION_TYPE_AUTO: //option==1执行viewPage跳转到下一个
                    int currentPostion = viewPage.getCurrentItem();//获得当前的ViewPage位置
                    viewPage.setCurrentItem(++currentPostion, true);
                    handler.sendEmptyMessageDelayed(OPTION_TYPE_AUTO, 3000);//回调handler 实现自动轮播
                    break;
                case OPTION_TYPE_POINT:
                    //重新设置Adapter 这个地方不设置就会报如下错误 很诡异：
                    // The specified child already has a parent. You must call removeView() on the child's parent first
                    int currentPostion2 = viewPage.getCurrentItem();//获得当前的ViewPage位置
                    viewPage.setAdapter(new MyViewPageAdapter());
                    viewPage.setCurrentItem(currentPostion2 - currentPostion2 % images.size() + pointClickPosition, true);
                    handler.sendEmptyMessageDelayed(OPTION_TYPE_AUTO, 3000);//回调handler 实现自动轮播
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home,null);
        ButterKnife.bind(this, view);
        init();
        List<MainMovieBean> beans = new ArrayList<MainMovieBean>();
        int[] resIds = {R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,R.mipmap.banner4
        ,R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,R.mipmap.banner4};
        for (int i=0;i<8;i++)
        {
            MainMovieBean bean = new MainMovieBean();
            bean.setImageRes(resIds[i]);
            bean.setMovieName("电影名称" + (i + 1));
            bean.setDetail("【说明】..."+(i+1));
            beans.add(bean);
        }
        MainGridViewAdapter adapter = new MainGridViewAdapter(getActivity(),beans);
        gridview_movie.setAdapter(adapter);
        gridview_mv.setAdapter(adapter);
        gridview_movie.setFocusable(false);
        gridview_mv.setFocusable(false);
        gridview_movie.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getActivity(),"xxxxxx",Toast.LENGTH_SHORT).show();
            }
        });
        //让ScrollView滚动到顶部
        scrollview.scrollTo(0, 0) ;
        return view;
    }

    private void init()
    {
        for (int i = 0; i < imgesId.length; i++) {
            ImageView image = new ImageView(getActivity());
            image.setBackgroundResource(imgesId[i]);
            images.add(image);

            ImageView point = new ImageView(getActivity());
            point.setBackgroundResource(R.drawable.point_selecte);
            //为Point设置布局参数 应为point的父节点是LinerLayout，所以需要使用LinearLayout.LayoutParams
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.bottomMargin = 20;
            if (i == 0)
                point.setEnabled(false);
            else {
                point.setEnabled(true);
                params.leftMargin = 20;
            }
            point.setLayoutParams(params);
            ll_point_group.addView(point);
        }
        viewPage.setAdapter(new MyViewPageAdapter());
        //初始化显示imgDescription
        imgDescription.setText(imgDescriptions[presentPostion]);
        //设置初始启动imageView的位置为1000的中间，避免初始为0时不能向左滑动
        viewPage.setCurrentItem(1000 / 2 - 1000 / 2 % images.size(), true);
        //设置ViewPage页面切换的监听事件
        viewPage.addOnPageChangeListener(new MyPageChangeListener());
        //延迟发送handler消息 用于启动ViewPage自动轮播
        handler.sendEmptyMessageDelayed(OPTION_TYPE_AUTO, 2000);

        for (int i = 0; i < ll_point_group.getChildCount(); i++) {
            final View point = ll_point_group.getChildAt(i);
            final int finalI = i;
            point.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pointClickPosition = finalI;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(OPTION_TYPE_POINT, 50);
                    for (int j = 0; j < ll_point_group.getChildCount(); j++) {
                        ll_point_group.getChildAt(j).setEnabled(true);
                    }
                    point.setEnabled(false);
                }
            });
        }
    }

    /**
     * viewPage切换监听
     */
    class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * 当页滑动的时候 回调该方法
         *
         * @param position             当前滑动页面的位置
         * @param positionOffset       当前页面滑动的百分比
         * @param positionOffsetPixels 当前页面滑动的像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 页面被选中后已完全显示时调用改方法
         *
         * @param position 被选中的页面的位置
         */
        @Override
        public void onPageSelected(int position) {
            imgDescription.setText(imgDescriptions[position % imgDescriptions.length]);
            ll_point_group.getChildAt(presentPostion % images.size()).setEnabled(true);
            ll_point_group.getChildAt(position % imgDescriptions.length).setEnabled(false);
            presentPostion = position;
        }

        /**
         * 页面滑动的状态
         * 静止-滑动
         * 滑动-静止
         * 静止-拖拽
         *
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == viewPage.SCROLL_STATE_DRAGGING) { //拖拽状态
                isDraging = true;
                //如果处于拖拽状态 就移除handler 避免拖拽过程中自动轮播、
                handler.removeCallbacksAndMessages(null);
            } else if (state == viewPage.SCROLL_STATE_SETTLING) {//滑动状态

            } else if (state == viewPage.SCROLL_STATE_IDLE) {//休闲状态
                isDraging = false;
                //拖拽结束后调用改方法 先移除handler 然后重新发送handler 启动自动 轮播
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(OPTION_TYPE_AUTO, 3000);
            }

        }
    }

    /**
     * pageAdaapter适配器
     */
    class MyViewPageAdapter extends PagerAdapter
    {

        /**
         * 返回ViewPage总数
         *
         * @return
         */
        @Override
        public int getCount() {
            return 1000;
        }

        /**
         * 返回ViewPage中的position位置处的ImageView
         *
         * @param container 代表ViewPage
         * @param position  位置
         * @return 返回ViewPage
         */
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = (ImageView) images.get(position % images.size());
            container.addView(imageView);

            //监听Touch事件 长按图片时禁止viewpage滚动
            viewPage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //鼠标按下的时候移除handler
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_MOVE:

                            break;
                        case MotionEvent.ACTION_UP:
                            //鼠标抬起的时候移除handler 并且重新发送handler
                            handler.removeCallbacksAndMessages(null);
                            handler.sendEmptyMessageDelayed(OPTION_TYPE_AUTO, 3000);
                            break;
                    }
                    return false; //返回false 表示不消费触摸操作 任然可以触发其他操作
                }
            });

            //为当前imageView设置点击监听
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), imgDescriptions[position % imgDescriptions.length], Toast.LENGTH_SHORT).show();
                }
            });

            return imageView;
        }

        /**
         * 工系统调用 判断instantiateItem方法返回的View是否和object相同
         *
         * @param view   instantiateItem方法返回的ImageView
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 供系统调用 用于销毁ViewPage中的object
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
