package com.social.Entity;

import javax.persistence.*; // מייבא את הכלים של JPA
import java.sql.Date;

@Entity // אומר ל-Hibernate לייצר טבלה עבור המחלקה הזו
@Table(name = "posts") // שם הטבלה במסד הנתונים
public class Post {

    @Id // מגדיר את ה-Id כמפתח ראשי (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // מגדיר Auto Increment ב-MySQL
    private int id;

    @Column(name = "author_id") // מיפוי השדה לשם עמודה ב-SQL
    private int authorId;

    @Column(columnDefinition = "TEXT") // מאפשר תוכן ארוך יותר מאשר String רגיל
    private String content;

    @Column(name = "post_date")
    private Date postDate;

    // חובה: קונסטרקטור ריק עבור Hibernate
    public Post() {
    }

    public Post(int id, int authorId, String content, Date postDate) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.postDate = postDate;
    }

    // Getters ו-Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }
}