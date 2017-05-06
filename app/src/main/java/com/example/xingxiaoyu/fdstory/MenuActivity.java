package com.example.xingxiaoyu.fdstory;

/**
 * Created by xingxiaoyu on 17/5/6.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


/**
 * Created by xingxiaoyu on 17/4/29.
 */

public class MenuActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TextView mToolBarTextView;
    private String TAG = MenuActivity.class.getSimpleName();
    FragmentManager fm;
    private int articleID;
    private MenuFragment mMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        setDefaultFragment();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        fm = getSupportFragmentManager();
        articleID = getIntent().getIntExtra("article_id", -1);
        mMenuFragment = MenuFragment.newInstance(articleID);
        addFragment(mMenuFragment, true, R.id.container);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        mToolBarTextView.setText("菜单");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mMenuFragment != null && mMenuFragment.isAdded()) {
            fm.popBackStack();
            this.finish();
        } else {
            finish();
        }
    }

    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fm.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }
}
