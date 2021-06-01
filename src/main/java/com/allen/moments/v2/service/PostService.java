package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.PostDao;
import com.allen.moments.v2.model.Post;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.ThreadPoolManager;
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
            int postId = getPostId();
            Post post = new Post(postId, text, uid);
            int rowsAffected = postDao.insertSelective(post);
            redis.set("post:" + postId, post);
            return rowsAffected == 1;
    }

    /**
     * insert post with photo(s)
     * @return whether the operation success or not
     */
    public boolean addPostWithPhotos(int uid, String text, List<String> photoUrls) {
        int postId = getPostId();
            Post post = new Post(postId, text, uid, photoUrls);
            int rowsAffected = postDao.insertSelective(post);
            if (rowsAffected == 1) {
                // user another thread to execute redis update in order to avoid waiting for redis execution in the main thread
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        redis.set("post:" + postId, post);
                    }
                });
                return true;
            }
            return false;
    }

    public Post getPost(int postId) {
//            redis.get("post:comment:" + postId);
            Post post = (Post) redis.get("post:" + (postId));

            if (post != null) {
                return post;
            }
            return postDao.selectByPrimaryKey(postId);
    }

    public List<Post> getAllPosts() {
        return postDao.selectAllPosts();
    }

    public List<Post> getPostsWithHighestLikeCounts(int start, int limit) {
        try {
            if (start < 100) { // to avoid having big key in redis, only writes the first 100 popular posts into redis
                List<Post> posts = (List<Post>) (Object) redis.listGet("postsWithHighestLikeCounts", start, limit);
                if (posts != null) {
                    return posts;
                }
            }
            List<Post>  postsInDB = postDao.getPostsWithHighestLikeCounts(start, limit);
            redis.listMSetWithExpiration("postsWithHighestLikeCounts", (List<Object>) (Object) postsInDB.subList(0, 100), 20);
            return postsInDB;
        }
        catch (ClassCastException castException) {
            return null;
        }
    }

    /**
     * like a post or unlike a post
     * @return returns whether the operation succeeded or not
     */
    public boolean likeOrUnlike(boolean isLike, int uid, int postId) {
        if (isLike && postDao.insertLikeRecord(uid, postId) == 1) {
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    redis.setSet("post:likes:" + (postId), uid);
                }
            });
            return true;
        }
        else if (!isLike && postDao.removeLikeRecord(uid, postId) == 1) {
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    redis.setRemove("post:likes:" + (postId), uid);
                }
            });
            return true;
        }
        return false;
    }

    public List<Integer> getUsersWhoLikedPosts(int postId, int start, int limit) {
        return postDao.selectUidsThatLikedPost(postId, start, limit);
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
