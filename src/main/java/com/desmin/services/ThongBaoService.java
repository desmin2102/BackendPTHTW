/*
 * Click nb://fs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nb://fs://SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface ThongBaoService {
    List<ThongBao> getThongBaos(Map<String, String> params);
    ThongBao getThongBaoById(long id);
    List<ThongBao> getThongBaosByUserOrPublic(User user);
    void thongBaoHoatDongMoi(String noiDung);
}