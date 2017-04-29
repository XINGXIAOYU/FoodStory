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
        Comment comment = null;
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                comment = new Comment(i + "", "张三" + i, "http://d.hiphotos.baidu.com/image/h%3D360/sign=856d60650933874483c5297a610fd937/55e736d12f2eb938e81944c7d0628535e5dd6f8a.jpg", "今天真开心，敲了一天代码。", "2015-03-04 23:02:06");
            }
            if (i % 2 == 1) {
                comment = new Comment(i + "", "张三" + i, "http://g.hiphotos.baidu.com/image/h%3D360/sign=c7fd97e3bc0e7bec3cda05e71f2cb9fa/960a304e251f95ca2f34115acd177f3e6609521d.jpg", "今天真开心，敲了一天代码。", "2015-03-04 23:02:06");
            }
            mCommentList.add(comment);
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
                Comment item = mCommentList.get(position);
                switch (index) {
                    case 0:
                        // delete
//					delete(item);
                        mCommentList.remove(position);
                        mBaseAdapter.notifyDataSetChanged();
                        //更新数据库
                        break;
                }
                return false;
            }
        });
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