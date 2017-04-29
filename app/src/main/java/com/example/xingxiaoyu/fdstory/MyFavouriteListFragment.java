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
import com.example.xingxiaoyu.fdstory.entity.Favourite;

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
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                favourite = new Favourite(i + "", "标题" + i, "http://farm9.staticflickr.com/8335/8144074340_38a4c622ab.jpg", "作者" + i);
            }
            if (i % 2 == 1) {
                favourite = new Favourite(i + "", "标题" + i, "http://farm9.staticflickr.com/8335/8144074340_38a4c622ab.jpg", "作者" + i);
            }
            mFavouriteList.add(favourite);
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
                Favourite item = mFavouriteList.get(position);
                switch (index) {
                    case 0:
                        // delete
//					delete(item);
                        mFavouriteList.remove(position);
                        mBaseAdapter.notifyDataSetChanged();
                        //更新数据库
                        break;
                }
                return false;
            }
        });

        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int post=position;//位置
                Toast.makeText(getActivity(), "点击了第"+post+"个", Toast.LENGTH_SHORT).show();
                //显示这片文章的详细内容
                Intent i = new Intent(getActivity(), ArticleActivity.class);
                i.putExtra("article_name", "这篇文章的ID"+post);
                startActivity(i);

            }
        });
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
