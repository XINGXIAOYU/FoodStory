package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private PictureUploadTask pictureUploadTask;
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
    @Bind(R.id.article_class)
    TextView foodNameView;
    String title;
    String content;
    String image;
    String location;
    String foodName;
    Uri uri;

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果


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
                location = locationtext.getText().toString();
                foodName = foodNameView.getText().toString();
                latlngTask = new GetLatlngTask();
                latlngTask.execute((Void) null);
                pictureUploadTask = new PictureUploadTask();
                pictureUploadTask.execute((Void) null);
                finish();
                break;
        }

    }


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
                    camera();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    gallery();
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    /*
       * 从相册获取
       */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
   * 从相机获取
   */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            String path = Environment.getExternalStorageDirectory().toString();
            tempFile = new File(path + File.separator + PHOTO_FILE_NAME);
            if (!tempFile.exists()) {
                tempFile.delete();//创建新文件
            }
//            tempFile = new File(Environment.getExternalStorageDirectory(),
//                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
       * 判断sdcard是否被挂载
       */


    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;

        } else {
            return false;

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                uri = data.getData();
                articleImage.setImageURI(uri);

            }


        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
//                crop(Uri.fromFile(tempFile));
//                System.out.println("::::::::path::::::::::  "+tempFile);
//                articleImage.setImageURI(Uri.fromFile(tempFile));
                uri = Uri.fromFile(tempFile);
                articleImage.setImageURI(uri);

            } else {
                Toast.makeText(WriteArticleActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                articleImage.setImageBitmap(bitmap);

            }
            try {
                // 将临时文件删除
                tempFile.delete();

            } catch (Exception e) {
                e.printStackTrace();

            }


        }

        super.onActivityResult(requestCode, resultCode, data);

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
                path = path + "?address=" + location + "&output=json&ak=Eid9ruoBOqD1hrnqDYW1VjpTThwYjaaW&mcode=F3:26:B4:85:F7:50:58:63:18:E1:58:D1:A9:2F:33:84:AF:13:57:41;com.example.xingxiaoyu.fdstory";
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
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/saveArticle";
            path = path + "?articleAuthor=" + UserInfo.email
                    + "&articleTitle=" + title
                    + "&articleFood=" + foodName
                    + "&articleFoodLocation=上海"
//                    + "&articleFoodLocation=" + location
                    + "&articleContent=" + content
                    + "&articleLatitude=121.607421"
                    + "&articleLongitude=31.211632";
//                    + "&articleLatitude=" + latLng.latitude
//                    + "&articleLongitude=" + latLng.longitude;
            Log.i("WriteArticleWeb", "NO1. " + UserInfo.email + " " + articleTitle);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            Log.i("WriteArticleWeb", "ResponseCoode" + conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                String responseData = ParseInput.parseInfo(is);
                //转换成json数据处理
                JSONArray jsonArray = new JSONArray(responseData);
                for (int i = 0; i < jsonArray.length(); i++) {       //一个循环代表一个对象
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int articleId = jsonObject.getInt("articleId");
                    if (articleId > 0) {
                        return pictureUpload(getRealPathFromURI(uri), articleId);
                    }
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

    public class PictureUploadTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return saveArticle();
        }
    }

    public boolean pictureUpload(String filePath, int articleId) {
        String rsp = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "|"; // request头和上传文件内容分隔符
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/saveArticle";
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            File file = new File(filePath);
            File newFile = new File(articleId + ".jpg");
            file.renameTo(newFile);
            String filename = articleId + ".jpg";
            String contentType = "image/jpg";
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"" + filePath
                    + "\"; filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            rsp = buffer.toString();
            reader.close();
            reader = null;
            if(Integer.parseInt(rsp)>0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return false;
    }

    private String getRealPathFromURI(Uri contentUri) {//得到路经
        Cursor cursor = null;
        String locationPath = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            locationPath = cursor.getString(column_index);
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return locationPath;
    }
}
