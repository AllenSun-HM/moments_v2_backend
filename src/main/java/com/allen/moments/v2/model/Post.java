package com.allen.moments.v2.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;

public class Post {
    private Integer postid;

    private String text;

    private Integer postedBy;

    private Date timeCreated;

    private Integer likeCount;

    private List<String> photoUrls;

    private String jsonPhotoUrls;

    public Post() {}

    public Post(Integer postid, String text, Integer postedBy, List<String> photoUrls) {
        this.postid = postid;
        this.text = text;
        this.postedBy = postedBy;
        this.photoUrls = photoUrls;
        this.jsonPhotoUrls = JSONObject.toJSONString(this.photoUrls);
    }

    public Post(Integer postid, String text, Integer postedBy) {
        this.postid = postid;
        this.text = text;
        this.postedBy = postedBy;
    }

    public Integer getPostid() {
        return postid;
    }

    public void setPostid(Integer postid) {
        this.postid = postid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    public Integer getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Integer postedBy) {
        this.postedBy = postedBy;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getJsonPhotoUrls() {
        return jsonPhotoUrls;
    }

    public void setJsonPhotoUrls(String jsonPhotoUrls) {
        this.jsonPhotoUrls = null;
        this.photoUrls = JSONObject.parseArray(jsonPhotoUrls, String.class);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Post other = (Post) that;
        return (this.getPostid() == null ? other.getPostid() == null : this.getPostid().equals(other.getPostid()))
            && (this.getText() == null ? other.getText() == null : this.getText().equals(other.getText()))
            && (this.getPostedBy() == null ? other.getPostedBy() == null : this.getPostedBy().equals(other.getPostedBy()))
            && (this.getTimeCreated() == null ? other.getTimeCreated() == null : this.getTimeCreated().equals(other.getTimeCreated()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getPhotoUrls() == null ? other.getPhotoUrls() == null : this.getPhotoUrls().equals(other.getPhotoUrls()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPostid() == null) ? 0 : getPostid().hashCode());
        result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
        result = prime * result + ((getPostedBy() == null) ? 0 : getPostedBy().hashCode());
        result = prime * result + ((getTimeCreated() == null) ? 0 : getTimeCreated().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getPhotoUrls() == null) ? 0 : getPhotoUrls().hashCode());
        return result;
    }
}