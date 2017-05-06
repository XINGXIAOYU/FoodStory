package com.example.xingxiaoyu.fdstory.entity;

/**
 * Created by xingxiaoyu on 17/5/6.
 */

public class MenuItemInfo {
    String image;
    String text;

    public MenuItemInfo( String image, String text) {
        this.image = image;
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
