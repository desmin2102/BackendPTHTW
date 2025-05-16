package com.desmin.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tham_gia")
@NamedQueries({
    @NamedQuery(
        name = "ThamGia.getThamGiasChuaDiemDanhByDiemRenLuyenId",
        query = "FROM ThamGia t WHERE t.sinhVien.id = (SELECT d.sinhVien.id FROM DiemRenLuyen d WHERE d.id = :drlId) " +
                "AND t.hoatDongNgoaiKhoa.hkNh.id = (SELECT d.hkNh.id FROM DiemRenLuyen d WHERE d.id = :drlId) " +
                "AND t.state = :state"
    ),
    @NamedQuery(
        name = "ThamGia.findBySinhVienAndHoatDongNgoaiKhoa",
        query = "SELECT t FROM ThamGia t WHERE t.sinhVien = :sinhVien AND t.hoatDongNgoaiKhoa = :hoatDong"
    ),
    @NamedQuery(
        name = "ThamGia.findByHoatDongNgoaiKhoaId",
        query = "SELECT t FROM ThamGia t WHERE t.hoatDongNgoaiKhoa.id = :hoatDongId"
    ),
    @NamedQuery(
        name = "ThamGia.findBySinhVienId",
        query = "SELECT t FROM ThamGia t WHERE t.sinhVien.id = :sinhVienId"
    ),
    @NamedQuery(
        name = "ThamGia.findById",
        query = "SELECT t FROM ThamGia t WHERE t.id = :id"
    ),
    @NamedQuery(
        name = "ThamGia.findBySinhVienIdAndState",
        query = "SELECT t FROM ThamGia t JOIN FETCH t.hoatDongNgoaiKhoa h JOIN FETCH h.dieu WHERE t.sinhVien.id = :sinhVienId AND t.state IN ('DangKy', 'DiemDanh')"
    )
})
public class ThamGia implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

     @OneToOne(mappedBy = "thamGia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private MinhChung minhChung;

    
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

    /**
     * @return the minhChung
     */
    public MinhChung getMinhChung() {
        return minhChung;
    }

    /**
     * @param minhChung the minhChung to set
     */
    public void setMinhChung(MinhChung minhChung) {
        this.minhChung = minhChung;
    }
}
