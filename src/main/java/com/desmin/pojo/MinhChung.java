package com.desmin.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "minh_chung")
@NamedQueries({
    @NamedQuery(
        name = "MinhChung.findByTrangThai",
        query = "SELECT m FROM MinhChung m WHERE m.trangThai = :trangThai AND m.active = true"
    ),
    @NamedQuery(
        name = "MinhChung.findByTrangThaiAndKhoa",
        query = "SELECT m FROM MinhChung m JOIN m.thamGia t JOIN t.sinhVien sv JOIN sv.lop l JOIN l.khoa k " +
                "WHERE m.trangThai = :trangThai AND m.active = true AND k.id = :khoaId"
    ),
    @NamedQuery(
        name = "MinhChung.findById",
        query = "SELECT m FROM MinhChung m WHERE m.id = :id"
    )
})
public class MinhChung implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name="anh_minh_chung",nullable = false)
    private String anhMinhChung;

    @OneToOne
    @JoinColumn(name = "tham_gia_id", nullable = false)
    private ThamGia thamGia;

    @Enumerated(EnumType.STRING)
    @Column(name="trang_thai")
    private TrangThai trangThai = TrangThai.CHO_DUYET;

    public enum TrangThai {
        CHO_DUYET, DA_DUYET, TU_CHOI
    }

    // Audit
    @Column(nullable = false)
    private boolean active = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnhMinhChung() {
        return anhMinhChung;
    }

    public void setAnhMinhChung(String anhMinhChung) {
        this.anhMinhChung = anhMinhChung;
    }

    public ThamGia getThamGia() {
        return thamGia;
    }

    public void setThamGia(ThamGia thamGia) {
        this.thamGia = thamGia;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
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
