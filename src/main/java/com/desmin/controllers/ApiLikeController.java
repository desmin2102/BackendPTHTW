/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Like;
import com.desmin.pojo.User;
import com.desmin.services.LikeService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiLikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/secure/likes")
    public ResponseEntity<?> toggleLike(@RequestParam("userId") long userId, @RequestParam("baiVietId") long baiVietId) {
        Like like = new Like();
        like.setUser(new User());
        like.getUser().setId(userId);
        like.setBaiViet(new BaiViet());
        like.getBaiViet().setId(baiVietId);

        boolean isLiked = likeService.addLike(like);
        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/secure/is-liked")
public ResponseEntity<Boolean> isLiked(@RequestParam("userId") long userId, @RequestParam("baiVietId") long baiVietId) {
    boolean exists = likeService.existsLike(userId, baiVietId);
    return ResponseEntity.ok(exists);
}
}
