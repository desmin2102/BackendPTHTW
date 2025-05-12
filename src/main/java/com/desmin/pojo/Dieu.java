package com.desmin.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "dieu")
public class Dieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_dieu", nullable = false, unique = true)
    private String maDieu;

    @Column(name = "ten_dieu", nullable = false)
    private String tenDieu;

    @Column(name = "diem_toi_da", nullable = false)
    private Integer diemToiDa = 30;

    // Audit
    @Column(nullable = false)
    private boolean active = true;

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaDieu() {
        return maDieu;
    }

    public void setMaDieu(String maDieu) {
        this.maDieu = maDieu;
    }

    public String getTenDieu() {
        return tenDieu;
    }

    public void setTenDieu(String tenDieu) {
        this.tenDieu = tenDieu;
    }

    public Integer getDiemToiDa() {
        return diemToiDa;
    }

    public void setDiemToiDa(Integer diemToiDa) {
        this.diemToiDa = diemToiDa;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
