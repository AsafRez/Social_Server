package com.college.Classes;
import java.sql.Date;
import java.util.List;


public class Post {

    private int Id;
    private int AuthorId;
    private String content;
    private Date PostDate;
//    private List <Like> likes;

    public Post(int id, int authorId, String content, Date postDate) {
        Id = id;
        AuthorId = authorId;
        this.content = content;
        PostDate = postDate;
//        this.likes = likes;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(int authorId) {
        AuthorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostDate() {
        return PostDate;
    }

    public void setPostDate(Date postDate) {
        PostDate = postDate;
    }

//    public List<Like> getLikes() {
//        return likes;
//    }
//
//    public void setLikes(List<Like> likes) {
//        this.likes = likes;
//    }
}
