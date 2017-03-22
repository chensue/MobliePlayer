package com.example.czy.myapplication.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.czy.myapplication.util.UIUtils;
import com.example.czy.myapplication.view.LoadingPage;

import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-3-16.
 */
public abstract class BaseFragment extends Fragment
{
    private LoadingPage loadingPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadingPage = new LoadingPage(container.getContext()) {
            @Override
            public int LayoutId() {
                return getLayoutId();
            }

            @Override
            protected void OnSuccess(ResultState resultState, View successView) {
                ButterKnife.bind(BaseFragment.this, successView);
                initData(resultState.getContent());
            }

            @Override
            protected HashMap<String,String> params() {
                return getParams();
            }

            @Override
            protected String url() {
                return getUrl();
            }
        };
        return loadingPage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UIUtils.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                show();
            }
        }, 1000);
    }

    protected abstract HashMap<String,String> getParams();

    protected abstract String getUrl();

    protected abstract void initData(String content);

    public abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void show() {
        loadingPage.show();
    }
}
