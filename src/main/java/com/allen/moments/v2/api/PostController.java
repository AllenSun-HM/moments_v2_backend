package com.allen.moments.v2.api;

import com.allen.moments.v2.model.Post;
import com.allen.moments.v2.service.PostService;
import com.allen.moments.v2.service.S3Service;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.annotations.RequireToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;
    private final S3Service s3Service;

    @Autowired
    public PostController(PostService postService, S3Service s3Service) {
        this.postService = postService;
        this.s3Service = s3Service;
    }

    @PostMapping()
    @RequireToken
    public JsonResult<?> addPost(HttpServletRequest request, @RequestBody String text, @RequestBody List<MultipartFile> photos) {
        int uid = (Integer) request.getAttribute("logged_uid");
        try {
            int postId;
            if (photos != null && photos.size() > 0) { // photos are uploaded
                List<String> photoUrls = s3Service.upload(photos);
                postId = postService.addPostWithPhotos(uid, text, photoUrls);
            } else { // no photos uploaded
                postId = postService.addPost(uid, text);
            }
            HashMap<String, Integer> result = new HashMap<>();
            result.put("post_id", postId);
            return JsonResult.successWithData(result);
        }
        catch (Exception exception) {
            return JsonResult.failure(200001, "post upload failed");
        }
    }

    @PostMapping("/like/{postId}")
    @RequireToken
    public JsonResult<?> likePost(HttpServletRequest request, HttpServletResponse response, @PathVariable("postId") Integer postId) {
        int uid = (Integer) request.getAttribute("logged_uid");
        JsonResult<?> result;
        try {
            if (postId == null) {
                throw new Exception("illegal query parameter");
            }
            if (postService.likeOrUnlike(true, uid, postId)) {
                return JsonResult.success();
            }
            else return JsonResult.unknownFailure();
        }
        catch (Exception exception) {
            if (exception.getClass() == DuplicateKeyException.class) {
                return JsonResult.failure(200003, "aleady liked this post");
            }
            return JsonResult.failure(200009, exception.getMessage());
        }
    }

    @GetMapping("like/{#postId}")
    @RequireToken()
    public JsonResult<?> getUidsWhoLikedThis(@PathVariable("postId") Integer postId, @RequestParam Integer start, @RequestParam Integer limit) {
        if (postId == null || start == null || limit == null) {
            return JsonResult.failure(200010, "illegal query parameter");
        }
        if (limit > 200) {
            return JsonResult.failure(200011, "query range is too big");
        }
        return JsonResult.successWithData(postService.getUsersWhoLikedPosts(postId, start, limit));
    }

    @DeleteMapping("/like/{postId}")
    @RequireToken
    public JsonResult<?> UnlikePost(HttpServletRequest request, HttpServletResponse response, @PathVariable("postId") Integer postId) {
        int uid = (Integer) request.getAttribute("logged_uid");
        JsonResult<?> result;
        try {
            if (postId == null) {
                throw new Exception("illegal query parameter");
            }
            if (postService.likeOrUnlike(false, uid, postId)) {
                return JsonResult.success();
            }
            else return JsonResult.unknownFailure();
        }
        catch (Exception exception) {
            return JsonResult.failure(200003, exception.getMessage());
        }
    }

    @PostMapping("/comment/{postId}")
    @RequireToken
    public JsonResult<?> addComment(HttpServletRequest request, HttpServletResponse response, @PathVariable("postId") Integer postId, @RequestBody  String comment) {
        int uid = (Integer) request.getAttribute("logged_uid");
        JsonResult<?> result;
        try {
            if (postId == null || comment == null) {
                throw new Exception("illegal query parameter");
            }
            if (postService.addComment(uid, postId, comment)) {
                return JsonResult.success();
            }
            else return JsonResult.unknownFailure();
        }
        catch (Exception exception) {
            return JsonResult.failure(200003, exception.getMessage());
        }
    }

    @DeleteMapping("/comment/{postId}/{commentId}")
    @RequireToken
    public JsonResult<?> removeComment(HttpServletRequest request, HttpServletResponse response, @PathVariable("commentId") Integer commentId) {
        int uid = (Integer) request.getAttribute("logged_uid");
        JsonResult<?> result;
        try {
            if (commentId == null) {
                throw new Exception("illegal query parameter");
            }
            if (postService.deleteComment(commentId, uid)) {
                return JsonResult.success();
            }
            else return JsonResult.unknownFailure();
        }
        catch (Exception exception) {
            return JsonResult.failure(200003, exception.getMessage());
        }
    }

    @GetMapping("/{postId}")
    @RequireToken
    public JsonResult<?> getPost(@PathVariable("postId") int postId) {
        Post post = postService.getPost(postId);
        JsonResult<Post> result;
        if (post != null) {
            post.setJsonPhotoUrls(null);
            return JsonResult.successWithData(post);
        }
        return JsonResult.failure(200002, "no post found");
    }

    @GetMapping("/rank/like_count")
    @RequireToken
    public JsonResult<?> getPostsWithHighestLikeCounts(@RequestParam Integer start, @RequestParam Integer limit) {
        if (start == null || limit == null) {
            return JsonResult.failure(200010, "illegal query parameter");
        }
        List<Post> posts = postService.getPostsWithHighestLikeCounts(start, limit);
        if (posts == null) {
            return JsonResult.failure(400001, "no more posts");
        }
        return JsonResult.successWithData(posts);
    }
 }
