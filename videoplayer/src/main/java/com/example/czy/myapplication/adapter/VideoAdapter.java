package com.example.czy.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.util.Utils;
import com.example.czy.myapplication.bean.VideoBenas;
import com.example.czy.myapplication.util.imageCache.ImageCacheUtil;

import java.util.List;

/**
 * Created by gch on 2016/7/6.
 */
public class VideoAdapter extends BaseAdapter {
    private List<VideoBenas> data;
    private LayoutInflater inflater;
    private Utils util;
    private Context context;
//    private LruCache<String, Bitmap> mMemoryCache;
    private ImageCacheUtil imageCacheUtil;
    public VideoAdapter(List<VideoBenas> data, Context context, Utils util) {
        this.data = data;
        inflater = LayoutInflater.from(context);
        this.util = util;
        this.context=context;
        imageCacheUtil = new ImageCacheUtil(context);
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
//        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//        // 使用最大可用内存值的1/8作为缓存的大小。
//        int cacheSize = maxMemory / 8;
//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
//                return bitmap.getByteCount() / 1024;
//            }
//        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.videolist_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            holder.describe = (TextView) convertView.findViewById(R.id.video_describe);
            holder.size = (TextView) convertView.findViewById(R.id.video_size);
            holder.longtime = (TextView) convertView.findViewById(R.id.video_longtime);
            holder.videoImage = (ImageView)convertView.findViewById(R.id.video_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(data.get(position).getTitle());
        holder.describe.setText(data.get(position).getDescribe());
        //Formatter.formatFileSize(context, data.get(position).getSize()//自带视频大小转换工具类
        holder.size.setText(Formatter.formatFileSize(context, data.get(position).getSize()));
        holder.longtime.setText(util.stringfortime(Integer.valueOf(data.get(position).getLongtime())));
        String path = data.get(position).getPath();
        if (path!=null&&!"".equals(path))
        {
            Bitmap bitmap = getBitmapFromMemCache(path);
            if (bitmap==null)
            {
                new MyTask(context,holder.videoImage).execute(new String[]{path});
            }else
            {
                holder.videoImage.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }

    class MyTask extends AsyncTask<String,Void,Bitmap>
    {
        private ImageView mimageView;
        private Context mcontext;
        public MyTask(Context context,ImageView imageView)
        {
            mcontext = context;
            mimageView = imageView;
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
//            super.onPostExecute(bitmap);
            if(bitmap!=null)
                mimageView.setImageBitmap(bitmap);
        }
    }

    private class ViewHolder {
        TextView title, describe, size, longtime;
        ImageView videoImage;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null) {
//            mMemoryCache.put(key, bitmap);
            imageCacheUtil.putBitmap(key,bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return imageCacheUtil.getBitmap(key);
//        return mMemoryCache.get(key);
    }

}
