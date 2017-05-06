package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;
import com.example.xingxiaoyu.fdstory.entity.UserInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, OnMenuItemClickListener, OnMenuItemLongClickListener {
    Toolbar mToolbar;
    TextView mToolBarTextView;
    BottomNavigationBar bottomNavigationBar;
    private MapFragment mMapFragment;
    private ShareFragment mShareFragment;
    private MyselfFragment mMySelfFragment;
    private NightPageFragment nightPageFragment;
    int lastSelectedPosition = 0;
    private String TAG = MainActivity.class.getSimpleName();
    android.support.v4.app.FragmentManager fm;
    private NightTask nightTask;
    private SaveLikeTask saveLikeTask;
    private SaveSaveTask saveSaveTask;
    private int article_id;
    private boolean isNight;
    private String image;
    private String title;
    private String author;
    private String date;
    private String content;
    private int like;
    private int save;


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
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        fm = getSupportFragmentManager();
        nightTask = new NightTask();
        nightTask.execute((Void) null);
        mShareFragment = ShareFragment.newInstance();
        mMySelfFragment = MyselfFragment.newInstance();
        mToolBarTextView.setText("首页");
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
                if (!isNight && mMapFragment == null) {
                    mMapFragment = mMapFragment.newInstance();
                    transaction.replace(R.id.tb, mMapFragment);
                } else if (!isNight) {
                    transaction.show(mMapFragment);
                    transaction.replace(R.id.tb, mMapFragment);
                } else if (isNight && nightPageFragment == null) {
                    nightPageFragment = nightPageFragment.newInstance(article_id, image, title, author, date, content, like, save);
                    transaction.replace(R.id.tb, nightPageFragment);
                } else if (isNight) {
                    transaction.show(nightPageFragment);
                    transaction.replace(R.id.tb, nightPageFragment);
                }
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

    private ContextMenuDialogFragment mMenuDialogFragment;

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);
        MenuObject comment = new MenuObject("评论");
        comment.setResource(R.drawable.icn_1);
        MenuObject like = new MenuObject("点赞");
        like.setResource(R.drawable.icn_2);
        MenuObject addFav = new MenuObject("收藏");
        addFav.setResource(R.drawable.icn_4);
        menuObjects.add(close);
        menuObjects.add(comment);
        menuObjects.add(like);
        menuObjects.add(addFav);
        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fm.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fm, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                //评论
                Intent i = new Intent(this, CommentActivity.class);
                i.putExtra("article_id", article_id);
                startActivity(i);
                break;
            case 2:
                //点赞
                int num = nightPageFragment.getArguments().getInt("article_like");//从数据库获得点赞数
                num++;
                saveLikeTask = new SaveLikeTask(num);
                saveLikeTask.execute((Void) null);
                break;
            case 3:
                //收藏
                int num2 = nightPageFragment.getArguments().getInt("article_save");//从数据库获得点赞数
                num2++;
                saveSaveTask = new SaveSaveTask((num2));
                saveSaveTask.execute((Void) null);
                break;
        }
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }

    //判断是否夜间模式
    public class NightTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/night";
                path = path + "?userEmail=" + UserInfo.email;
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    String responseData = ParseInput.parseInfo(is);
                    //转换成json数据处理
                    JSONArray jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {       //一个循环代表一个对象
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getBoolean("isNight")) {
                            isNight = true;
                            article_id = jsonObject.getInt("articleID");
                            image = WebIP.PATH + jsonObject.getString("articleImage");
                            title = jsonObject.getString("articleTitle");
                            author = jsonObject.getString("articleAuthor");
                            date = jsonObject.getString("articleDate");
                            content = jsonObject.getString("articleContent");
                            like = jsonObject.getInt("likeNumber");
                            save = jsonObject.getInt("saveNumber");
                            nightPageFragment = nightPageFragment.newInstance(article_id, image, title, author, date, content, like, save);
                            return true;
                        } else {
                            isNight = false;
                        }
                        return true;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            nightTask = null;
            if (success) {
                if (isNight == true) {
                    initMenuFragment();
                    addFragment(nightPageFragment, true, R.id.tb);
                } else {
                    mMapFragment = MapFragment.newInstance();
                    addFragment(mMapFragment, true, R.id.tb);
                }
            }
        }
    }

    //保存点赞数
    public class SaveLikeTask extends AsyncTask<Void, Void, Boolean> {
        int likeNum;

        public SaveLikeTask(int likeNum) {
            this.likeNum = likeNum;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/addLike";
                path = path + "?articleID=" + getIntent().getIntExtra("article_id", -1) + "&userEmail=" + UserInfo.email;
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    EventBus.getDefault().post(new MyEvent(likeNum, 1));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            saveLikeTask = null;
        }

    }

    //保存收藏数
    public class SaveSaveTask extends AsyncTask<Void, Void, Boolean> {
        int saveNum;

        public SaveSaveTask(int saveNum) {
            this.saveNum = saveNum;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/addSave";
                path = path + "?articleID=" + getIntent().getIntExtra("article_id", -1) + "&userEmail=" + UserInfo.email;
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    EventBus.getDefault().post(new MyEvent(saveNum, 2));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            saveSaveTask = null;
        }
    }
}