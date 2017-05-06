package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
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
import com.example.xingxiaoyu.fdstory.entity.UserInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
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
    SimpleDraweeView articleImage;
    @Bind(R.id.insert_location)
    ImageView insertLocation;
    @Bind(R.id.location)
    TextView locationtext;
    String title;
    String content;
    String image;
    String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
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
        menuObjects.add(close);
        menuObjects.add(comment);
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
                latlngTask = new GetLatlngTask();
                latlngTask.execute((Void) null);
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
            image = "http://farm8.staticflickr.com/7127/7675112872_e92b1dbe35.jpg";
            articleImage.setImageURI(Uri.parse(image));
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
                        locationtext.setText(location);
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

    //调用百度地图web service geocoding

    public class GetLatlngTask extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection conn = null;
        InputStream is = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String path = "http://api.map.baidu.com/geocoder/v2/";
                path = path + "?address=" + location + "&output=json&ak=Eid9ruoBOqD1hrnqDYW1VjpTThwYjaaW&mcode=FF:C4:4F:41:08:2B:6D:AA:CC:F1:F9:9E:8B:BF:C2:1C:A9:74:53:84;com.example.xingxiaoyu.fdstory";
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
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject jsonObject2 = jsonObject.getJSONObject("result");
                    JSONObject jsonObject3 = jsonObject2.getJSONObject("location");
                    double longitude = jsonObject3.getDouble("lng");
                    double latitude = jsonObject3.getDouble("lat");
                    latLng = new LatLng(latitude, longitude);
                    return saveArticle();
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
            latlngTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), "成功发表，进入审核", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean saveArticle() {
        HttpURLConnection conn = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/saveArticle";
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            String data = "articleAuthor" + UserInfo.email
                    + "&articleTitle" + title
                    + "&articleContent" + content
                    + "&articleImage" + image
                    + "ArticleLatitude" + latLng.latitude
                    + "ArticleLongitude" + latLng.longitude;
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            conn.setRequestProperty("Content-Length", data.length() + "");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            if (conn.getResponseCode() == 200) {
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
}
