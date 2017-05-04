package com.example.xingxiaoyu.fdstory.entity;

import java.io.Serializable;

/**
 * Created by xingxiaoyu on 17/5/3.
 */

public class MarkerInfo implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private String image;
    private String articleName;
    private String date;

    public MarkerInfo(int id, double latitude, double longitude, String image, String articleName, String date) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.articleName = articleName;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
