package com.example.xingxiaoyu.fdstory.entity;

/**
 * Created by xingxiaoyu on 17/5/4.
 */

public class ShareInfo {
    int id;
    String image;

    public ShareInfo(int id, String image) {
        this.id = id;
        this.image = image;
    }

    public ShareInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
