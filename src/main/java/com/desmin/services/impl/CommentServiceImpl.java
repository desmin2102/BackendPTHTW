/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.Comment;
import com.desmin.repositories.CommentRepository;
import com.desmin.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository cmtRepo;

    @Override
    public void addCmt(Comment cmt) {
        this.cmtRepo.addCmt(cmt);
    }

}
