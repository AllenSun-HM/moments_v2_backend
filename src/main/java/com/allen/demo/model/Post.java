package com.allen.demo.model;

import java.util.Date;

public class Post {
    private Integer postid;

    private String text;

    private Integer postedby;

    private Date timecreated;

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

    public Post(String text, Integer postedby, Date timecreated) {
        this.text = text;
        this.postedby = postedby;
        this.timecreated = timecreated;
    }

    public Integer getPostedby() {
        return postedby;
    }

    public void setPostedby(Integer postedby) {
        this.postedby = postedby;
    }

    public Date getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Date timecreated) {
        this.timecreated = timecreated;
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
            && (this.getPostedby() == null ? other.getPostedby() == null : this.getPostedby().equals(other.getPostedby()))
            && (this.getTimecreated() == null ? other.getTimecreated() == null : this.getTimecreated().equals(other.getTimecreated()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPostid() == null) ? 0 : getPostid().hashCode());
        result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
        result = prime * result + ((getPostedby() == null) ? 0 : getPostedby().hashCode());
        result = prime * result + ((getTimecreated() == null) ? 0 : getTimecreated().hashCode());
        return result;
    }
}