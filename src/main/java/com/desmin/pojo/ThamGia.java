package com.desmin.pojo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tham_gia")
public class ThamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User sinhVien;

    @ManyToOne
    @JoinColumn(name = "hoat_dong_id", nullable = false)
    private HoatDongNgoaiKhoa hoatDongNgoaiKhoa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThai state = TrangThai.DangKy;

    public enum TrangThai {
        DangKy, DiemDanh, BaoThieu
    }

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void validate() {
        if (sinhVien.getRole() != User.Role.SINH_VIEN) {
            throw new IllegalStateException("Chỉ sinh viên được tham gia hoạt động");
        }
        if (state == TrangThai.DangKy && LocalDate.now().isAfter(hoatDongNgoaiKhoa.getHanDangKy())) {
            throw new IllegalStateException("Đã hết hạn đăng ký hoạt động");
        }
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(User sinhVien) {
        this.sinhVien = sinhVien;
    }

    public HoatDongNgoaiKhoa getHoatDongNgoaiKhoa() {
        return hoatDongNgoaiKhoa;
    }

    public void setHoatDongNgoaiKhoa(HoatDongNgoaiKhoa hoatDongNgoaiKhoa) {
        this.hoatDongNgoaiKhoa = hoatDongNgoaiKhoa;
    }

    public TrangThai getState() {
        return state;
    }

    public void setState(TrangThai state) {
        this.state = state;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
