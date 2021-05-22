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
            redis.set("post:" + postId, post);
            return true;
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    /**
     * insert post with photo(s)
     * @return whether the operation success or not
     */
    public boolean addPostWithPhotos(int uid, String text, List<String> photoUrls) {
        Date timeCreated = new Date();
        int postId = getPostId();
        try {
            PostWithBLOBs post = new PostWithBLOBs(text, uid, timeCreated, photoUrls);
            postDao.insertSelective(post);
            redis.set("post" + postId, post);
            return true;
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    public PostWithBLOBs getPost(int postId) {
        try {
            PostWithBLOBs post = (PostWithBLOBs) redis.get("post:" + (postId));
            if (post != null) {
                return post;
            }
            return postDao.selectByPrimaryKey(postId);
        }
        catch (Exception e) {
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
     * @return returns whether the operation succeeded or not
     */
    public boolean likeOrUnlike(boolean isLike, int uid, int postId) {
        if (isLike && postDao.insertLikeRecord(uid, postId) == 1) {
            redis.sSet("post:likes:" + (postId), uid);
            return true;
        }
        else if (!isLike && postDao.removeLikeRecord(uid, postId) == 1) {
            redis.setRemove("post:likes:" + (postId), uid);
            return true;
        }
        return false;
    }

    public boolean addComment(int commentedBy, int postId, String comment) {
        return postDao.insertCommentRecord(postId, comment, commentedBy) == 1;
    }

    public boolean deleteComment(int commentId, int uid) throws Exception {
        switch (postDao.removeCommentRecord(commentId, uid)) {
            case 1:
                return true;
            case 0:
                throw new Exception("illegal operation of comment deletion");
            default:
                return false;
        }
    }

    private synchronized int getPostId() {
        return ++maxPostId;
    }

}
