package com.example.xingxiaoyu.fdstory.entity;

/**
 * Created by xingxiaoyu on 17/4/29.
 */

public class Favourite {


    private int id;
    private String articleName;
    private String imgUrl;
    private String author;

    public Favourite() {
    }

    public Favourite(int id, String articleName, String imgUrl, String author) {
        this.id = id;
        this.articleName = articleName;
        this.imgUrl = imgUrl;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


}
