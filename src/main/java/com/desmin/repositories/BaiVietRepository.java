/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.Khoa;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface BaiVietRepository {
    List<BaiViet> getAllBaiViet();
    BaiViet getBaiVietById(long id);
    BaiViet addBaiViet(BaiViet baiViet);
    void updateBaiViet(BaiViet baiViet);
    void deleteBaiViet(long id);
    BaiViet getBaiVietByHoatDongId(long hoatDongId);

}
