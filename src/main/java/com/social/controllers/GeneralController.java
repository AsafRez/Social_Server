package com.social.controllers;

import com.social.Entity.*;
import com.social.responses.BasicResponse;
import com.social.responses.PostResponse;
import com.social.responses.UserResponse;
import com.social.utils.DbUtils;
import com.social.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;
    @Autowired
    private JwtUtils jwtUtils;

    @PostConstruct
    public void init() {
    }

    private String generateMD5(String username, String password) {
        try {
            String source = username + password;

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(source.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    @RequestMapping("/register")
    public BasicResponse register(String username, String password, String photolink) {
        String hashedPassword = generateMD5(username, password);
        return dbUtils.registerUser(username, hashedPassword, photolink);

    }

    @RequestMapping("/Count-Likes")
    public int countLikes(@RequestParam int postId, @CookieValue(value = "token", required = false) String token) {
        if (token != null) {
            return dbUtils.countLikes(postId);
        }
        return -1;
    }

    @RequestMapping("/Count-Followers")
    public int countFollowrs(@RequestParam int userId, @CookieValue(value = "token", required = false) String token) {
        if (token != null) {
            return dbUtils.countFollowers(userId);
        }
        return -1;
    }


    @RequestMapping("/Get-Following-Posts")
    public PostResponse getPost(@RequestParam int numberToFetch, @CookieValue(value = "token", required = false) String token) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            PostResponse postsanswer = null;
            if (userName != null) {
                postsanswer = dbUtils.getPosts(userName, numberToFetch);
                return postsanswer;
            }

        }
        return new PostResponse(false, null, null);
    }

    @RequestMapping(value = "/Login")
    public UserResponse login(String username, String password) {
        String hashedPassword = generateMD5(username, password);
        UserResponse result = dbUtils.login(username, hashedPassword);
        String token = jwtUtils.generateToken(username);
        result.setToken(token);
        return result;
    }

    @PostMapping(value = "/Update")
    public BasicResponse update(@CookieValue(name = "token", required = true) String token, String newUsername, String password,
                                String photolink) {
        String userName = jwtUtils.extractUserId(token);
        User current = dbUtils.exportUserDetails(userName);
        String hashedPassword = generateMD5(userName, password);
        if (current.getPassword().equals(password) ||password==null) {
            return dbUtils.update(userName, newUsername, hashedPassword, photolink);
        } else {

            BasicResponse result = dbUtils.update(userName, hashedPassword, newUsername, photolink);
            return result;
        }
    }

    @PostMapping(value = "/Publish-Post")
    public BasicResponse publishedPost(@CookieValue(name = "token", required = true) String token, String content) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            if (userName != null) {
                dbUtils.publishedPost(userName, content);
                return new BasicResponse(true, null);
            }
            return new BasicResponse(false, null);
        }
        return new BasicResponse(false, null);
    }


    @PostMapping(value = "/Like-Post")
    public BasicResponse toggleLike(@CookieValue(name = "token", required = true) String token, int postid) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            if (userName != null) {
                dbUtils.toggleLike(userName, postid);
                return new BasicResponse(true, null);
            }
            return new BasicResponse(false, null);
        }
        return new BasicResponse(false, null);
    }


    @PostMapping(value = "/Toggle-Follow")
    public BasicResponse follow(@CookieValue(name = "token", required = true) String token, String follower, String following) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            if (userName != null) {
                dbUtils.toggleFollow(follower, following);
                return new BasicResponse(true, null);
            }
            return new BasicResponse(false, null);
        }
        return new BasicResponse(false, null);
    }


    @PostMapping(value = "/Get-User-Profile")
    public UserResponse exportUserDetails(@CookieValue(name = "token", required = true) String token) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            if (userName != null) {
                User user = dbUtils.exportUserDetails(userName);
                return new UserResponse(true, null, user);
            }
        }
        return new UserResponse(false, null, null);
    }

    @PostMapping(value = "/Get-user-post")
    public PostResponse getPosts(@RequestBody int numberToFech, @CookieValue(name = "token", required = true) String token) {
        if (token != null) {
            String userName = jwtUtils.extractUserId(token);
            if (userName != null) {
                return dbUtils.getPosts(userName, numberToFech);
            }
        }
        return new PostResponse(false, null, null);
    }


    @RequestMapping(value = "/Search-User")
    public List<User> SearchUser(@RequestParam String username, @CookieValue(value = "token", required = true) String token) {
        if (token != null) {
            return dbUtils.searchUser(username);
        }
        return null;
    }

}

