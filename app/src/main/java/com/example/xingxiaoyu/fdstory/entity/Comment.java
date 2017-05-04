package com.example.xingxiaoyu.fdstory.entity;

public class Comment {
    private int id;
    private String nickName;
    private String imgUrl;
    private String content;

    public Comment() {
    }

    public Comment(int id, String nickName, String imgUrl, String content) {
        this.id = id;
        this.nickName = nickName;
        this.imgUrl = imgUrl;
        this.content = content;
    }

    public Comment(String nickName, String imgUrl, String content) {
        this.nickName = nickName;
        this.imgUrl = imgUrl;
        this.content = content;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
