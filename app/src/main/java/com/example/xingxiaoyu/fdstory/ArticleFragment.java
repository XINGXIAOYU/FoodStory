package com.example.xingxiaoyu.fdstory;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xingxiaoyu.fdstory.R;

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
    //    private EventBus eventBus;
    int likeNum = 9;
    int commentNum = 9;

    public static ArticleFragment newInstance(String param1) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
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
        articleImage.setImageBitmap(getRes("test1"));
        articleTitle.setText(getArguments().getString("agrs1"));
        author.setText("Saoirse");
        date.setText("2016/10/05");
        articleContent.setText("刘天霖大笨蛋 邢晓渝小机智");
        likeNumView.setText(likeNum + "");
        commentNumView.setText(commentNum + "");
//        eventBus = new EventBus();
//        //注册事件
        EventBus.getDefault().register(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    public Bitmap getRes(String name) {
        ApplicationInfo appInfo = this.getActivity().getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
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
