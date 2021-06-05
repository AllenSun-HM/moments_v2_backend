package com.allen.moments.v2.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int uid;
    private String name;
    private Integer age;
    private transient String password;
    private String email;
    private Integer sex;
    private List<Integer> followers;
    private List<Integer> followings;
    private Integer followerCount;
    private String avatarURI;

    public User(String name, String email, Integer uid, Integer age, Integer sex, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
    }

    public User(Integer uid, String name, Integer age, Integer sex, String email) {
        this.name = name;
        this.age = age;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
    }

    public User(Integer uid, String name, Integer age, Integer sex, String email, int followerCount) {
        this.name = name;
        this.age = age;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
        this.followerCount = followerCount;
    }

    public User(Integer uid, String name, Integer age, Integer sex, String email, int followerCount, String avatarURI) {
        this.name = name;
        this.age = age;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
        this.followerCount = followerCount;
        this.avatarURI = avatarURI;
    }

    public User(Integer uid, String name, Integer age, Integer sex, String email, int followerCount, String avatarURI, String password) {
        this.name = name;
        this.age = age;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
        this.followerCount = followerCount;
        this.avatarURI = avatarURI;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getFollowers() {
        return followers;
    }


    public List<Integer> getFollowings() {
        return followings;
    }

    public void addFollowing(Integer uidToFollow) {
        this.followings.add(uidToFollow);
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public String getAvatarURI() {
        return this.avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
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
        User other = (User) that;
        return (this.getUid() == 0 ? other.getUid() == 0 : this.getUid() == other.getUid())
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getAge() == null ? other.getAge() == null : this.getAge().equals(other.getAge()))
                && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUid() == 0) ? 0 : getUid());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getAge() == null) ? 0 : getAge().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        return result;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}