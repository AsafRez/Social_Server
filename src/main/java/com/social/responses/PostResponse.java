package com.social.responses;

import com.social.Classes.Post;

import java.util.List;

public class PostResponse extends  BasicResponse{
    private List<Post> posts;

    public PostResponse(boolean success, Integer errorCode, List<Post> posts) {
        super(success, errorCode);
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
