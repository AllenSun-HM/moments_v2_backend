package com.allen.moments.v2.api;

import com.allen.moments.v2.model.PostWithBLOBs;
import com.allen.moments.v2.service.PostService;
import com.allen.moments.v2.service.S3Service;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.annotations.RequireToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * handle person info CRUD
 *
 */
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
    public JsonResult addPost(@JsonProperty("uid") Integer uid, @JsonProperty("text") String text, List<MultipartFile> photos) {
        JsonResult result;
        boolean isAddSuccess = false;
        try {
            if (photos != null && photos.size() > 0) { // 如果用户上传了图片
                List<String> photoUrls = s3Service.upload(photos);
                isAddSuccess = postService.addPostWithPhotos(uid, text, photoUrls);
            } else { // 用户没有上传图片
                isAddSuccess = postService.addPost(uid, text);
            }
        }
        catch (Exception exception) {
            System.err.println(exception);
        }
        finally {
            return isAddSuccess ? new JsonResult(true) : new JsonResult(200001, "post upload failed");
        }
    }

    @GetMapping()
    @RequireToken
    public JsonResult getPost(Integer postId) {
        PostWithBLOBs post = postService.getPost(postId);
        JsonResult<PostWithBLOBs> result;
        if (post != null) {
            return new JsonResult<>(post);
        }
        return new JsonResult<>(200002, "no post found");
    }
}
