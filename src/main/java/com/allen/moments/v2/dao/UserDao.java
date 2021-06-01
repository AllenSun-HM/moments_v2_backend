package com.allen.moments.v2.dao;

import com.allen.moments.v2.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface UserDao {


    @Delete({
                    "delete from user",
                    "where uid = #{uid,jdbcType=INTEGER}"
    })
    int deleteByUid(Integer uid);


    @Insert({
                    "insert into user (uid, name, ",
                    "age, sex, password, email)",
                    "values (#{uid,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
                    "#{age,jdbcType=INTEGER}, #{sex,jdbcType=INTEGER}, #{password,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR})"
    })
    int insert(User record);



    @InsertProvider(type=UserSqlProvider.class, method="insertSelective")
    int insertSelective(User record);


    @Select({
                    "select",
                    "uid, name, age, sex, email, avatar_uri, follower_count",
                    "from user",
                    "where uid = #{uid,jdbcType=INTEGER}"
    })
    @Results({
            @Result(column="uid", property="uid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
            @Result(column="follower_count", property="followerCount", jdbcType=JdbcType.INTEGER),
            @Result(column="avatar_uri", property="avatarURI", jdbcType=JdbcType.VARCHAR)
    })
    User selectByUid(Integer uid);


    @Select({
            "SELECT",
            "uid, name, age, sex, email, follower_count, avatar_uri",
            "FROM user",
            "ORDER BY follower_count DESC",
            "LIMIT #{start,jdbcType=INTEGER}, #{limit,jdbcType=INTEGER}"
    })
    @Results({
            @Result(column="uid", property="uid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
            @Result(column="follower_count", property="followerCount", jdbcType=JdbcType.INTEGER),
            @Result(column="avatar_uri", property="avatarURI", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectUsersOrderByFollowerCounts(int start, int limit);

    @UpdateProvider(type=UserSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(User record);



    @Update({
                    "update user",
                    "set name = #{name,jdbcType=VARCHAR},",
                    "age = #{age,jdbcType=INTEGER},",
                    "sex = #{sex,jdbcType=INTEGER},",
                    "password = #{password,jdbcType=VARCHAR}",
                    "where uid = #{uid,jdbcType=INTEGER}"
    })
    int updateByUid(User record);



    @Update(
            {
                    "update user",
                    "set password = #{newPassword,jdbcType=VARCHAR},",
                    "where uid = #{uid, jdbcType=INTEGER}",
            }
    )
    int updatePassword(int uid, String newPassword);



    @Select({
                    "select",
                    "uid, name, age, sex. email",
                    "from user",
    })
    @Results({
            @Result(column="uid", property="uid", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectAll();


    @Select(
            {
                    "select",
                    "max(uid)",
                    "from user"
            }
    )
    int getMaxUid();



    @Select({
                    "SELECT",
                    "uid, name, age, sex, email, follower_count, avatar_uri",
                    "FROM user",
                    "WHERE email = #{email, jdbcType=VARCHAR}"
    })
        @Results({
                @Result(column="uid", property="uid", jdbcType=JdbcType.INTEGER, id=true),
                @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
                @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
                @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
                @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
                @Result(column="follower_count", property="followerCount", jdbcType=JdbcType.INTEGER),
                @Result(column="avatar_uri", property="avatarURI", jdbcType=JdbcType.VARCHAR)

    })
    User selectByEmail(String email);

    @Insert({
                    "INSERT INTO follow_relations",
                    "(followed_id, follower_id)",
                    "VALUES (#{followedId, jdbcType=INTEGER}, #{followerId, jdbcType=INTEGER})"
    })
    int addFollower(int followedId, int followerId);

    @Select({
                    "SELECT",
                    "follower_id",
                    "FROM follow_relations",
                    "WHERE followed_id = #{uid, jdbcType=INTEGER}"
    })
    List<Integer> selectFollowersById(int uid);

    @Select({
                    "SELECT",
                    "followed_id",
                    "FROM follow_relations",
                    "WHERE follower_id = {uid, jdbcType=INTEGER}"
    })
    List<Integer> selectFollowingsById(int uid);

    @Delete({
                    "DELETE FROM follow_relations",
                    "WHERE",
                    "followed_id = #{followedId, jdbcType=INTEGER}",
                    "AND",
                    "follower_id = #{followerId, jdbcType=INTEGER}"
    })
    int removeFollowingRelation(int followedId, int followerId);

    @Update({
            "UPDATE user",
            "SET avatar_uri = #{avatarURI, jdbcType=VARCHAR}",
            "WHERE uid = #{uid, jdbcType=INTEGER}"
    })
    int addAvatarURI(int uid, String avatarURI);
}