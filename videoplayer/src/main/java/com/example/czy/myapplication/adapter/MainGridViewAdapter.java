package com.example.czy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.bean.MainMovieBean;
import com.example.czy.myapplication.view.RoundAngleImageView;

import java.util.List;

/**
 * Created by Administrator on 2017-2-20.
 */
public class MainGridViewAdapter extends BaseAdapter
{
    private List<MainMovieBean> mbeans;
    private Context mcontext;
    public MainGridViewAdapter(Context context,List<MainMovieBean> beans)
    {
        mcontext = context;
        mbeans = beans;
    }

    @Override
    public int getCount()
    {
        return mbeans==null?0:mbeans.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mbeans==null?null:mbeans.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView==null)
        {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.main_gridview_layout,null);
            holder = new ViewHolder();
            holder.roundangleimageview = (RoundAngleImageView)convertView.findViewById(R.id.roundangleimageview);
            holder.tv_movie_name = (TextView)convertView.findViewById(R.id.tv_movie_name);
            holder.tv_detail = (TextView)convertView.findViewById(R.id.tv_detail);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        MainMovieBean bean = mbeans.get(position);
        holder.roundangleimageview.setImageResource(bean.getImageRes());
        holder.tv_movie_name.setText(bean.getMovieName());
        holder.tv_detail.setText(bean.getDetail());
        return convertView;
    }

    class ViewHolder
    {
        RoundAngleImageView roundangleimageview;
        TextView tv_movie_name;
        TextView tv_detail;
    }

}
