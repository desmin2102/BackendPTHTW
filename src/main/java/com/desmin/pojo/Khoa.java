package com.desmin.pojo;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "khoa")
@NamedQueries({
    @NamedQuery(name = "Khoa.findAll", query = "SELECT k FROM Khoa k"),
    @NamedQuery(name = "Khoa.findById", query = "SELECT k FROM Khoa k WHERE k.id = :id"),})

public class Khoa implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_khoa", nullable = false, unique = true)
    private String maKhoa;

    @Column(name = "ten_khoa", nullable = false)
    private String tenKhoa;

    // Audit
    @Column(nullable = false)
    private boolean active = true;

      public Khoa(){
        
    }
    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
