package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.xingxiaoyu.fdstory.entity.Comment;
import com.example.xingxiaoyu.fdstory.entity.Favourite;
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

public class MyFavouriteListFragment extends Fragment {
    @Bind(R.id.favourites)
    SwipeMenuListView favouritesList;
    //列表数据
    List<Favourite> mFavouriteList;

    //adapter
    BaseAdapter mBaseAdapter;

    public static MyFavouriteListFragment newInstance() {
        MyFavouriteListFragment fragment = new MyFavouriteListFragment();
        return fragment;
    }

    public MyFavouriteListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfavouratelist, container, false);
        ButterKnife.bind(this, view);
        initData();
        initAdapter();
        return view;
    }

    private void initData() {
        mFavouriteList = new ArrayList<>();//从数据库读取
        Favourite favourite = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getMyFavouriteInfo";
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
                    int id = jsonObject.getInt("favouriteID");
                    String title = jsonObject.getString("favouriteTitle");
                    String image = jsonObject.getString("favouriteImage");
                    String author = jsonObject.getString("favouriteAuthor");
                    mFavouriteList.add(new Favourite(id, title, image, author));

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
        favouritesList.setMenuCreator(creator);
        favouritesList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        // step 2. listener item click event
        favouritesList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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

        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int post = position;//位置
                Toast.makeText(getActivity(), "点击了第" + post + "个", Toast.LENGTH_SHORT).show();
                //显示这片文章的详细内容
                Intent i = new Intent(getActivity(), ArticleActivity.class);
                i.putExtra("article_id", mFavouriteList.get(position).getId());
                startActivity(i);

            }
        });

    }

    private void deleteItem(int position) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/deleteFavouriteItem";
            path = path + "?myFavouriteID=" + mFavouriteList.get(position).getId();
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                mFavouriteList.remove(position);
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
        mBaseAdapter = new FavouriteAdapter(getActivity(), mFavouriteList);
        favouritesList.setAdapter(mBaseAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
