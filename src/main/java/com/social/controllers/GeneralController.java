package com.social.controllers;


import com.social.Classes.*;
import com.social.responses.BasicResponse;
import com.social.responses.PostResponse;
import com.social.utils.DbUtils;
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
    private Map<Integer, String>OTPmap;

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
//     private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
//     private static boolean isValidEmail(String email) {
//         if (email == null) {
//             return false;
//         }
//             Matcher matcher = EMAIL_PATTERN.matcher(email);
//             return matcher.matches();
//         }


    @RequestMapping("/register")
    public BasicResponse register(String username, String password) {
            String hashedPassword = generateMD5(username, password);
            return dbUtils.registerUser(username, hashedPassword);

    }
    @RequestMapping("/Count-Likes")
    public int countLikes(@RequestParam int postId, @CookieValue(value = "token", required = false) String token) {
        if (token != null && dbUtils.verifyToken(token).isSuccess()) {
            return dbUtils.countLikes(postId);
        }
        return -1;
    }
    @RequestMapping("/Count-Followers")
    public int countFollowrs(@RequestParam int userId, @CookieValue(value = "token", required = false) String token) {
        if (token != null && dbUtils.verifyToken(token).isSuccess()) {
            return  dbUtils.countFollowers(userId);
        }
        return -1;
    }


    @RequestMapping("/Get-Following-Posts")
    public PostResponse getPost(int userId,int number ){
        return dbUtils.getPosts(userId,number);
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

    @RequestMapping(value = "/Search-User")
    public List <User> SearchUser(@RequestParam String username, @CookieValue(value = "token", required = false) String token) {
        if (token != null && dbUtils.verifyToken(token).isSuccess()) {
            return  dbUtils.searchUser(username);
        }
        return null;
    }

//    private int sendSMS(String tel, int OTPSend) {
//        // 1. הגדרת ה-URL המלא (כבר כולל את הפרמטרים בתוכו)
//        String serverURL = "https://backend-qcf9.onrender.com/send-sms?token=Almog464@&phoneNumber="
//                + tel + "&message=Hello your one time password is:" + OTPSend
//                + " please do not share it with others PLZZZZZZ";
//        // 2. יצירת אובייקט RestTemplate
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(serverURL, null, String.class);
//
//            // בדיקה אם הסטטוס קוד הוא 200 (OK)
//            if (response.getStatusCode().is2xxSuccessful()) {
//                System.out.println("SMS נשלח בהצלחה: " + response.getBody());
//                return 1; // החזרת הצלחה
//            } else {
//                return 0; // נכשל
//            }
//        } catch (Exception e) {
//            System.err.println("שגיאה בשליחת SMS: " + e.getMessage());
//            return -1; // שגיאת רשת
//        }
//    }
//    private int generateOTP(){
//        Random random = new Random();
//        int otp = random.nextInt(10000,99999);
//        return otp;
//    }


}
