/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.Like;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface BaiVietRepository {
    List<BaiViet> getAllBaiViet(Map<String, String> params);
    BaiViet getBaiVietById(long id);
    BaiViet addBaiViet(BaiViet baiViet);
    void updateBaiViet(BaiViet baiViet);
    void deleteBaiViet(long id);
    BaiViet getBaiVietByHoatDongId(long hoatDongId);
    List<Comment> getComments(long baivietId);
    List <Like> getLikes(long baiVietId);

}
