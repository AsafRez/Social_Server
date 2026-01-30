package com.social.Entity;

import javax.persistence.*; // מייבא את כלי ה-JPA
import java.util.List;

@Entity // אומר ל-Hibernate לייצר טבלה עבור המשתמשים
@Table(name = "USERS") // מגדיר את שם הטבלה ב-MySQL
public class User {

    @Id // מגדיר את ה-id כמפתח ראשי
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment ב-MySQL
    private int id;

    @Column(name = "user_name", unique = true) // מבטיח ששמות משתמש יהיו ייחודיים
    private String userName;

    private String password;

    @Column(name = "profile_image", columnDefinition = "TEXT") // מאפשר כתובות URL ארוכות לתמונות
    private String Profile_image;

    @Transient // Hibernate יתעלם מהרשימה הזו בשלב יצירת הטבלה כדי למנוע שגיאות
    private List<Post> posts;

    @Transient
    private List<User> followers;

    @Transient
    private List<User> following;

    // קונסטרקטורים (נשארו כפי שהיו)
    public User() {
    }

    public User(int id, String userName, String password, String profile_picture) {
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

    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
        this.Profile_image = "/images/default.png";
    }

    // Getters ו-Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getProfile_image() { return Profile_image; }
    public void setProfile_image(String profile_image) { this.Profile_image = profile_image; }
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    public List<User> getFollowers() { return followers; }
    public void setFollowers(List<User> followers) { this.followers = followers; }
    public List<User> getFollowing() { return following; }
    public void setFollowing(List<User> following) { this.following = following; }
}