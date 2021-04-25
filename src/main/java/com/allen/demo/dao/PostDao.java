package com.allen.demo.dao;

import com.allen.demo.model.Photo;
import com.allen.demo.model.Post;
import com.allen.demo.model.PostWithBLOBs;
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
        "postedBy, timeCreated, ",
        "photo, like)",
        "values (#{postid,jdbcType=INTEGER}, #{text,jdbcType=VARCHAR}, ",
        "#{postedby,jdbcType=INTEGER}, #{timecreated,jdbcType=TIMESTAMP}, ",
        "#{jsonPhotos,jdbcType=LONGVARCHAR}, #{jsonLikes,jdbcType=LONGVARCHAR})"
    })
    int insert(PostWithBLOBs record);

    @InsertProvider(type=PostSqlProvider.class, method="insertSelective")
    int insertSelective(Post record);

    @Select({
        "select",
        "postid, text, postedBy, timeCreated, photo, like",
        "from post",
        "where postid = #{postid,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
        @Result(column="postedBy", property="postedby", jdbcType=JdbcType.INTEGER),
        @Result(column="timeCreated", property="timecreated", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="photo", property="jsonPhotos", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="like", property="jsonLikes", jdbcType=JdbcType.LONGVARCHAR)
    })
    PostWithBLOBs selectByPrimaryKey(Integer postid);

    @UpdateProvider(type=PostSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(PostWithBLOBs record);

    @Update({
        "update post",
        "set text = #{text,jdbcType=VARCHAR},",
          "postedBy = #{postedby,jdbcType=INTEGER},",
          "timeCreated = #{timecreated,jdbcType=TIMESTAMP},",
          "photo = #{jsonPhotos,jdbcType=LONGVARCHAR},",
          "like = #{jsonPhotos,jdbcType=LONGVARCHAR}",
        "where postid = #{postid,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(PostWithBLOBs record);

    @Update({
        "update post",
        "set text = #{text,jdbcType=VARCHAR},",
          "postedBy = #{postedby,jdbcType=INTEGER},",
          "timeCreated = #{timecreated,jdbcType=TIMESTAMP}",
        "where postid = #{postid,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Post record);

    @Update({
            "update post",
            "set text = #{text,jdbcType=VARCHAR},",
            "postedBy = #{postedby,jdbcType=INTEGER},",
            "timeCreated = #{timecreated,jdbcType=TIMESTAMP}",
            "where postid = #{postid,jdbcType=INTEGER}"
    })
    @Select({
            "select max(postid)",
            "from post"
    })
    int selectMaxPostId();

    @Select({
            "select postid, text, postedBy, timeCreated, photo, like",
            "from post"
    })
    @Results({
            @Result(column="postid", property="postid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="text", property="text", jdbcType=JdbcType.VARCHAR),
            @Result(column="postedBy", property="postedby", jdbcType=JdbcType.INTEGER),
            @Result(column="timeCreated", property="timecreated", jdbcType=JdbcType.TIMESTAMP),
            @Result(column="photo", property="jsonPhotos", jdbcType=JdbcType.LONGVARCHAR),
            @Result(column="like", property="jsonLikes", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PostWithBLOBs> selectAllPosts();


//    @Insert({
//            "insert into post_photo (postid, url)",
//            "values",
//            "<foreach collection=\"items\" index=\"index\" item=\"item\" separator=\",\"> ",
//            "(#{item.postid},#{item.url})",
//            "</foreach> ",
//    })
//    int insertAll(List<Photo> photos);


}