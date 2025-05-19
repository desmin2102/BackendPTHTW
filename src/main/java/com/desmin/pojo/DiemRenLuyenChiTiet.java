package com.desmin.pojo;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "diem_ren_luyen_chi_tiet")
@NamedQueries({
    @NamedQuery(
        name = "DiemRenLuyenChiTiet.findByDiemRenLuyenId",
        query = "SELECT ct FROM DiemRenLuyenChiTiet ct WHERE ct.diemRenLuyen.id = :diemRenLuyenId"
    ),
       
})
public class DiemRenLuyenChiTiet  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diem_ren_luyen_id", nullable = false)
    private DiemRenLuyen diemRenLuyen;

    @ManyToOne
    @JoinColumn(name = "dieu_id", nullable = false)
    private Dieu dieu;

    @Column(nullable = false)
    private Integer diem;

    // Audit
    @Column(nullable = false)
    private boolean active = true;

    
      public DiemRenLuyenChiTiet(){
        
    }
    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiemRenLuyen getDiemRenLuyen() {
        return diemRenLuyen;
    }

    public void setDiemRenLuyen(DiemRenLuyen diemRenLuyen) {
        this.diemRenLuyen = diemRenLuyen;
    }

    public Dieu getDieu() {
        return dieu;
    }

    public void setDieu(Dieu dieu) {
        this.dieu = dieu;
    }

    public Integer getDiem() {
        return diem;
    }

    public void setDiem(Integer diem) {
        this.diem = diem;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
