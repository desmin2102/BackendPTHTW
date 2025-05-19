/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.Like;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
public interface BaiVietService {

    List<BaiViet> getAllBaiViet(Map<String, String> params);

    BaiViet getBaiVietById(long id);

    BaiViet addBaiViet(Map<String, String> params,MultipartFile avatar);

    void updateBaiViet(BaiViet baiViet);

    void deleteBaiViet(long id);

    BaiViet getBaiVietByHoatDongId(long hoatDongId);
     List<Comment> getComments(long baivietId);
          List<Like> getLikes(long baivietId);



}
