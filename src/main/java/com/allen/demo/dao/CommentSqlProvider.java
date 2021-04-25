package com.allen.demo.dao;

import com.allen.demo.model.Comment;
import org.apache.ibatis.jdbc.SQL;

public class CommentSqlProvider {

    public String insertSelective(Comment record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("post_comment");
        
        if (record.getCommentId() != null) {
            sql.VALUES("comment_id", "#{commentId,jdbcType=INTEGER}");
        }
        
        if (record.getPostid() != null) {
            sql.VALUES("postid", "#{postid,jdbcType=INTEGER}");
        }
        
        if (record.getComment() != null) {
            sql.VALUES("comment", "#{comment,jdbcType=VARCHAR}");
        }
        
        if (record.getCommentedby() != null) {
            sql.VALUES("commentedBy", "#{commentedby,jdbcType=INTEGER}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(Comment record) {
        SQL sql = new SQL();
        sql.UPDATE("post_comment");
        
        if (record.getPostid() != null) {
            sql.SET("postid = #{postid,jdbcType=INTEGER}");
        }
        
        if (record.getComment() != null) {
            sql.SET("comment = #{comment,jdbcType=VARCHAR}");
        }
        
        if (record.getCommentedby() != null) {
            sql.SET("commentedBy = #{commentedby,jdbcType=INTEGER}");
        }
        
        sql.WHERE("comment_id = #{commentId,jdbcType=INTEGER}");
        
        return sql.toString();
    }
}