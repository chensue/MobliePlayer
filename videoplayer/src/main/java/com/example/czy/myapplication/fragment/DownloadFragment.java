package com.example.czy.myapplication.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.czy.myapplication.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-2-16.
 */
public class DownloadFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_download,null);
        ButterKnife.bind(this,view);
        return view;
    }
}
