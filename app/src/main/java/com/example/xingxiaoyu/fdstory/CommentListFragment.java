package com.example.xingxiaoyu.fdstory;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.entity.Comment;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class CommentListFragment extends Fragment {
    @Bind(R.id.comments)
    ListView commentList;
    @Bind(R.id.message)
    EditText message;
    @Bind(R.id.liuyan)
    LinearLayout liuYan;
    @Bind(R.id.close)
    Button closeButton;
    @Bind(R.id.submit)
    Button submintButton;

    //列表数据
    List<Comment> mCommentList;
    //回复的内容
    String info = "";
    //adapter
    BaseAdapter mBaseAdapter;

    public static CommentListFragment newInstance(String param1) {
        //获取文章ID 获取评论
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
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
    }

    private void initAdapter() {
        mBaseAdapter = new CommentAdapter(getActivity(), mCommentList);
        commentList.setAdapter(mBaseAdapter);
    }


    @OnClick({R.id.message, R.id.liuyan, R.id.close, R.id.submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message:
                break;
            case R.id.liuyan:
                break;
            case R.id.close:
                comment(false);
                break;
            case R.id.submit:
                saveComment();
                break;
        }
    }

    private void saveComment() {//存储到数据库中
        if (!TextUtils.isEmpty(message.getText())) {
            info = message.getText().toString();
            updateComment();
        } else {
            Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateComment() {
        Comment comment = null;
        comment = new Comment(666 + "", "张三" + 666, "http://d.hiphotos.baidu.com/image/h%3D360/sign=856d60650933874483c5297a610fd937/55e736d12f2eb938e81944c7d0628535e5dd6f8a.jpg", info, "2015-03-04 23:02:06");
        mCommentList.add(0, comment);
        mBaseAdapter.notifyDataSetChanged();
        //还原
        comment(false);
    }

    private void comment(boolean flag) {
        if (flag) {
            liuYan.setVisibility(View.VISIBLE);
            onFocusChange(flag);
        } else {
            liuYan.setVisibility(View.GONE);
            onFocusChange(flag);
        }


    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    message.setFocusable(true);
                    message.requestFocus();
                } else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
