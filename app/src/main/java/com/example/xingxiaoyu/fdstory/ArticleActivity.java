package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class ArticleActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {
    Toolbar mToolbar;
    private String TAG = ArticleActivity.class.getSimpleName();
    private ContextMenuDialogFragment mMenuDialogFragment;
    private ArticleFragment mArticleFragment;
    private int article_id;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        initMenuFragment();
        setDefaultFragment();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        fm = getSupportFragmentManager();
        getArticleInfo();
        addFragment(mArticleFragment, true, R.id.container);
    }

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

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.getBackground().setAlpha(180);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
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
                int num = mArticleFragment.getArguments().getInt("article_like");//从数据库获得点赞数
                num++;
                saveLikeNum(num);
                break;
            case 3:
                //收藏
                int num2 = mArticleFragment.getArguments().getInt("article_save");//从数据库获得点赞数
                num2++;
                saveSaveNum(num2);
                break;
        }
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    //根据文章的ID获取相关信息
    private void getArticleInfo() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getArticleInfo";
            path = path + "?articleID=" + getIntent().getStringExtra("article_id");
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
                    article_id = jsonObject.getInt("articleID");
                    String image = jsonObject.getString("articleImage");
                    String title = jsonObject.getString("articleTitle");
                    String author = jsonObject.getString("articleAuthor");
                    String date = jsonObject.getString("articleDate");
                    String content = jsonObject.getString("articleContent");
                    int like = jsonObject.getInt("likeNumber");
                    int save = jsonObject.getInt("saveNumber");
                    mArticleFragment = mArticleFragment.newInstance(article_id, image, title, author, date, content, like, save);
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
    }

    //保存点赞数
    private void saveLikeNum(int num){
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/addLike";
            path = path + "?articleID=" + getIntent().getStringExtra("article_id")+"&likeNum"+num;
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                EventBus.getDefault().post(new MyEvent(num, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    //保存收藏数
    private void saveSaveNum(int num){
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/addSave";
            path = path + "?articleID=" + getIntent().getStringExtra("article_id")+"?saveNum"+num;
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                EventBus.getDefault().post(new MyEvent(num, 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
