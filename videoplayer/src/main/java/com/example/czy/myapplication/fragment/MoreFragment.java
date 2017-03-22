package com.example.czy.myapplication.fragment;

import android.support.v4.app.Fragment;

import android.widget.TextView;

import com.example.czy.myapplication.R;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by Administrator on 2017-2-16.
 */
public class MoreFragment extends BaseFragment
{
    @Bind(R.id.tv_show)
    TextView tv_show;

    @Override
    protected HashMap<String,String> getParams()
    {
        return null;
    }

    @Override
    protected String getUrl()
    {
        return "https://www.baidu.com";
    }

    @Override
    protected void initData(String content)
    {
        tv_show.setText(content);
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_more;
    }
}
