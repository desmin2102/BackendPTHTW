package com.desmin.pojo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "hoat_dong_ngoai_khoa")
public class HoatDongNgoaiKhoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_hoat_dong", nullable = false, unique = true)
    private String maHoatDong;

    @Column(name = "ten_hoat_dong", nullable = false)
    private String tenHoatDong;

    @Column(nullable = false)
    private LocalDateTime ngay;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "diem_ren_luyen", nullable = false)
    private Integer diemRenLuyen = 5;

    @Column(name = "han_dang_ky")
    private LocalDate hanDangKy;

    @ManyToOne
    @JoinColumn(name = "dieu_id", nullable = false)
    private Dieu dieu;

    @ManyToOne
    @JoinColumn(name = "hk_nh_id", nullable = false)
    private HocKyNamHoc hkNh;

    @OneToMany(mappedBy = "hoatDongNgoaiKhoa")
    private List<ThamGia> danhSachThamGia;

    // Audit
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaHoatDong() {
        return maHoatDong;
    }

    public void setMaHoatDong(String maHoatDong) {
        this.maHoatDong = maHoatDong;
    }

    public String getTenHoatDong() {
        return tenHoatDong;
    }

    public void setTenHoatDong(String tenHoatDong) {
        this.tenHoatDong = tenHoatDong;
    }

    public LocalDateTime getNgay() {
        return ngay;
    }

    public void setNgay(LocalDateTime ngay) {
        this.ngay = ngay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiemRenLuyen() {
        return diemRenLuyen;
    }

    public void setDiemRenLuyen(Integer diemRenLuyen) {
        this.diemRenLuyen = diemRenLuyen;
    }

    public LocalDate getHanDangKy() {
        return hanDangKy;
    }

    public void setHanDangKy(LocalDate hanDangKy) {
        this.hanDangKy = hanDangKy;
    }

    public Dieu getDieu() {
        return dieu;
    }

    public void setDieu(Dieu dieu) {
        this.dieu = dieu;
    }

    public HocKyNamHoc getHkNh() {
        return hkNh;
    }

    public void setHkNh(HocKyNamHoc hkNh) {
        this.hkNh = hkNh;
    }

    public List<ThamGia> getDanhSachThamGia() {
        return danhSachThamGia;
    }

    public void setDanhSachThamGia(List<ThamGia> danhSachThamGia) {
        this.danhSachThamGia = danhSachThamGia;
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