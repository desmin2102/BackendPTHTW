/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface HoatDongNgoaiKhoaService {

    List<HoatDongNgoaiKhoa> getHoatDongNgoaiKhoas(Map<String, String> params);

    HoatDongNgoaiKhoa getHoatDongNgoaiKhoaById(long id);

    List<HoatDongNgoaiKhoa> findByHanDangKyBefore(LocalDate date);

    List<HoatDongNgoaiKhoa> findActiveAndNotExpired(LocalDate date);

    void update(HoatDongNgoaiKhoa hoatDong);

    HoatDongNgoaiKhoa addHoatDongNgoaiKhoa(HoatDongNgoaiKhoa h);

    List<HoatDongNgoaiKhoa> findByUserParticipated(User user);

    List<HoatDongNgoaiKhoa> findByUserRegisteredOrAttended(User user);

}
