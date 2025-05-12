/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.pojo.Khoa;
import com.desmin.pojo.Lop;
import com.desmin.pojo.User;
import com.desmin.pojo.User.Role;
import com.desmin.repositories.UserRepository;
import com.desmin.services.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author admin
 */
@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username!");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole().name()));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public User addSinhVien(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setHo(params.get("ho"));
        u.setTen(params.get("ten"));
        u.setEmail(params.get("email"));
        u.setUsername(params.get("username"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setMssv(params.get("mssv"));
        u.setActive(true);
        u.setRole(Role.SINH_VIEN);
        u.setFile(avatar);  // <- cần thiết!

        // Xử lý lop
        String lopId = params.get("lop");
        if (lopId != null && !lopId.isEmpty()) {
            Lop lop = new Lop();
            lop.setId(Long.parseLong(lopId));
            u.setLop(lop);
        }
        // Xử lý avatar
        if (!u.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(u.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatarUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.userRepo.addUser(u);
    }

    @Override
    public User addTroLySinhVien(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setEmail(params.get("email"));
        u.setUsername(params.get("username"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setActive(true);
        u.setRole(Role.TRO_LY_SINH_VIEN);
        u.setFile(avatar);  // <- cần thiết!

        // Xử lý khoaPhuTrach
        String khoaId = params.get("khoaPhuTrach");
        if (khoaId != null && !khoaId.isEmpty()) {
            Khoa khoa = new Khoa();
            khoa.setId(Long.parseLong(khoaId));
            u.setKhoaPhuTrach(khoa);
        } else {
            throw new IllegalArgumentException("Khoa phụ trách là bắt buộc cho trợ lý sinh viên");
        }
// Xử lý avatar
        if (!u.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(u.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatarUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepo.authenticate(username, password);
    }
}
