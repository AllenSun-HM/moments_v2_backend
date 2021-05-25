package com.allen.moments.v2.dao;

import com.allen.moments.v2.model.Post;
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
        "delete from post",
        "where postid = #{postid,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer postid);

    @Insert({
        "insert into post (postid, text, ",
        "posted_by, time_created, ",
        "like_count, photo)",
        "values (#{postid,jdbcType=INTEGER}, #{text,jdbcType=VARCHAR}, ",
        "#{postedBy,jdbcType=INTEGER}, #{timeCreated,jdbcType=TIMESTAMP}, ",
        "#{likeCount,jdbcType=INTEGER}, #{jsonPhotoUrls,jdbcType=LONGVARCHAR})"
    })
    int insert(Post record);

    @InsertProvider(type=PostSqlProvider.class, method="insertSelective")
    int insertSelective(Post record);

    @Select({
        "select",
        "postid, text, posted_by, time_created, like_count, photo",
        "from post",
        "where postid = #{postid,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
        @Result(column="posted_by", property="postedBy", jdbcType=JdbcType.INTEGER),
        @Result(column="time_created", property="timeCreated", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="like_count", property="likeCount", jdbcType=JdbcType.INTEGER),
        @Result(column="photo", property="jsonPhotoUrls", jdbcType=JdbcType.LONGVARCHAR)
    })
    Post selectByPrimaryKey(Integer postid);

    @UpdateProvider(type=PostSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Post record);

//    @Update({
//        "update post",
//        "set text = #{text,jdbcType=VARCHAR},",
//          "posted_by = #{postedBy,jdbcType=INTEGER},",
//          "time_created = #{timeCreated,jdbcType=TIMESTAMP},",
//          "like_count = #{likeCount,jdbcType=INTEGER},",
//          "photo = #{jsonPhotoUrls,jdbcType=LONGVARCHAR}",
//        "where postid = #{postid,jdbcType=INTEGER}"
//    })
//    int updateByPrimaryKeyWithBLOBs(Post record);
//
//    @Update({
//        "update post",
//        "set text = #{text,jdbcType=VARCHAR},",
//          "posted_by = #{postedBy,jdbcType=INTEGER},",
//          "time_created = #{timeCreated,jdbcType=TIMESTAMP},",
//          "like_count = #{likeCount,jdbcType=INTEGER}",
//        "where postid = #{postid,jdbcType=INTEGER}"
//    })
//    int updateByPrimaryKey(Post record);


    @Select({
            "SELECT max(postid)",
            "FROM post"
    })
    int selectMaxPostId();

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

    @Select({
            "SELECT post.postid, post.text, post.posted_by, post.time_created, post.photo, post.like_count",
            "FROM MomentsDB.post",
            "ON post.postid = post_likes.postid",
            "GROUP BY post.postid"
    })
    @Results({
            @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
            @Result(column="posted_by", property="postedBy", jdbcType=JdbcType.INTEGER),
            @Result(column="time_created", property="timeCreated", jdbcType=JdbcType.TIMESTAMP),
            @Result(column="photo", property="jsonPhotoUrls", jdbcType=JdbcType.LONGVARCHAR),
            @Result(column="like_count", property="likeCount", jdbcType=JdbcType.INTEGER)
    })
    List<Post> selectAllPosts();
}

