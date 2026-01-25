package com.social.utils;

import com.social.Entity.*;
import com.social.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.social.utils.Errors.*;

@Component
public class DbUtils {
    private Connection connection;
    @Autowired
    private JwtUtils jwtUtils;

    @PostConstruct
    public void init() {
        try {
            String host = "localhost";
            String username = "root";
            String password = "1234";
            String schema = "socialnetwork";
            int port = 3306;
            String url = "jdbc:mysql://"
                    + host + ":" + port + "/"
                    + schema;
            this.connection =
                    DriverManager.getConnection(url, username, password);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println("Failed to create db connection");
            e.printStackTrace();
        }
    }

    private boolean checkUser(String username) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT id from users where username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Verify Token
    public BasicResponse verifyToken(String token) {
        if (token == null) return new BasicResponse(false, GENERIC_ERROR);

        try (PreparedStatement ps = this.connection.prepareStatement(
                "SELECT Id, Username FROM users WHERE token = ?")) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("Id"), rs.getString("Username"));
                return new UserResponse(true, null, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, GENERIC_ERROR);
    }

    //register query
    public BasicResponse registerUser(String username, String password,String link) {
        try {
            String PicPath="/images/DefaultPic.png";
            if(!link.isEmpty()){
                PicPath=link;
            }
            if (!checkUser(username)) {
                PreparedStatement ps = this.connection.prepareStatement("INSERT INTO users(username, password,Profile_image) values(?,?,?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, PicPath);
                int rs = ps.executeUpdate();
                if (rs == 0) {
                    return new BasicResponse(false, ERROR_USERNAME_TAKEN);
                }
            }else{
                return new BasicResponse(false, ERROR_USERNAME_TAKEN);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new BasicResponse(true, null);

    }


    //login query
    public UserResponse login(String username, String password) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT Id,username,Profile_image FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");

                    String token = jwtUtils.generateToken(username, userId);
                    return new UserResponse(true, null, new User(userId,
                            rs.getString("Username"), rs.getString("Profile_image")
                    ), token);
                }
                return new UserResponse(false, ERROR_WRONG_INFO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserResponse(false, ERROR_WRONG_INFO);
    }


    public User exportUserDetails(String username) {
        User user = null;
        user.setUserName(username);
        try {
            PreparedStatement ps = this.connection.prepareStatement(
                    "SELECT u.Id,u.Username,u.Profile_image,p.Content FROM users AS u" +
                            "LEFT JOIN posts AS p ON u.Id = p.Author " +
                            " LEFT JOIN follows f on f.Following=u.Id+" +
                            " LEFT JOIN likes l on l.User_id=u.Id " +
                            " WHERE u.Username=?"
            );

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
//                    user.setId(rs.getInt("id"));
            if (user != null) {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    public List<User> searchUser(String username) {
        List <User> users = new ArrayList<>();
        try {
            PreparedStatement ps = this.connection.prepareStatement(
                    "SELECT Id,Username,Profile_image FROM users where username LIKE ?");
            ps.setString(1, "%"+username+"%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(rs.getInt("Id"),
                            rs.getString("Username"),
                            rs.getString("Profile_image"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private boolean checkIfFollow(int followerId, int followingId) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT Follower FROM follows WHERE Follower=? AND Following=?");
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BasicResponse followUser(int followerId, int followingId) {
        if (followerId != followingId) {
            try {
                PreparedStatement ps;
                int rs;
                if (checkIfFollow(followerId, followingId)) {
                    ps = this.connection.prepareStatement("DELETE FROM follows WHERE Follower=? AND Following=?");
                } else {
                    ps = this.connection.prepareStatement("INSERT INTO follows (Follower,Following) VALUES (?,?)");
                }
                ps.setInt(1, followerId);
                ps.setInt(2, followingId);
                rs = ps.executeUpdate();
                if (rs == 1) {
                    return new BasicResponse(true, null);
                }
                return new BasicResponse(false, ERROR_DB_NOT_UPDATED);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new BasicResponse(false, ERROR_WRONG_INFO);

    }

    public int countLikes(int postId) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("Select Count(User_id) FROM likes WHERE Post_Id=?");
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countFollowers(int userId) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("Select Count(Follower) FROM follows WHERE Following=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean checkLikeStatus(int userId, int postId) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT 1 FROM likes WHERE Post_id=? AND User_id=?");
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BasicResponse likeToggle(int userId, int postId) {
        boolean likeStatus = checkLikeStatus(userId, postId);

        PreparedStatement ps;
        try {
            if (likeStatus) {
                ps = this.connection.prepareStatement("DELETE FROM likes WHERE Post_id=? AND User_id=?");
            } else {
                ps = this.connection.prepareStatement("INSERT INTO likes VALUES (?,?)");
            }
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            if (ps.executeUpdate() == 1) {
                return new BasicResponse(true, null);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new BasicResponse(false, ERROR_WRONG_INFO);
    }

    //Toggle like function
    public BasicResponse toggleLike(int userId, int postId) {
        String checkSQL = "SELECT 1 FROM likes WHERE Post_id=? AND User_id=?"; //check if liked query
        String insertSQL = "INSERT INTO likes (Post_id, User_id) VALUES (?, ?)"; //insert like query
        String deleteSQL = "DELETE FROM likes WHERE Post_id=? AND User_id=?"; //remove like query
        try (PreparedStatement checkPS = this.connection.prepareStatement(checkSQL)) {
            checkPS.setInt(1, postId);
            checkPS.setInt(2, userId);

            boolean alreadyLiked; // holds liked or not
            try (ResultSet rs = checkPS.executeQuery()) {//runs check if liked query to set alreadyLiked
                alreadyLiked = rs.next(); //returns true IF line exists
            }

            //if liked -> deletes the like using the remove query
            if (alreadyLiked) {
                try (PreparedStatement deletePS = this.connection.prepareStatement(deleteSQL)) {
                    deletePS.setInt(1, postId);
                    deletePS.setInt(2, userId);

                    int affected = deletePS.executeUpdate();
                    if (affected == 1) {
                        return new BasicResponse(true, null);//removed
                    }
                    return new BasicResponse(false, ERROR_DB_NOT_UPDATED);
                }
                // else (not liked) -> adds a like using the insert query
            } else {
                try (PreparedStatement insertPS = this.connection.prepareStatement(insertSQL)) {
                    insertPS.setInt(1, postId);
                    insertPS.setInt(2, userId);

                    int affected = insertPS.executeUpdate();
                    if (affected == 1) {
                        return new BasicResponse(true, null); //added
                    }
                    return new BasicResponse(false, ERROR_DB_NOT_UPDATED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, ERROR_DATABASE);
    }


    public BasicResponse publishedPost(int userid, String postContent) {
        try {
            if (!postContent.isEmpty()) {
                if (postContent.length() <= 500) {
                    PreparedStatement ps = this.connection.prepareStatement(
                            "INSERT INTO posts " +
                                    "(Content,Author,Posted_Date) VALUES (?,?,?)");
                    ps.setString(1, postContent);
                    ps.setInt(2, userid);
                    ps.setTimestamp(3,
                            new java.sql.Timestamp(System.currentTimeMillis()));
                    int results = ps.executeUpdate();
                    if (results == 1) {
                        return new BasicResponse(true, null);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, ERROR_WRONG_INFO);
    }

    public PostResponse getPosts(int userId, int count) {
        PreparedStatement ps;
        List<Post> posts = new ArrayList<Post>();
        try {
            if (count != 0) {
                ps = this.connection.prepareStatement("SELECT P.Id,P.Content,P.Author,P.Posted_Date FROM posts P JOIN follows F ON F.following=P.Author " +
                        "WHERE F.follower=? ORDER BY P.Posted_Date DESC LIMIT ? ");
                ps.setInt(2, count);
            } else {
                ps = this.connection.prepareStatement("SELECT P.Id,P.Content,P.Author,P.Posted_Date FROM posts P JOIN follows F ON F.following=P.Author " +
                        "WHERE F.follower=?");

            }
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    posts.add(new Post(
                            rs.getInt("Id"),
                            rs.getInt("Author"),
                            rs.getString("Content"),
                            rs.getDate("Posted_date")));

                }
                return new PostResponse(true, null, posts);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new PostResponse(false, ERROR_WRONG_INFO, null);

    }
}

