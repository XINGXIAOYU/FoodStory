package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/28.
 */

public class WriteArticleActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {
    private Toolbar mToolbar;
    private TextView mToolBarTextView;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private String TAG = WriteArticleActivity.class.getSimpleName();
    private LatLng latLng = null;
    private GetLatlngTask latlngTask;
    FragmentManager fm = getSupportFragmentManager();
    @Bind(R.id.article_title)
    TextView articleTitle;
    @Bind(R.id.article_content)
    TextView articleContent;
    @Bind(R.id.photo)
    ImageView articleImage;
    @Bind(R.id.insert_location)
    ImageView insertLocation;
    String title;
    String content;
    Drawable image;
    String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        initMenuFragment();
        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PopupWindows(WriteArticleActivity.this, articleImage);
            }
        });
        insertLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PopupWindows2(WriteArticleActivity.this, insertLocation);
            }
        });
    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        mToolBarTextView.setText("写分享");
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
        finish();
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
        MenuObject comment = new MenuObject("发送");
        comment.setResource(R.drawable.icn_5);
//        MenuObject like = new MenuObject("保存");
//        like.setResource(R.drawable.icn_3);
        menuObjects.add(close);
        menuObjects.add(comment);
//        menuObjects.add(like);
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
                title = articleTitle.getText().toString();
                content = articleContent.getText().toString();
//                image = articleImage.getDrawable();
                latlngTask = new GetLatlngTask();
                latlngTask.execute((Void) null);
//                location =
//                //发送
//                /*
//                saveArticle()
//                 */
                Toast.makeText(this, "成功发表，进入审核", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

    }

//    private void saveArticle(){
//        if(!TextUtils.isEmpty(articleTitle.getText())&&
//                !TextUtils.isEmpty(articleContent.getText())&&
//                articleImage.getResources()!=null&&
//                ){
//
//        }
//    }


    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }


    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_write_article_popwindow, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.FILL_PARENT);
            setHeight(ViewGroup.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    album();
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }


        public void photo() {
            //拍照功能
            //TODO
        }

        public void album() {
            //相册功能
//            String url = "/Users/xingxiaoyu/Documents/FDStory/app/src/main/res/drawable/wel_pic.png";
//            EventBus.getDefault().post(new PhotoEvent(url));
            articleImage.setImageResource(R.drawable.wel_pic);
            //TODO
        }
    }

    public class PopupWindows2 extends PopupWindow {

        public PopupWindows2(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_write_article_popwindow2, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.FILL_PARENT);
            setHeight(ViewGroup.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();
            final TextView tx1 = (TextView) view.findViewById(R.id.location);
            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_ok);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(tx1.getText())) {
                        location = tx1.getText().toString();
                        dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入位置", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    public class GetLatlngTask extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection conn = null;
        InputStream is = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String path = "http://api.map.baidu.com/geocoder/v2/";
                path = path + "?address=" + location + "&output=json&ak=9IocfDhkOuk5b6laSCUTHaLqKEeT7nef&mcode=5E:3B:2E:17:56:E1:00:95:54:F2:A2:A9:FE:9E:B1:74:36:85:3F:05;com.example.xingxiaoyu.fdstory";
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                Log.i("LoginWeb", "NO1. " + location);
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    String responseData = ParseInput.parseInfo(is);
                    //转换成json数据处理
                    JSONArray jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {       //一个循环代表一个对象
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        latLng = (LatLng) jsonObject.get("location");
                        Log.i("LoginWeb", "NO2. " + latLng);
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
            return null;
        }
    }
}
