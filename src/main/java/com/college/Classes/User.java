package com.college.Classes;
import java.util.List;


public class User {
    private int id;
    private String userName;
    private String password;
    private String profile_picture;

    private List<Post> posts; //User uploaded posts
    private List<User> followers; //User followers list
    private List<User> following; //User following list

    public User(int id, String userName, String password, String profile_picture, List<Post> posts, List<User> followers, List<User> following) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.profile_picture = profile_picture;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
    }
    public User(int id, String userName, String profile_picture, List<Post> posts, List<User> followers, List<User> following) {
        this.id = id;
        this.userName = userName;
        this.profile_picture = profile_picture;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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

    public void setFollowing(List<User> following) {
        this.following = following;
    }
}
