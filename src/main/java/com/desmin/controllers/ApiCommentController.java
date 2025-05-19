/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.User;
import com.desmin.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
public class ApiCommentController {

    @Autowired
    private CommentService cmtService;

    @PostMapping("/secure/comments")
    public ResponseEntity<?> addComment(
            @RequestParam("userId") long userId,
            @RequestParam("baiVietId") long baiVietId,
            @RequestParam("content") String content) {

        Comment cmt = new Comment();
        cmt.setUser(new User());
        cmt.getUser().setId(userId);
        cmt.setBaiViet(new BaiViet());
        cmt.getBaiViet().setId(baiVietId);
        cmt.setContent(content); 

        cmtService.addCmt(cmt);

        return ResponseEntity.ok("Comment added successfully");
    }

}
