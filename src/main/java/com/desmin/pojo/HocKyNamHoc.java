package com.desmin.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "hoc_ky_nam_hoc", uniqueConstraints = @UniqueConstraint(columnNames = {"hocKy", "namHoc"}))
public class HocKyNamHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HocKy hocKy;

    @Column(nullable = false)
    private String namHoc;

    public enum HocKy {
        ONE, TWO, THREE
    }

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

    public HocKy getHocKy() {
        return hocKy;
    }

    public void setHocKy(HocKy hocKy) {
        this.hocKy = hocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
