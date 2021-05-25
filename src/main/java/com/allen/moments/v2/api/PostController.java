package com.allen.moments.v2.api;

import com.allen.moments.v2.model.Post;
import com.allen.moments.v2.service.PostService;
import com.allen.moments.v2.service.S3Service;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.annotations.RequireToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public JsonResult<?> addPost(HttpServletRequest request, @JsonProperty("text") String text, List<MultipartFile> photos) {
        JsonResult<?> result;
        boolean isAddSuccess = false;
        int uid = (Integer) request.getAttribute("logged_uid");
        try {
            if (photos != null && photos.size() > 0) { // 如果用户上传了图片
                List<String> photoUrls = s3Service.upload(photos);
                isAddSuccess = postService.addPostWithPhotos(uid, text, photoUrls);
            } else { // 用户没有上传图片
                isAddSuccess = postService.addPost(uid, text);
            }
            return isAddSuccess ? JsonResult.success() : JsonResult.failure(200001, "post upload failed");

        }
        catch (Exception exception) {
            System.err.println(exception.getMessage());
            return JsonResult.unknownFailure();
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
    public JsonResult<?> addComment(HttpServletRequest request, HttpServletResponse response, @PathVariable("postId") Integer postId, String comment) {
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
            return JsonResult.successWithData(post);
        }
        return JsonResult.failure(200002, "no post found");
    }
}
