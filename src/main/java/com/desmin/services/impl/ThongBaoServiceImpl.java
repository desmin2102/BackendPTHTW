/*
 * Click nb://fs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nb://fs://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import com.desmin.repositories.ThongBaoRepository;
import com.desmin.services.ThongBaoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
@Transactional
public class ThongBaoServiceImpl implements ThongBaoService {

    @Autowired
    private ThongBaoRepository thongBaoRepo;

    @Override
    public List<ThongBao> getThongBaos(Map<String, String> params) {
        return this.thongBaoRepo.getThongBaos(params);
    }

    @Override
    public ThongBao getThongBaoById(long id) {
        return this.thongBaoRepo.getThongBaoById(id);
    }

    @Override
    public List<ThongBao> getThongBaosByUserOrPublic(User user) {
        return this.thongBaoRepo.getThongBaosByUserOrPublic(user);
    }

    @Override
    public void thongBaoHoatDongMoi(String noiDung) {
        ThongBao thongBao = new ThongBao();
        thongBao.setNoiDung(noiDung);
        thongBao.setCreatedDate(LocalDateTime.now());
        thongBao.setUser(null); // null để gửi đến tất cả
        thongBaoRepo.save(thongBao);
    }
}