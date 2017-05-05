package com.example.xingxiaoyu.fdstory;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.entity.ShareInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;
import com.origamilabs.library.views.StaggeredGridView;

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
 * Created by xingxiaoyu on 17/4/25.
 */

public class ShareFragment extends Fragment {
    StaggeredGridView gridView;
    @Bind(R.id.write)
    FloatingActionButton write;
    List<ShareInfo> shareInfoList = new ArrayList<>();
    ReadInfoTask task;

//    private String urls[] = {
//            "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg",
//            "http://farm5.staticflickr.com/4133/5096108108_df62764fcc_b.jpg",
//            "http://farm5.staticflickr.com/4074/4789681330_2e30dfcacb_b.jpg",
//            "http://farm9.staticflickr.com/8208/8219397252_a04e2184b2.jpg",
//            "http://farm9.staticflickr.com/8483/8218023445_02037c8fda.jpg",
//            "http://farm9.staticflickr.com/8335/8144074340_38a4c622ab.jpg",
//            "http://farm9.staticflickr.com/8060/8173387478_a117990661.jpg",
//            "http://farm9.staticflickr.com/8056/8144042175_28c3564cd3.jpg",
//            "http://farm9.staticflickr.com/8183/8088373701_c9281fc202.jpg",
//            "http://farm9.staticflickr.com/8185/8081514424_270630b7a5.jpg",
//            "http://farm9.staticflickr.com/8462/8005636463_0cb4ea6be2.jpg",
//            "http://farm9.staticflickr.com/8306/7987149886_6535bf7055.jpg",
//            "http://farm9.staticflickr.com/8444/7947923460_18ffdce3a5.jpg",
//            "http://farm9.staticflickr.com/8182/7941954368_3c88ba4a28.jpg",
//            "http://farm9.staticflickr.com/8304/7832284992_244762c43d.jpg",
//            "http://farm9.staticflickr.com/8163/7709112696_3c7149a90a.jpg",
//            "http://farm8.staticflickr.com/7127/7675112872_e92b1dbe35.jpg",
//            "http://farm8.staticflickr.com/7111/7429651528_a23ebb0b8c.jpg",
//            "http://farm6.staticflickr.com/5336/7384863678_5ef87814fe.jpg",
//            "http://farm8.staticflickr.com/7102/7179457127_36e1cbaab7.jpg",
//            "http://farm8.staticflickr.com/7086/7238812536_1334d78c05.jpg",
//            "http://farm8.staticflickr.com/7243/7193236466_33a37765a4.jpg",
//            "http://farm8.staticflickr.com/7251/7059629417_e0e96a4c46.jpg",
//            "http://farm8.staticflickr.com/7084/6885444694_6272874cfc.jpg"
//    };


    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public ShareFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ButterKnife.bind(this, view);
        task = new ReadInfoTask();
        task.execute((Void) null);
        gridView = (StaggeredGridView) view.findViewById(R.id.staggeredGridView_sharelist);
        int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        gridView.setItemMargin(margin); // set the GridView margin
        gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), WriteArticleActivity.class);
                startActivity(i);
            }
        });
        return view;
    }

    public class ReadInfoTask extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection conn = null;
        InputStream is = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/getShareListInfo";
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    String responseData = ParseInput.parseInfo(is);
                    //转换成json数据处理
                    JSONArray jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {       //一个循环代表一个对象
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("shareID");
                        String image = jsonObject.getString("shareImage");
                        shareInfoList.add(new ShareInfo(id, image));

                    }
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
            task = null;
            if (success) {
                String[] urls = new String[shareInfoList.size()];
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = shareInfoList.get(i).getImage();
                }
                StaggeredAdapter adapter = new StaggeredAdapter(getActivity(), R.id.imageView1, urls);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
                    @Override
                    public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                        int post = position;//位置
                        Toast.makeText(getActivity(), "点击了第" + post + "个", Toast.LENGTH_SHORT).show();
                        //显示这片文章的详细内容
                        Intent i = new Intent(getActivity(), ArticleActivity.class);
                        i.putExtra("article_id", shareInfoList.get(post).getId());
                        startActivity(i);
                    }
                });
                adapter.notifyDataSetChanged();//update
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
