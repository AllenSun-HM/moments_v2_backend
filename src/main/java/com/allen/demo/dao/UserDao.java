package com.allen.demo.dao;

import com.allen.demo.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface UserDao {


    @Delete({
                    "delete from user",
                    "where uid = #{uid,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer uid);



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
                    "uid, name, age, sex, password, email",
                    "from user",
                    "where uid = #{uid,jdbcType=INTEGER}"
    })
    @Results({
            @Result(column="uid", property="uid", jdbcType=JdbcType.INTEGER, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
            @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR)
    })
    User selectByPrimaryKey(Integer uid);




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
//            "order by age desc",
//            "limit 10",
//            "offset 2"
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
                    "select",
                    "uid, name, age, sex, email, password",
                    "from user",
                    "where email = #{email, jdbcType=VARCHAR}"
    })
    @Results({
            @Result(column="uid", property="uid", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="sex", property="sex", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
            @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR)
    })
    User selectByEmail(String email);

    @Update({
            "update user",
            "set following = #{followingIds,jdbcType=LONGVARCHAR}",
            "where uid = #{uid, jdbcType=INTEGER}"
    })
    int addFollowing(int uid, List<Integer> followingIds);

    @Update({
            "update user",
            "set followed = #{followedIds,jdbcType=LONGVARCHAR}",
            "where uid = #{uid, jdbcType=INTEGER}"
    })
    int addFollower(int uid, List<Integer> followerIds);
}