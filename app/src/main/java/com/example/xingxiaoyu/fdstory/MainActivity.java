package com.example.xingxiaoyu.fdstory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    Toolbar mToolbar;
    TextView mToolBarTextView;
    BottomNavigationBar bottomNavigationBar;
    private MapFragment mMapFragment;
    private ShareFragment mShareFragment;
    private MyselfFragment mMySelfFragment;
    private ArticleFragment nightPageFragment;
    int lastSelectedPosition = 0;
    private String TAG = MainActivity.class.getSimpleName();
    android.support.v4.app.FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
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
//        String name = getIntent().getStringExtra("email");
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        fm = getSupportFragmentManager();
        if (isDay()) {
            mMapFragment = MapFragment.newInstance();
            addFragment(mMapFragment,true,R.id.tb);
        } else {
//            nightPageFragment = ArticleFragment.newInstance("文章名字", "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg");
//            addFragment(nightPageFragment,true,R.id.tb);
        }
        mShareFragment = ShareFragment.newInstance();
        mMySelfFragment = MyselfFragment.newInstance();
        mToolBarTextView.setText("首页");

//        transaction.commit();
    }

    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fm.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    //当前时间是否比设定时间小
    private boolean isDay() {
        //TODO
        return true;
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
        //开启事务
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (isDay()&&mMapFragment == null) {
                    mMapFragment = mMapFragment.newInstance();
                    transaction.replace(R.id.tb, mMapFragment);
                } else if(isDay()) {
                    transaction.show(mMapFragment);
                    transaction.replace(R.id.tb, mMapFragment);
                }
//                }else if(!isDay()&&nightPageFragment == null){
//                    nightPageFragment = ArticleFragment.newInstance("文章名字", "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg");
//                    transaction.replace(R.id.tb, nightPageFragment);
//                }else if(!isDay()){
//                    transaction.show(nightPageFragment);
//                    transaction.replace(R.id.tb, nightPageFragment);
//                }
                mToolBarTextView.setText("首页");
                break;
            case 1:
                if (mShareFragment == null) {
                    mShareFragment = mShareFragment.newInstance();
                } else {
                    transaction.show(mShareFragment);
                }
                mToolBarTextView.setText("分享");
                transaction.replace(R.id.tb, mShareFragment);
                break;
            case 2:
                if (mMySelfFragment == null) {
                    mMySelfFragment = mMySelfFragment.newInstance();
                } else {
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