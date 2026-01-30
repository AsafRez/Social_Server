package com.social.utils;


import com.social.Entity.*;
import com.social.responses.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.*;


import static com.social.utils.Errors.*;

@Component
public class DbUtils {
    private Connection connection;


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

    //register query
    public BasicResponse registerUser(String username, String password,String link) {
        try {
            String PicPath="/images/"+username+".png";
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

    public BasicResponse updateProfileImage(String username) {
        try {
            String PicPath="/images/"+username+".png";
            String sql = "UPDATE users SET Profile_image = ? WHERE Username = ?";
            if (checkUser(username)) {
                PreparedStatement ps = this.connection.prepareStatement(sql);
                ps.setString(1, PicPath);
                ps.setString(2, username);
                int rs = ps.executeUpdate();
                if (rs == 0) {
                    return new BasicResponse(false, 2000);
                }
            }else{
                return new BasicResponse(false, 2000);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BasicResponse(true, null);

    }



    private User getLists(User user) {
        List<User> followers=new ArrayList<>();
        List<User> following=new ArrayList<>();
        PreparedStatement ps = null;
        int count = 0;
        while (count != 2) {
            String getFollowers;
            if (count == 0) {
                getFollowers = "SELECT U.Id,U.Username,U.Profile_image FROM users U JOIN follows F ON F.follower=U.Username WHERE F.following=?";
            } else {
                getFollowers = "SELECT U.Id,U.Username,U.Profile_image FROM users U JOIN follows F ON F.following=U.Username WHERE F.follower=?";
            }
            try {
                ps = this.connection.prepareStatement(getFollowers);
                ps.setString(1, user.getUserName());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (count == 0) {
                            followers.add(
                                    new User(rs.getInt(1), rs.getString(2), rs.getString(3))
                            );
                        } else {
                            following.add(
                                    new User(rs.getInt(1), rs.getString(2), rs.getString(3))
                            );
                        }
                    }
                }
                count++;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        user.setFollowers(followers);
        user.setFollowing(following);
        return user;
    }
    //login query
    public UserResponse login(String username, String password) {
        boolean userValid = false;
        User user = new User();
        PreparedStatement ps;
        try {
             ps = this.connection.prepareStatement("SELECT username,Profile_image FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setUserName(username);
                    user.setProfile_image(rs.getString("Profile_image"));
                    userValid = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (userValid) {
            getLists(user);
            return new UserResponse(true, null,user);
        }
        return new UserResponse(false, GENERIC_ERROR);

    }



    public User exportUserDetails(String username) {
        User user = new User(0,username);
        String sqlQuery = "SELECT u.Id,u.Username,u.Profile_image,p.Content FROM users AS u"+
                " LEFT JOIN posts AS p ON u.Id = p.Author "+
                " LEFT JOIN follows f on f.Following=u.Id"+
                " LEFT JOIN likes l on l.User=u.Id" +
                " WHERE u.Username=?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(sqlQuery);

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user.setUserName(rs.getString("Username"));
                System.out.println(rs.getString("Profile_image"));
                user.setProfile_image(rs.getString("Profile_image"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLists(user);
        return user;
    }


    public List<User> searchUser(String username) {
        if (username.length() >= 2) {
            List<User> users = new ArrayList<>();

            try {
                PreparedStatement ps = this.connection.prepareStatement(
                        "SELECT Id,Username,Profile_image FROM users where username LIKE ?");
                ps.setString(1, "%" + username + "%");
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
        return null;
    }

    private boolean checkIfFollow(String follower, String following) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT Follower FROM follows WHERE Follower=? AND Following=?");
            ps.setString(1, follower);
            ps.setString(2, following);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BasicResponse toggleFollow(String  follower, String following) {
        if (!Objects.equals(follower, following)) {
            try {
                PreparedStatement ps;
                int rs;
                if (checkIfFollow(follower, following)) {
                    ps = this.connection.prepareStatement("DELETE FROM follows WHERE Follower=? AND Following=?");
                } else {
                    ps = this.connection.prepareStatement("INSERT INTO follows (Follower,Following) VALUES (?,?)");
                }
                ps.setString(1, follower);
                ps.setString(2, following);
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
            PreparedStatement ps = this.connection.prepareStatement("Select Count(User) FROM likes WHERE Post_Id=?");
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
            PreparedStatement ps = this.connection.prepareStatement("SELECT 1 FROM likes WHERE Post_id=? AND User=?");
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
                ps = this.connection.prepareStatement("DELETE FROM likes WHERE Post_id=? AND User=?");
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
    public BasicResponse toggleLike(String username, int postId) {
        String checkSQL = "SELECT 1 FROM likes WHERE Post_id=? AND User=?"; //check if liked query
        String insertSQL = "INSERT INTO likes (Post_id, User) VALUES (?, ?)"; //insert like query
        String deleteSQL = "DELETE FROM likes WHERE Post_id=? AND User=?"; //remove like query
        try (PreparedStatement checkPS = this.connection.prepareStatement(checkSQL)) {
            checkPS.setInt(1, postId);
            checkPS.setString(2, username);

            boolean alreadyLiked; // holds liked or not
            try (ResultSet rs = checkPS.executeQuery()) {//runs check if liked query to set alreadyLiked
                alreadyLiked = rs.next(); //returns true IF line exists
            }

            //if liked -> deletes the like using the remove query
            if (alreadyLiked) {
                try (PreparedStatement deletePS = this.connection.prepareStatement(deleteSQL)) {
                    deletePS.setInt(1, postId);
                    deletePS.setString(2, username);

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
                    insertPS.setString(2, username);

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


    public BasicResponse publishedPost(String username, String postContent) {
        try {
            if (!postContent.isEmpty()) {
                if (postContent.length() <= 500) {
                    PreparedStatement ps = this.connection.prepareStatement(
                            "INSERT INTO posts " +
                                    "(Content,Author,Posted_Date) VALUES (?,?,?)");
                    ps.setString(1, postContent);
                    ps.setString(2, username);
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

    public PostResponse getPosts(String username, int numberToFetch) {
        PreparedStatement ps;
        List<Post> posts = new ArrayList<Post>();
        try {
            if (numberToFetch != 0) {
                ps = this.connection.prepareStatement("SELECT P.Id,P.Content,P.Author,P.Posted_Date FROM posts P JOIN follows F ON F.following=P.Author " +
                        "WHERE F.follower=? ORDER BY P.Posted_Date DESC LIMIT  ? ");
                ps.setInt(2, numberToFetch);
            } else {
                ps = this.connection.prepareStatement("SELECT P.Id,P.Content,P.Author,P.Posted_Date FROM posts P JOIN follows F ON F.following=P.Author " +
                        "WHERE F.follower=?");

            }
            ps.setString(1, username);

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

