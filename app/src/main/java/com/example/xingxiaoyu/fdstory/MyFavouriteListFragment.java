package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
    private List<Favourite> mFavouriteList = new ArrayList<>();

    //adapter
    private BaseAdapter mBaseAdapter;

    private ReadInfoTask task;
    private DeleteItemTask deleteItemTask;

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
        setSwipe();
        task = new ReadInfoTask();
        task.execute((Void) null);
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

        return view;
    }

    public void setSwipe() {
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
                        deleteItemTask = new DeleteItemTask(position);
                        deleteItemTask.execute((Void) null);
                        break;
                }
                return false;
            }
        });
    }

    //根据用户的ID获取我的收藏相关信息
    public class ReadInfoTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
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
                initAdapter();
            }
        }
    }

    public class DeleteItemTask extends AsyncTask<Void, Void, Boolean> {
        int position;

        public DeleteItemTask(int position) {
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/deleteFavouriteItem";
                path = path + "?myFavouriteID=" + mFavouriteList.get(position).getId() + "&userEmail=" + UserInfo.email;
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
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

        @Override
        protected void onPostExecute(final Boolean success) {
            deleteItemTask = null;
            if (success) {
                mFavouriteList.remove(position);
                mBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initAdapter() {
        mBaseAdapter = new FavouriteAdapter(getActivity(), mFavouriteList);
        favouritesList.setAdapter(mBaseAdapter);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
