package com.example.czy.myapplication.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czy.myapplication.R;
import com.example.czy.myapplication.fragment.DownloadFragment;
import com.example.czy.myapplication.fragment.HomeFragment;
import com.example.czy.myapplication.fragment.LocalFragment;
import com.example.czy.myapplication.fragment.MoreFragment;
import com.example.czy.myapplication.util.UIUtils;
import com.example.czy.myapplication.view.MyPopupWindow;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.tencent.android.tpush.service.XGPushServiceV3;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.Vitamio;

public class MainActivity extends AppCompatActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationview;

    @Bind(R.id.ll_home)
    LinearLayout ll_home;
    @Bind(R.id.iv_home)
    ImageView iv_home;
    @Bind(R.id.tv_home)
    TextView tv_home;

    @Bind(R.id.ll_local)
    LinearLayout ll_local;
    @Bind(R.id.iv_local)
    ImageView iv_local;
    @Bind(R.id.tv_local)
    TextView tv_local;

    @Bind(R.id.ll_download)
    LinearLayout ll_download;
    @Bind(R.id.iv_download)
    ImageView iv_download;
    @Bind(R.id.tv_download)
    TextView tv_download;

    @Bind(R.id.ll_more)
    LinearLayout ll_more;
    @Bind(R.id.iv_more)
    ImageView iv_more;
    @Bind(R.id.tv__more)
    TextView tv__more;

    private HomeFragment homeFragment;
    private LocalFragment localFragment;
    private DownloadFragment downloadFragment;
    private MoreFragment moreFragment;
    private FragmentTransaction ft;

    MyPopupWindow menuWindow; //弹出框

    /**
     * 完成信鸽服务的启动与APP注册过程
     */
    private void initXg()
    {
        // 开启logcat输出，方便debug，发布时请关闭
        // XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        Context context = getApplicationContext();
//        XGPushManager.registerPush(context);
        XGPushManager.registerPush(context, new XGIOperateCallback()
        {
            @Override
            public void onSuccess(Object data, int flag)
            {
                Log.w(Constants.LogTag,
                        "+++ register push sucess. token:" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg)
            {
                Log.w(Constants.LogTag,
                        "+++ register push fail. token:" + data
                                + ", errCode:" + errCode + ",msg:"
                                + msg);
            }
        });

        // 2.36（不包括）之前的版本需要调用以下2行代码
        Intent service = new Intent(context, XGPushServiceV3.class);
        context.startService(service);


        // 其它常用的API：
        // 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account, XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
        // 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
        // 反注册（不再接收消息）：unregisterPush(context)
        // 设置标签：setTag(context, tagName)
        // 删除标签：deleteTag(context, tagName)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_main);
        initXg();
        ButterKnife.bind(this);
        initToolBar();
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                int id = item.getItemId();
                String text = "";
                if (id == R.id.nav_camara)
                {
                    // Handle the camera action
                    text = "nav_camara";
                } else if (id == R.id.nav_gallery)
                {
                    text = "nav_gallery";
                } else if (id == R.id.nav_slideshow)
                {
                    text = "nav_slideshow";
                } else if (id == R.id.nav_manage)
                {
                    text = "nav_manage";
                } else if (id == R.id.nav_share)
                {
                    text = "nav_share";
                } else if (id == R.id.nav_send)
                {
                    text = "nav_send";
                }
                Toast.makeText(MainActivity.this,text+" was click！",Toast.LENGTH_SHORT).show();
                drawerlayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });

//        其实，NavigationView是一个RecyclerView（在23.1.0版本之前是ListView），header布局通常是0号元素。
        //在23.1.0版本中，就需要通过这种方法
        View headerLayout = null;
//                navigationview.inflateHeaderView(R.layout.header_layout);
        if(headerLayout==null)
        {
            //在Support Library v23.1.1版本中，可以使用如下方法很方便地获取到header中的view：
            headerLayout = navigationview.getHeaderView(0); // 0-index header
        }
        //头像
        ImageView head_icon_iv = (ImageView)headerLayout.findViewById(R.id.head_icon_iv);
        head_icon_iv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this,"点击了头像！",Toast.LENGTH_SHORT).show();
                drawerlayout.closeDrawer(Gravity.LEFT);
            }
        });
//        VideoView videoView = (VideoView) findViewById(R.id.videoView);
//        MediaController mediaController = new MediaController(this);
//        videoView.setMediaController(mediaController);
//        mediaController.setMediaPlayer(videoView);
//        //为videoView设置视频路径
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        videoView.setVideoPath(path + "/00.mp4");
        setSelect(0);
    }

    private void initToolBar()
    {
        //设置toolbar标题
        toolbar.setTitle("VideoPlay ToolBar");
        //设置Navigation
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        //需要将setSupportActionBar(toolbar)放在setNavigationOnClickListener()之前设置才行
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("TAG", "**************************************************");
                //点击侧滑弹出的事件
                if (drawerlayout.isDrawerOpen(Gravity.LEFT))
                {
                    drawerlayout.closeDrawer(Gravity.LEFT);
                } else
                {
                    drawerlayout.openDrawer(Gravity.LEFT);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_setting, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchViewButton = (MenuItem)menu.findItem(R.id.ab_search);
        SearchView searchView = (SearchView) searchViewButton.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.toolbar_add:
                menuWindow = new MyPopupWindow(MainActivity.this, new View.OnClickListener()
                {

                    public void onClick(View v)
                    {
                        Log.e("TAG","&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        menuWindow.dismiss();
                    }
                });
                //显示窗口
                menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.toolbar_add), Gravity.TOP | Gravity.RIGHT, 5, 200);
                //设置layout在PopupWindow中显示的位置
                break;
        }
        return true;
    }

    @OnClick({R.id.ll_home,R.id.ll_local,R.id.ll_download,R.id.ll_more})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_home:
                    setSelect(0);
                break;
            case R.id.ll_local:
                    setSelect(1);
                break;
            case R.id.ll_download:
                    setSelect(2);
                break;
            case R.id.ll_more:
                    setSelect(3);
                break;
        }
    }

    private void setSelect(int i)
    {
        resetBottomButton();
        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        hideFragment();
        switch (i)
        {
            case 0:
                if(homeFragment==null)
                {
                    homeFragment = new HomeFragment();
                    ft.add(R.id.frame_main,homeFragment);
                }
                iv_home.setImageResource(R.mipmap.bid01);
                tv_home.setTextColor(UIUtils.getColor(R.color.home_back_selected));
                ft.show(homeFragment);
                break;
            case 1:
                if(localFragment==null)
                {
                    localFragment = new LocalFragment();
                    ft.add(R.id.frame_main,localFragment);
                }
                iv_local.setImageResource(R.mipmap.bid03);
                tv_local.setTextColor(UIUtils.getColor(R.color.home_back_selected));
                ft.show(localFragment);
                break;
            case 2:
                if(downloadFragment==null)
                {
                    downloadFragment = new DownloadFragment();
                    ft.add(R.id.frame_main,downloadFragment);
                }
                iv_download.setImageResource(R.mipmap.bid05);
                tv_download.setTextColor(UIUtils.getColor(R.color.home_back_selected));
                ft.show(downloadFragment);
                break;
            case 3:
                if(moreFragment==null)
                {
                    moreFragment = new MoreFragment();
                    ft.add(R.id.frame_main,moreFragment);
                }
                iv_more.setImageResource(R.mipmap.bid07);
                tv__more.setTextColor(UIUtils.getColor(R.color.home_back_selected));
                ft.show(moreFragment);
                break;
        }
        ft.commit();
    }

    private void hideFragment()
    {
        if(homeFragment!=null)
        {
            ft.hide(homeFragment);
        }
        if(localFragment!=null)
        {
            ft.hide(localFragment);
        }
        if(downloadFragment!=null)
        {
            ft.hide(downloadFragment);
        }
        if(moreFragment!=null)
        {
            ft.hide(moreFragment);
        }
    }

    private void resetBottomButton()
    {
        iv_home.setImageResource(R.mipmap.bid02);
        iv_local.setImageResource(R.mipmap.bid04);
        iv_download.setImageResource(R.mipmap.bid06);
        iv_more.setImageResource(R.mipmap.bid08);
        tv_home.setTextColor(UIUtils.getColor(R.color.home_back_unselected));
        tv_local.setTextColor(UIUtils.getColor(R.color.home_back_unselected));
        tv_download.setTextColor(UIUtils.getColor(R.color.home_back_unselected));
        tv__more.setTextColor(UIUtils.getColor(R.color.home_back_unselected));
    }

}
