package com.desmin.pojo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "diem_ren_luyen")
public class DiemRenLuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User sinhVien;

    @ManyToOne
    @JoinColumn(name = "hk_nh_id", nullable = false)
    private HocKyNamHoc hkNh;

    @Column(nullable = false)
    private Integer diemTong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private XepLoai xepLoai;

    @OneToMany(mappedBy = "diemRenLuyen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiemRenLuyenChiTiet> chiTiet;

    // Audit fields
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    public enum XepLoai {
        XUAT_SAC, GIOI, KHA, TRUNG_BINH, YEU, KEM
    }

    @PrePersist
    @PreUpdate
    public void validateAndSetXepLoai() {
        if (sinhVien.getRole() != User.Role.SINH_VIEN) {
            throw new IllegalStateException("Chỉ sinh viên có điểm rèn luyện");
        }
        if (diemTong >= 90) {
            xepLoai = XepLoai.XUAT_SAC;
        } else if (diemTong >= 80) {
            xepLoai = XepLoai.GIOI;
        } else if (diemTong >= 70) {
            xepLoai = XepLoai.KHA;
        } else if (diemTong >= 50) {
            xepLoai = XepLoai.TRUNG_BINH;
        } else if (diemTong >= 30) {
            xepLoai = XepLoai.YEU;
        } else {
            xepLoai = XepLoai.KEM;
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

    public HocKyNamHoc getHkNh() {
        return hkNh;
    }

    public void setHkNh(HocKyNamHoc hkNh) {
        this.hkNh = hkNh;
    }

    public Integer getDiemTong() {
        return diemTong;
    }

    public void setDiemTong(Integer diemTong) {
        this.diemTong = diemTong;
    }

    public XepLoai getXepLoai() {
        return xepLoai;
    }

    public void setXepLoai(XepLoai xepLoai) {
        this.xepLoai = xepLoai;
    }

    public List<DiemRenLuyenChiTiet> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<DiemRenLuyenChiTiet> chiTiet) {
        this.chiTiet = chiTiet;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
