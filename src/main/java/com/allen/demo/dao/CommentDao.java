package com.allen.demo.dao;

import com.allen.demo.model.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface CommentDao {
    @Delete({
        "delete from post_comment",
        "where comment_id = #{commentId,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer commentId);

    @Insert({
        "insert into post_comment (comment_id, postid, ",
        "comment, commentedBy)",
        "values (#{commentId,jdbcType=INTEGER}, #{postid,jdbcType=INTEGER}, ",
        "#{comment,jdbcType=VARCHAR}, #{commentedby,jdbcType=INTEGER})"
    })
    int insert(Comment record);

    @InsertProvider(type=CommentSqlProvider.class, method="insertSelective")
    int insertSelective(Comment record);

    @Select({
        "select",
        "comment_id, postid, comment, commentedBy",
        "from post_comment",
        "where comment_id = #{commentId,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="comment_id", property="commentId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER),
        @Result(column="comment", property="comment", jdbcType=JdbcType.VARCHAR),
        @Result(column="commentedBy", property="commentedby", jdbcType=JdbcType.INTEGER)
    })
    Comment selectByPrimaryKey(Integer commentId);

    @UpdateProvider(type=CommentSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Comment record);

    @Update({
        "update post_comment",
        "set postid = #{postid,jdbcType=INTEGER},",
          "comment = #{comment,jdbcType=VARCHAR},",
          "commentedBy = #{commentedby,jdbcType=INTEGER}",
        "where comment_id = #{commentId,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Comment record);
}