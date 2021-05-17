package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.PostDao;
import com.allen.moments.v2.model.Post;
import com.allen.moments.v2.model.PostWithBLOBs;
import com.allen.moments.v2.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    private final PostDao postDao;
    private final RedisUtil redis;
    private static volatile Integer maxPostId;

    static {
        System.out.println("Post service initialized");
    }

    @Autowired
    public PostService(PostDao postDao, RedisUtil redis) {
        this.postDao = postDao;
        this.redis = redis;
        maxPostId = this.postDao.selectMaxPostId();
    }

    public boolean addPost(int uid, String text) {
        try {
            int postId = getPostId();
            Date timeCreated = new Date();
            Post post = new Post(text, uid, timeCreated);
            postDao.insertSelective(post);
            redis.set("post:" + String.valueOf(postId), post);
            return true;
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    /**
     * insert post with photo(s)
     * @param uid
     * @param text
     * @param photoUrls
     * @return
     */
    public boolean addPostWithPhotos(int uid, String text, List<String> photoUrls) {
        Date timeCreated = new Date();
        int postId = getPostId();
        try {
            PostWithBLOBs post = new PostWithBLOBs(text, uid, timeCreated, photoUrls);
            postDao.insertSelective(post);
            redis.set("post" + String.valueOf(postId), post);
            return true;
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    public PostWithBLOBs getPost(int postId) {
        try {
            PostWithBLOBs post = (PostWithBLOBs) redis.get("post:" + String.valueOf(postId));
            if (post != null) {
                return post;
            }
            return postDao.selectByPrimaryKey(postId);
        }
        catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public List<PostWithBLOBs> getAllPosts() {
        try {
//            List<PostWithBLOBs> allPosts = (List<PostWithBLOBs>)(Object)redis.hashGetAll("allPosts").values();
//            if (allPosts.size() != 0) {
//                return allPosts;
//            }
            return postDao.selectAllPosts();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * like a post or unlike a post
     * @param isLike
     * @param uid
     * @param postId
     * @return returns whether the operation succeeded or not
     */
    public boolean likeOrUnlike(boolean isLike, int uid, int postId) {
        if (isLike && postDao.unLikeAPost()) {
            redis.sSet("post:likes:" + String.valueOf(postId), uid);
            return true;
        }
        else if (!isLike && postDao.likeAPost()) {
            redis.setRemove("post:likes:" + String.valueOf(postId), uid);
            return true;
        }
        return false;
    }

    public boolean addOrDeleteComment(boolean isAddComment, int postId, String comment) {
        return true;
    }
    private synchronized int getPostId() {
        return ++maxPostId;
    }

}
