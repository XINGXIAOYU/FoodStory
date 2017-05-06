package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xingxiaoyu.fdstory.entity.MenuItemInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/5/6.
 */

public class MenuAdapter extends BaseAdapter {
    ViewHolder viewHolder = null;
    private LayoutInflater mLayoutInflater;
    private List<MenuItemInfo> mMenuList;
    private Context mContext;

    public MenuAdapter(Context context, List<MenuItemInfo> mMenuList) {
        this.mContext = context;
        this.mMenuList = mMenuList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMenuList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_fragment_menu, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //开始设置参数
        MenuItemInfo menuItemInfo = mMenuList.get(position);
        viewHolder.mIamSdvImg.setImageURI(Uri.parse(menuItemInfo.getImage()));
        viewHolder.menuItemContent.setText(menuItemInfo.getText());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iam_sdv_img)
        SimpleDraweeView mIamSdvImg;
        @Bind(R.id.menu_item_content)
        TextView menuItemContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
