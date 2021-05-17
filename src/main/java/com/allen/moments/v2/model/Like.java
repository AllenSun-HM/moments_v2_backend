package com.allen.moments.v2.model;

public class Like {
    private Integer postid;

    private Integer likeby;

    public Integer getPostid() {
        return postid;
    }

    public void setPostid(Integer postid) {
        this.postid = postid;
    }

    public Integer getLikeby() {
        return likeby;
    }

    public void setLikeby(Integer likeby) {
        this.likeby = likeby;
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
        Like other = (Like) that;
        return (this.getPostid() == null ? other.getPostid() == null : this.getPostid().equals(other.getPostid()))
            && (this.getLikeby() == null ? other.getLikeby() == null : this.getLikeby().equals(other.getLikeby()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPostid() == null) ? 0 : getPostid().hashCode());
        result = prime * result + ((getLikeby() == null) ? 0 : getLikeby().hashCode());
        return result;
    }
}