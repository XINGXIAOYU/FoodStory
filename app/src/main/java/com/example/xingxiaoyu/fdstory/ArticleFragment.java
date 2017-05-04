package com.example.xingxiaoyu.fdstory;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class ArticleFragment extends Fragment {
    @Bind(R.id.article_image)
    ImageView articleImage;
    @Bind(R.id.author)
    TextView author;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.article_title2)
    TextView articleTitle;
    @Bind(R.id.article_content)
    TextView articleContent;
    @Bind(R.id.like_num)
    TextView likeNumView;
    @Bind(R.id.comment_num)
    TextView commentNumView;
    int likeNum;
    int commentNum;

    public static ArticleFragment newInstance(int id, String image, String title, String author, String date, String content, int like, int save) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt("article_id", id);
        args.putString("article_image", image);
        args.putString("article_title", title);
        args.putString("article_author", author);
        args.putString("article_date", date);
        args.putString("article_content", content);
        args.putInt("article_like", like);
        args.putInt("article_save", save);
        fragment.setArguments(args);
        return fragment;
    }


    public ArticleFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        ButterKnife.bind(this, view);
        articleImage.setImageURI(Uri.parse(getArguments().getString("article_image")));
        articleTitle.setText(getArguments().getString("article_title"));
        author.setText(getArguments().getString("article_author"));
        date.setText(getArguments().getString("article_date"));
        articleContent.setText(getArguments().getString("article_content"));
        likeNum = getArguments().getInt("article_like");
        likeNumView.setText(likeNum + "");
        commentNum = getArguments().getInt("article_save");
        commentNumView.setText(commentNum + "");
        EventBus.getDefault().register(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }


    public void setText(String message) {
        likeNumView.setText(message);
    }

    public void setText2(String message) {
        commentNumView.setText(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MyEvent event) {
        int num = event.getNum();
        switch (event.type) {
            case 1:
                setText(num + "");
                break;
            case 2:
                setText2(num + "");
                break;
        }


    }
}
