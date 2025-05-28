/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.Like;
import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import com.desmin.repositories.BaiVietRepository;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
import com.desmin.repositories.ThongBaoRepository;
import com.desmin.repositories.UserRepository;
import com.desmin.services.BaiVietService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
@Service
@Transactional
public class BaiVietServiceImpl implements BaiVietService {

    @Autowired
    private BaiVietRepository baiVietRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private HoatDongNgoaiKhoaRepository hdnkRepo;
    @Autowired
    private ThongBaoRepository thongBaoRepo;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public List<BaiViet> getAllBaiViet(Map<String, String> params) {
        return baiVietRepo.getAllBaiViet(params);
    }

    @Override
    public BaiViet getBaiVietById(long id) {
        return this.baiVietRepo.getBaiVietById(id);
    }


    @Override
    public BaiViet addBaiViet(Map<String, String> params, MultipartFile imageFile) {
        try {
            BaiViet baiViet = new BaiViet();
            baiViet.setTitle(params.get("title"));
            baiViet.setContent(params.get("content"));
            baiViet.setActive(true);
            baiViet.setCreatedDate(LocalDateTime.now());
            baiViet.setUpdatedDate(LocalDateTime.now());

            // Lấy ID trợ lý từ params
            long troLyId = Long.parseLong(params.get("troLyId"));
            User troLy = this.userRepo.getUserById(troLyId);
            baiViet.setTroLy(troLy);

            // Lấy ID hoạt động ngoại khóa từ params
            long hoatDongId = Long.parseLong(params.get("hoatDongId"));
            HoatDongNgoaiKhoa hoatDong = this.hdnkRepo.getHoatDongNgoaiKhoaById(hoatDongId);
            baiViet.setHoatDongNgoaiKhoa(hoatDong);

            // Xử lý ảnh (imageFile)
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Map res = cloudinary.uploader().upload(imageFile.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    baiViet.setImageUrl(res.get("secure_url").toString()); // Lưu URL ảnh
                } catch (IOException ex) {
                    Logger.getLogger(BaiVietServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Lỗi khi upload ảnh bài viết: " + ex.getMessage());
                }
            }

            // Lưu bài viết vào DB
            BaiViet savedBaiViet = this.baiVietRepo.addBaiViet(baiViet);

                     // Tạo thông báo và gửi email cho từng sinh viên
            List<User> sinhViens = this.userRepo.findAllSinhVien();
            for (User sinhVien : sinhViens) {
                // Tạo thông báo
                ThongBao thongBao = new ThongBao();
                thongBao.setNoiDung("Bài viết mới: " + savedBaiViet.getTitle());
                thongBao.setCreatedDate(LocalDateTime.now());
                thongBao.setUser(sinhVien); // Gán user là sinh viên
                thongBaoRepo.save(thongBao);

                // Gửi email
                if (sinhVien.getEmail() != null && !sinhVien.getEmail().isEmpty()) {
                    try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(sinhVien.getEmail());
                        message.setSubject("Thông báo bài viết mới");
                        message.setText("Bài viết mới: " + savedBaiViet.getTitle() + "\nNội dung: " + savedBaiViet.getContent());
                        mailSender.send(message);
                    } catch (MailException ex) {
                        Logger.getLogger(BaiVietServiceImpl.class.getName()).log(Level.SEVERE, 
                            "Lỗi khi gửi email đến " + sinhVien.getEmail(), ex);
                        // Không ném exception để tiếp tục với sinh viên khác
                    }
                }
            }


            return savedBaiViet;
        } catch (Exception ex) {
            Logger.getLogger(BaiVietServiceImpl.class.getName()).log(Level.SEVERE, "Error when creating BaiViet", ex);
            throw new RuntimeException("Lỗi khi tạo bài viết: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void updateBaiViet(BaiViet baiViet) {
        this.baiVietRepo.updateBaiViet(baiViet);
    }

    @Override
    public void deleteBaiViet(long id) {
        this.baiVietRepo.deleteBaiViet(id);
    }

    @Override
    public BaiViet getBaiVietByHoatDongId(long hoatDongId) {
        return this.baiVietRepo.getBaiVietByHoatDongId(hoatDongId);
    }

    @Override
    public List<Comment> getComments(long baivietId) {
        return this.baiVietRepo.getComments(baivietId);

    }

    @Override
    public List<Like> getLikes(long baivietId) {
        return this.baiVietRepo.getLikes(baivietId);

    }
}
