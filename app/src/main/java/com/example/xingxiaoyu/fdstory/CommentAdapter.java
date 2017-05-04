package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xingxiaoyu.fdstory.entity.Comment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class CommentAdapter extends BaseAdapter {

    ViewHolder viewHolder = null;
    private LayoutInflater mLayoutInflater;
    private List<Comment> mCommentList;
    private Context mContext;

    public CommentAdapter(Context context, List<Comment> mCommentList) {
        this.mContext = context;
        this.mCommentList = mCommentList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_fragment_comment, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //开始设置参数
        Comment comment = mCommentList.get(position);
        viewHolder.mIamTvReply.setText(comment.getNickName());
        viewHolder.mIamTvContent.setText(comment.getContent());
//        viewHolder.mIamTvTime.setText(comment.getTime());

        viewHolder.mIamSdvImg.setImageURI(Uri.parse(comment.getImgUrl()));
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iam_sdv_img)
        SimpleDraweeView mIamSdvImg;
        @Bind(R.id.iam_tv_reply)
        TextView mIamTvReply;
        @Bind(R.id.iam_tv_content)
        TextView mIamTvContent;
        @Bind(R.id.iam_tv_time)
        TextView mIamTvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
