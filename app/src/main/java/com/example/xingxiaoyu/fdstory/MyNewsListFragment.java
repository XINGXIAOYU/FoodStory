package com.example.xingxiaoyu.fdstory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.xingxiaoyu.fdstory.entity.Comment;
import com.example.xingxiaoyu.fdstory.entity.UserInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;

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
 * Created by xingxiaoyu on 17/4/29.
 */

public class MyNewsListFragment extends Fragment {
    @Bind(R.id.news)
    SwipeMenuListView newsList;


    //列表数据
    List<Comment> mCommentList;
    //adapter
    BaseAdapter mBaseAdapter;

    public static MyNewsListFragment newInstance() {
        //获取文章ID 获取评论
        MyNewsListFragment fragment = new MyNewsListFragment();
        return fragment;
    }

    public MyNewsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycommentlist, container, false);
        ButterKnife.bind(this, view);
        initData();
        initAdapter();
        return view;
    }

    private void initData() {
        mCommentList = new ArrayList<>();//从数据库读取
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getMyCommentInfo";
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
                    int id = jsonObject.getInt("myCommentID");
                    String commenter = jsonObject.getString("myCommenter");
                    String image = jsonObject.getString("myCommenterImage");
                    String content = jsonObject.getString("myCommentContent");
                    String date = jsonObject.getString("myCommentDate");
//                    mCommentList.add(new Comment(id, commenter, image, content, date));

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

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(60));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        newsList.setMenuCreator(creator);
        newsList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        // step 2. listener item click event
        newsList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        deleteItem(position);
                        break;
                }
                return false;
            }
        });
    }

    private void deleteItem(int position) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/deleteMyCommentItem";
            path = path + "?myCommentID=" + mCommentList.get(position).getId();
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                mCommentList.remove(position);
                mBaseAdapter.notifyDataSetChanged();
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


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void initAdapter() {
        mBaseAdapter = new CommentAdapter(getActivity(), mCommentList);
        newsList.setAdapter(mBaseAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}