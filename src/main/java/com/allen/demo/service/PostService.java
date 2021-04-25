package com.allen.demo.service;

import com.allen.demo.dao.PostDao;
import com.allen.demo.model.Post;
import com.allen.demo.model.PostWithBLOBs;
import com.allen.demo.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class PostService {
    private final PostDao postDao;
    private final RedisUtil redis;
    private static int maxPostId;

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
            Date timeCreated = new Date();
            Post post = new Post(text, uid, timeCreated);
            postDao.insertSelective(post);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    redis.hset("allPosts", String.valueOf(maxPostId - 200000), post);
                }
            }).start();
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
        try {
            PostWithBLOBs post = new PostWithBLOBs(text, uid, timeCreated, photoUrls);
            postDao.insertSelective(post);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    redis.hset("allPosts", String.valueOf(maxPostId - 200000), post);
                }
            }).start();
            return true;
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    public List<PostWithBLOBs> getAllPosts() {
        try {
            List<PostWithBLOBs> allPosts = (List<PostWithBLOBs>)(Object)redis.hashGetAll("allPosts").values();
            if (allPosts.size() != 0) {
                return allPosts;
            }
            return postDao.selectAllPosts();
        }
        catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
    }

    public PostWithBLOBs getPost(Integer postId ) {
        try {
            PostWithBLOBs post = (PostWithBLOBs) redis.hashGet("allPosts", String.valueOf(postId - 200000));
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

}
