package com.allen.moments.v2.dao;

import com.allen.moments.v2.model.Post;
import org.apache.ibatis.jdbc.SQL;

public class PostSqlProvider {

    public String insertSelective(Post record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("post");
        
        if (record.getPostid() != null) {
            sql.VALUES("postid", "#{postid,jdbcType=INTEGER}");
        }
        
        if (record.getText() != null) {
            sql.VALUES("text", "#{text,jdbcType=VARCHAR}");
        }
        
        if (record.getPostedBy() != null) {
            sql.VALUES("posted_by", "#{postedBy,jdbcType=INTEGER}");
        }
        
        if (record.getTimeCreated() != null) {
            sql.VALUES("time_created", "#{timeCreated,jdbcType=TIMESTAMP}");
        }
        
        if (record.getLikeCount() != null) {
            sql.VALUES("like_count", "#{likeCount,jdbcType=INTEGER}");
        }
        
        if (record.getJsonPhotoUrls() != null) {
            sql.VALUES("photo", "#{jsonPhotoUrls,jdbcType=LONGVARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(Post record) {
        SQL sql = new SQL();
        sql.UPDATE("post");
        
        if (record.getText() != null) {
            sql.SET("text = #{text,jdbcType=VARCHAR}");
        }
        
        if (record.getPostedBy() != null) {
            sql.SET("posted_by = #{postedBy,jdbcType=INTEGER}");
        }
        
        if (record.getTimeCreated() != null) {
            sql.SET("time_created = #{timeCreated,jdbcType=TIMESTAMP}");
        }
        
        if (record.getLikeCount() != null) {
            sql.SET("like_count = #{likeCount,jdbcType=INTEGER}");
        }
        
        if (record.getJsonPhotoUrls() != null) {
            sql.SET("photo = #{jsonPhotoUrls,jdbcType=LONGVARCHAR}");
        }
        
        sql.WHERE("postid = #{postid,jdbcType=INTEGER}");
        
        return sql.toString();
    }
}