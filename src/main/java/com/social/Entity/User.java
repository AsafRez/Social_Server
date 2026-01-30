package com.social.Entity;

import java.util.List;


public class User {
    private int id;
    private String userName;
    private String password;
    private String Profile_image;

    private List<Post> posts; //User uploaded posts
    private List<User> followers; //User followers list
    private List<User> following; //User following list

    public User(int id, String userName,String password, String profile_picture) {
        this.id = id;
        this.password = password;
        this.Profile_image = profile_picture;
        this.userName = userName;
    }
    public User(int id, String userName, String profile_picture) {
        this.id = id;
        this.Profile_image = profile_picture;
        this.userName = userName;
    }


    public User() {
    }

    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
        this.Profile_image = "/images/default.png";

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_image() {
        return Profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.Profile_image = profile_image;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following)
    {
        this.following = following;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
