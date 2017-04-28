package com.example.xingxiaoyu.fdstory;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.xingxiaoyu.fdstory.view.ScaleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class StaggeredAdapter extends ArrayAdapter<String> {

    ViewHolder viewHolder = null;
    private LayoutInflater mLayoutInflater;

    public StaggeredAdapter(Context context, int textViewResourceId,
                            String[] objects) {
        super(context, textViewResourceId, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_fragment_share, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageURI(Uri.parse(getItem(position)));
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.imageView1)
        ScaleImageView imageView;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
