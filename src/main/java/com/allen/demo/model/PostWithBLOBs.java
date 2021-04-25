package com.allen.demo.model;

import org.json.simple.JSONArray;

import java.util.Date;
import java.util.List;

public class PostWithBLOBs extends Post {
    private List<String> photos;
    private String jsonPhotos;
    private List<Integer> likes;
    private String jsonLikes;

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getJsonPhotos() {
        return jsonPhotos;
    }

    public void setJsonPhotos(String jsonPhotos) {
        this.jsonPhotos = jsonPhotos;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

    public String getJsonLikes() {
        return jsonLikes;
    }

    public void setJsonLikes(String jsonLikes) {
        this.jsonLikes = jsonLikes;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public PostWithBLOBs(String text, Integer postedby, Date timecreated, List<String> photos) {
        super(text, postedby, timecreated);
        String jsonedPhotos = JSONArray.toJSONString(photos);
        this.photos = photos;
        this.jsonPhotos = jsonedPhotos;
    }

    public void setPhoto(String photo) {
        this.photos = photos == null ? null : photos;
        this.jsonPhotos = JSONArray.toJSONString(photos);
    }

    public List<Integer> getLike() {
        return likes;
    }

    public void setLike(List<Integer> likes) {
        this.likes = likes;
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
        PostWithBLOBs other = (PostWithBLOBs) that;
        return (this.getPostid() == null ? other.getPostid() == null : this.getPostid().equals(other.getPostid()))
            && (this.getText() == null ? other.getText() == null : this.getText().equals(other.getText()))
            && (this.getPostedby() == null ? other.getPostedby() == null : this.getPostedby().equals(other.getPostedby()))
            && (this.getTimecreated() == null ? other.getTimecreated() == null : this.getTimecreated().equals(other.getTimecreated()))
            && (this.getPhotos() == null ? other.getPhotos() == null : this.getPhotos().equals(other.getPhotos()))
            && (this.getLike() == null ? other.getLike() == null : this.getLike().equals(other.getLike()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPostid() == null) ? 0 : getPostid().hashCode());
        result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
        result = prime * result + ((getPostedby() == null) ? 0 : getPostedby().hashCode());
        result = prime * result + ((getTimecreated() == null) ? 0 : getTimecreated().hashCode());
        result = prime * result + ((getPhotos() == null) ? 0 : getPhotos().hashCode());
        result = prime * result + ((getLike() == null) ? 0 : getLike().hashCode());
        return result;
    }
}