package com.example.czy.myapplication.util;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * okHttp3网络请求
 * Created by Administrator on 2017-3-21.
 */
public class OkHttp3Util
{
    private final static OkHttp3Util okHttp3Util = new OkHttp3Util();
    OkHttpClient okHttpClient;

    private OkHttp3Util()
    {
        okHttpClient = new OkHttpClient();
    }

    public static OkHttp3Util getInstance()
    {
        return okHttp3Util;
    }

    public void get(String url, Callback callBack)
    {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("get",null);
        Request request = requestBuilder.build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callBack);
    }

    public void post(String url,HashMap<String,String> map,Callback callback)
    {
        RequestBody requestBody=null;
        if (map!=null&&map.size()>0)
        {
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Map.Entry<String,String> entry:map.entrySet())
            {
                formBuilder.add(entry.getKey(),entry.getValue());
            }
            requestBody = formBuilder.build();
        }
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

}
