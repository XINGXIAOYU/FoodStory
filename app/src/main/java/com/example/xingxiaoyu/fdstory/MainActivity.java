package com.example.xingxiaoyu.fdstory;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class  MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    Toolbar mToolbar;
    TextView mToolBarTextView;
    BottomNavigationBar bottomNavigationBar;
    private MapFragment mMapFragment;
    private ShareFragment mShareFragment;
    private MyselfFragment mMySelfFragment;
    int lastSelectedPosition = 0;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "首页").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, "分享").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.ic_aboutme_white_24dp, "我的").setActiveColorResource(R.color.orange))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mMapFragment = MapFragment.newInstance();
        mToolBarTextView.setText("首页");
        transaction.replace(R.id.tb, mMapFragment);
        transaction.commit();
    }

    private void initToolbar() {
        mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mMapFragment == null) {
                    mMapFragment = mMapFragment.newInstance();
                }else{
                    transaction.show(mMapFragment);
                }
                mToolBarTextView.setText("首页");
                transaction.replace(R.id.tb, mMapFragment);
                break;
            case 1:
                if (mShareFragment == null) {
                    mShareFragment = mShareFragment.newInstance();
                }else{
                    transaction.show(mShareFragment);
                }
                mToolBarTextView.setText("分享");
                transaction.replace(R.id.tb, mShareFragment);
                break;
            case 2:
                if (mMySelfFragment == null) {
                    mMySelfFragment = mMySelfFragment.newInstance("我的");
                }else{
                    transaction.show(mMySelfFragment);
                }
                mToolBarTextView.setText("我的");
                transaction.replace(R.id.tb, mMySelfFragment);
                break;
            default:
                break;
        }
        // 事务提交
        transaction.commit();

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}