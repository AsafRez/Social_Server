package com.social.Entity;

import javax.persistence.*; // מייבא את הכלים של JPA

@Entity // מגדיר ל-Hibernate לייצר טבלה עבור המחלקה הזו
@Table(name = "likes") // מגדיר את שם הטבלה בדיוק כפי שביקשת
public class Like {

    @Id // מגדיר את ה-id כמפתח ראשי
    @GeneratedValue(strategy = GenerationType.IDENTITY) // מגדיר Auto Increment ב-MySQL
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "post_id")
    private int postId;

    // חובה: קונסטרקטור ריק עבור Hibernate
    public Like() {
    }

    public Like(int id, int userId, int postId) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
    }

    // Getters ו-Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
}