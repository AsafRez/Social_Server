package com.college.controllers;


import com.college.responses.BasicResponse;
import com.college.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

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
    public BasicResponse register(String username, String password) {
        String hashedPassword = generateMD5(username, password);
        return dbUtils.registerUser(username, hashedPassword);

    }

    @RequestMapping(value = "/login")
    public BasicResponse login( String username,  String password) {
        String hashedPassword = generateMD5(username, password);
        return dbUtils.login(username, hashedPassword);
    }
    @RequestMapping(value = "/Publish-Post")
    public BasicResponse post(int userid,  String content) {
        return dbUtils.publishedPost(userid, content);
    }
    @RequestMapping(value = "/Like-Post")
    public BasicResponse like(int userid,  int postid ) {
        return dbUtils.toggleLike(userid, postid);
    }
    @RequestMapping(value = "/Follow-User")
    public BasicResponse follow(int followerid,  int followingid ) {
        return dbUtils.followUser(followerid, followingid);
    }


}
