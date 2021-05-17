package com.allen.moments.v2.dao;

import com.allen.moments.v2.model.PostWithBLOBs;
import org.apache.ibatis.jdbc.SQL;

public class PostSqlProvider {

    public String insertSelective(PostWithBLOBs record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("post");
        
        if (record.getPostid() != null) {
            sql.VALUES("postid", "#{postid,jdbcType=INTEGER}");
        }
        
        if (record.getText() != null) {
            sql.VALUES("text", "#{text,jdbcType=VARCHAR}");
        }
        
        if (record.getPostedby() != null) {
            sql.VALUES("postedBy", "#{postedby,jdbcType=INTEGER}");
        }
        
        if (record.getTimecreated() != null) {
            sql.VALUES("timeCreated", "#{timecreated,jdbcType=TIMESTAMP}");
        }
        
        if (record.getJsonPhotos() != null) {
            sql.VALUES("photo", "#{photo,jdbcType=LONGVARCHAR}");
        }
        
        if (record.getJsonLikes() != null) {
            sql.VALUES("like", "#{like,jdbcType=LONGVARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(PostWithBLOBs record) {
        SQL sql = new SQL();
        sql.UPDATE("post");
        
        if (record.getText() != null) {
            sql.SET("text = #{text,jdbcType=VARCHAR}");
        }
        
        if (record.getPostedby() != null) {
            sql.SET("postedBy = #{postedby,jdbcType=INTEGER}");
        }
        
        if (record.getTimecreated() != null) {
            sql.SET("timeCreated = #{timecreated,jdbcType=TIMESTAMP}");
        }
        
        if (record.getJsonPhotos() != null) {
            sql.SET("photo = #{photo,jdbcType=LONGVARCHAR}");
        }
        
        if (record.getJsonLikes() != null) {
            sql.SET("like = #{like,jdbcType=LONGVARCHAR}");
        }
        
        sql.WHERE("postid = #{postid,jdbcType=INTEGER}");
        return sql.toString();
    }
}