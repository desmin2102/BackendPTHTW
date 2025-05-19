/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Like;
import com.desmin.pojo.User;
import com.desmin.repositories.BaiVietRepository;
import com.desmin.repositories.LikeRepository;
import com.desmin.repositories.UserRepository;
import com.desmin.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaiVietRepository baiVietRepository;

@Override
public boolean addLike(Like like) {
    if (like == null || like.getUser() == null || like.getBaiViet() == null ||
        like.getUser().getId() == null || like.getBaiViet().getId() == null) {
        throw new IllegalArgumentException("Like, user hoặc bài viết không hợp lệ");
    }

    User user = userRepository.getUserById(like.getUser().getId());
    BaiViet baiViet = baiVietRepository.getBaiVietById(like.getBaiViet().getId());
    if (user == null) {
        throw new IllegalArgumentException("Người dùng không tồn tại: " + like.getUser().getId());
    }
    if (baiViet == null) {
        throw new IllegalArgumentException("Bài viết không tồn tại: " + like.getBaiViet().getId());
    }

    like.setUser(user);
    like.setBaiViet(baiViet);

    if (likeRepository.existsLike(user.getId(), baiViet.getId())) {
        likeRepository.removeLike(like);
        return false;
    } else {
        likeRepository.addLike(like);
        return true;
    }
}

    @Override
    public boolean existsLike(long userId, long baiVietId) {
        return this.likeRepository.existsLike(userId, baiVietId);
    }
}
