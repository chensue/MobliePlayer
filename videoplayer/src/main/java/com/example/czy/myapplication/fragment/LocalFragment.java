package com.example.czy.myapplication.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.activity.MainActivity;
import com.example.czy.myapplication.activity.PlayActivity;
import com.example.czy.myapplication.adapter.VideoAdapter;
import com.example.czy.myapplication.bean.VideoBenas;
import com.example.czy.myapplication.util.Utils;
import com.example.czy.myapplication.util.imageCache.ImageCacheUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2017-2-16.
 */
public class LocalFragment extends Fragment
{
    @Bind(R.id.video_listviw)
    ListView video_listview;
    @Bind(R.id.nodata_ll)
    LinearLayout nodata_ll;

    @Bind(R.id.loading_lv)
    LinearLayout loading_lv;
    @Bind(R.id.loading_iv)
    ImageView loading_iv;

    private Utils util;
    private VideoBenas beans;
    private List<VideoBenas> data = new ArrayList<VideoBenas>();
    private VideoAdapter adapter;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    loading_lv.setVisibility(View.GONE);
                    stopAnim();
                    if (data != null && data.size() != 0) {
                        nodata_ll.setVisibility(View.GONE);
                        adapter = new VideoAdapter(data, getActivity(), util);
                        video_listview.setAdapter(adapter);
                    } else {
                        nodata_ll.setVisibility(View.VISIBLE);
                    }

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_local,null);
        ButterKnife.bind(this,view);
        util = new Utils();
        getAllVideo();
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        loading_iv.setBackgroundResource(R.anim.loading_anim);
        //获取ImageView背景,此时已被编译成AnimationDrawable
        AnimationDrawable anim = (AnimationDrawable)loading_iv.getBackground();
        //开始动画
        anim.start();
    }

    private void stopAnim()
    {
        AnimationDrawable anim = (AnimationDrawable)loading_iv.getBackground();
        if (anim.isRunning()) {
            //如果正在运行,就停止
            anim.stop();
        }
    }

    private void getAllVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getActivity().getContentResolver();
//                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Uri uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                //                MediaStore.Video.Media.MINI_THUMB_MAGIC
                //                缩略图ThumbnailUtils类
                //                Bitmap android.media.ThumbnailUtils.extractThumbnail(Bitmap source, int width, int height, int options)
                //                　　创建一个指定大小居中的缩略图，如果options定义为OPTIONS_RECYCLE_INPUT，则回收资源
                //                1. static Bitmap createVideoThumbnail(String filePath, int kind)
                // 获取视频文件的缩略图，第一个参数为视频文件的位置，比如/sdcard/android123.3gp，
                // 而第二个参数可以为MINI_KIND或MICRO_KIND最终和分辨率有关
                //查找的条件
                String[] projection = {MediaStore.Video.Media.TITLE,   //标题
                        MediaStore.Video.Media.DESCRIPTION,     //描述
                        MediaStore.Video.Media.SIZE,        //大小
                        MediaStore.Video.Media.DURATION,    //时长
                        MediaStore.Video.Media.DATA,      //绝对路径
                        MediaStore.Video.Media.MINI_THUMB_MAGIC     //缩略图
                };
                Cursor cursor = resolver.query(uri, projection, null, null, null);
                while (cursor.moveToNext()) {
                    //判断视频大小>3MB的才添加进来,并且时长大于10秒。
                    if (cursor.getLong(2) > 3 * 1024 * 1024&&Integer.valueOf(cursor.getString(3))>10*1000) {
                        beans = new VideoBenas();
                        beans.setTitle(cursor.getString(0));
                        beans.setDescribe(cursor.getString(1));
                        beans.setSize(cursor.getLong(2));
                        beans.setLongtime(cursor.getString(3));
                        beans.setPath(cursor.getString(4));
                        beans.setImagepath(cursor.getString(5));
                        data.add(beans);
                    }
                }
//                handler.sendEmptyMessage(0);
                handler.sendEmptyMessageDelayed(0,1000);
            }
        }).start();


    }
    ImageCacheUtil imageCacheUtil;
    @OnItemClick(R.id.video_listviw)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        VideoBenas item = data.get(position);
        Intent intent = new Intent(getActivity(), PlayActivity.class);
        intent.setData(Uri.parse(item.getPath()));
        intent.putExtra("videotitle", item.getTitle());
        String imagePath = item.getPath();
        imageCacheUtil = new ImageCacheUtil(getActivity());
        Bitmap bitmap = imageCacheUtil.getBitmap(imagePath);
        if (bitmap==null)
        {
            new MyTask(getActivity(),bitmap).execute(new String[]{imagePath});
        }
        //默认横屏播放
        boolean isLandScape = true;
        if (bitmap!=null)
        {
            if(bitmap.getWidth()<bitmap.getHeight())
            {
                //竖屏播放
                isLandScape = false;
                Log.e("TAG","竖屏播放..................");
            }
        }
        intent.putExtra("isLandScape",isLandScape);
        intent.putExtra("isNeedChangeScape",true);
        startActivity(intent);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null) {
            imageCacheUtil.putBitmap(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return imageCacheUtil.getBitmap(key);
    }

    class MyTask extends AsyncTask<String,Void,Bitmap>
    {
        private Bitmap mbitmap;
        private Context mcontext;
        public MyTask(Context context,Bitmap bitmap)
        {
            mcontext = context;
            mbitmap = bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Video.Thumbnails.MINI_KIND);
            if(bitmap!=null)
                addBitmapToMemoryCache(params[0],bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            mbitmap = bitmap;
        }
    }

}
