package com.allen.moments.v2.dao;

import com.allen.moments.v2.model.Post;
import com.allen.moments.v2.model.PostWithBLOBs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDao {
    @Delete({
        "DELETE FROM post",
        "WHERE postid = #{postid,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer postid);

    @Insert({
        "INSERT INTO post (postid, text, ",
        "posted_by, time_created, ",
        "photo, like_count)",
        "VALUES (#{postid,jdbcType=INTEGER}, #{text,jdbcType=VARCHAR}, ",
        "#{postedby,jdbcType=INTEGER}, #{timecreated,jdbcType=TIMESTAMP}, ",
        "#{jsonPhotos,jdbcType=LONGVARCHAR}, #{jsonLikes,jdbcType=INTEGER})"
    })
    int insert(PostWithBLOBs record);

    @InsertProvider(type=PostSqlProvider.class, method="insertSelective")
    int insertSelective(Post record);

    @Select({
        "SELECT",
        "postid, text, posted_by, time_created, photo, like_count",
        "FROM post",
        "WHERE postid = #{postid,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
        @Result(column="posted_by", property="postedby", jdbcType=JdbcType.INTEGER),
        @Result(column="time_created", property="timecreated", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="photo", property="jsonPhotos", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="like_count", property="jsonLikes", jdbcType=JdbcType.INTEGER)
    })
    PostWithBLOBs selectByPrimaryKey(Integer postid);

    @UpdateProvider(type=PostSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(PostWithBLOBs record);

    @Update({
        "UPDATE post",
        "SET text = #{text,jdbcType=VARCHAR},",
          "posted_by = #{postedby,jdbcType=INTEGER},",
          "time_created = #{timecreated,jdbcType=TIMESTAMP},",
          "photo = #{jsonPhotos,jdbcType=LONGVARCHAR},",
          "like_count = #{jsonPhotos,jdbcType=LONGVARCHAR}",
        "WHERE postid = #{postid,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(PostWithBLOBs record);

    @Update({
        "UPDATE post",
        "SET text = #{text,jdbcType=VARCHAR},",
          "posted_by = #{postedby,jdbcType=INTEGER},",
          "time_created = #{timecreated,jdbcType=TIMESTAMP}",
        "WHERE postid = #{postid,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Post record);

    @Update({
            "UPDATE post",
            "SET text = #{text,jdbcType=VARCHAR},",
            "posted_by = #{postedby,jdbcType=INTEGER},",
            "time_created = #{timecreated,jdbcType=TIMESTAMP}",
            "WHERE postid = #{postid,jdbcType=INTEGER}"
    })
    @Select({
            "SELECT max(postid)",
            "FROM post"
    })
    int selectMaxPostId();

    @Select({
            "SELECT post.postid, post.text, post.posted_by, post.time_created, post.photo, post.like_count",
            "FROM MomentsDB.post",
            "ON post.postid = post_likes.postid",
            "GROUP BY post.postid"
    })
    @Results({
            @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
            @Result(column="posted_by", property="postedby", jdbcType=JdbcType.INTEGER),
            @Result(column="time_created", property="timecreated", jdbcType=JdbcType.TIMESTAMP),
            @Result(column="photo", property="jsonPhotos", jdbcType=JdbcType.LONGVARCHAR),
            @Result(column="like", property="jsonLikes", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PostWithBLOBs> selectAllPosts();

    @Insert({
            "INSERT INTO post_likes",
            "(postid, like_by)",
            "VALUES (#{postId,jdbcType=INTEGER}, #{uid,jdbcType=INTEGER})",
    })
    int insertLikeRecord(int uid, int postId);

    @Delete({
            "DELETE FROM post_likes",
            "WHERE postid = #{postId,jdbcType=INTEGER} and like_by = #{uid,jdbcType=VARCHAR}",
    })
    int removeLikeRecord(int uid, int postId);

    @Insert({
            "INSERT INTO post_comment",
            "(postid, comment, commented_by)",
            "VALUES (#{postId,jdbcType=INTEGER},#{comment,jdbcType=LONGVARCHAR}, #{commentedBy,jdbcType=VARCHAR})",
    })
    int insertCommentRecord(int postId, String comment, int commentedBy);

    @Delete({
            "DELETE FROM post_comment",
            "WHERE comment_id = #{commentId,jdbcType=INTEGER} AND commented_by = #{uid,jdbcType=INTEGER}",
    })
    int removeCommentRecord(int commentId, int uid);

//    @Insert({
//            "insert into post_photo (postid, url)",
//            "values",
//            "<foreach collection=\"items\" index=\"index\" item=\"item\" separator=\",\"> ",
//            "(#{item.postid},#{item.url})",
//            "</foreach> ",
//    })
//    int insertAll(List<Photo> photos);


}