package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xingxiaoyu.fdstory.entity.Favourite;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/29.
 */

public class FavouriteAdapter extends BaseAdapter {

    ViewHolder viewHolder = null;
    private LayoutInflater mLayoutInflater;
    private List<Favourite> mFavouriteList;
    private Context mContext;

    public FavouriteAdapter(Context context, List<Favourite> mFavouriteList) {
        this.mContext = context;
        this.mFavouriteList = mFavouriteList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFavouriteList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavouriteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_fragment_favourite, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //开始设置参数
        Favourite favourite = mFavouriteList.get(position);
        viewHolder.mIamSdvImg.setImageURI(Uri.parse(favourite.getImgUrl()));
        viewHolder.articleAuthor.setText(favourite.getAuthor());
        viewHolder.articleTitle.setText(favourite.getArticleName());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iam_sdv_img)
        SimpleDraweeView mIamSdvImg;
        @Bind(R.id.article_title)
        TextView articleTitle;
        @Bind(R.id.article_author)
        TextView articleAuthor;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
