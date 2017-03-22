package com.example.czy.myapplication.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.util.OkHttp3Util;
import com.example.czy.myapplication.util.UIUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 * Created by Administrator on 2017-3-16.
 */
public abstract class LoadingPage extends FrameLayout
{
    OkHttp3Util okHttp3Util = OkHttp3Util.getInstance();

    private static final int PAGE_LOADING_STATE = 1;

    private static final int PAGE_ERROR_STATE = 2;

    private static final int PAGE_EMPTY_STATE = 3;

    private static final int PAGE_SUCCESS_STATE = 4;

    private int PAGE_CURRENT_STATE = 1;

    private View loadingView;

    private View errorView;

    private View emptyView;

    private View successView;

    private LayoutParams lp;

    private ResultState resultState = null;

    private Context mConext;

    /**
     * 错误页面，点击重试
     */
    private LinearLayout ll_error;

    public LoadingPage(Context context) {
        this(context, null);
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mConext = context;
        init();
    }

    private void init() {
        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (loadingView == null) {
            loadingView = UIUtils.getXmlView(R.layout.page_loading);
            addView(loadingView, lp);
        }
        if (errorView == null) {
            errorView = UIUtils.getXmlView(R.layout.page_error);
            ll_error = (LinearLayout)errorView.findViewById(R.id.ll_error);
            ll_error.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //点击重试
                    //状态归位
                    if (PAGE_CURRENT_STATE != PAGE_LOADING_STATE)
                    {
                        PAGE_CURRENT_STATE = PAGE_LOADING_STATE;
                    }
                    showSafePage();
                    show();
                }
            });
            addView(errorView, lp);
        }
        if (emptyView == null) {
            emptyView = UIUtils.getXmlView(R.layout.page_empty);
            addView(emptyView, lp);
        }
        showSafePage();
    }

    private void showSafePage() {
        UIUtils.runOnUIThread(new Runnable()
        {
            @Override
            public void run()
            {
                showPage();
            }
        });
    }

    private void showPage() {
        loadingView.setVisibility(PAGE_CURRENT_STATE == PAGE_LOADING_STATE ? View.VISIBLE : View.GONE);
        errorView.setVisibility(PAGE_CURRENT_STATE == PAGE_ERROR_STATE ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(PAGE_CURRENT_STATE == PAGE_EMPTY_STATE ? View.VISIBLE : View.GONE);
        if (successView == null) {
            successView = View.inflate(mConext, LayoutId(), null);
            addView(successView, lp);
        }
        successView.setVisibility(PAGE_CURRENT_STATE == PAGE_SUCCESS_STATE ? View.VISIBLE : View.GONE);
    }

    public abstract int LayoutId();

    Callback callback = new Callback()
    {
        @Override
        public void onFailure(Call call, IOException e)
        {
            resultState = ResultState.ERROR;
            resultState.setContent("");
            loadPage();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException
        {
            String content = response.body().string();
            if (TextUtils.isEmpty(content))
            {
                resultState = ResultState.EMPTY;
                resultState.setContent("");
            } else
            {
                resultState = ResultState.SUCCESS;
                resultState.setContent(content);
            }
            loadPage();
        }
    };

    public void show() {
        //状态归位
        if (PAGE_CURRENT_STATE != PAGE_LOADING_STATE) {
            PAGE_CURRENT_STATE = PAGE_LOADING_STATE;
        }
        //处理不需要发送请求来决定界面的情况
        String url = url();
        if (TextUtils.isEmpty(url)) {
            resultState = ResultState.SUCCESS;
            resultState.setContent("");
            loadPage();
        } else {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (params()==null)
                    {
                        get(callback);
                    }else
                    {
                        post(callback);
                    }
                }
            }).start();
        }
    }

    private void get(Callback callback)
    {
        okHttp3Util.get(url(),callback);
    }

    private void post(Callback callback)
    {
        okHttp3Util.post(url(),params(),callback);
    }

    private void loadPage() {
        switch (resultState) {
            case ERROR:
                //当前状态设置为2，显示错误界面
                PAGE_CURRENT_STATE = 2;
                break;
            case EMPTY:
                //当前状态设置为3，显示空界面
                PAGE_CURRENT_STATE = 3;
                break;
            case SUCCESS:
                //当前状态设置为4，显示成功界面
                PAGE_CURRENT_STATE = 4;
                break;
        }
        showSafePage();
        if (PAGE_CURRENT_STATE == 4) {
            OnSuccess(resultState, successView);
        }
    }

    protected abstract void OnSuccess(ResultState resultState, View successView);

    protected abstract HashMap<String,String> params();

    protected abstract String url();

    public enum ResultState {

        ERROR(2), EMPTY(3), SUCCESS(4);

        private int state;

        private String content;

        ResultState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
