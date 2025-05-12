package com.desmin.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bai_viet")
public class BaiViet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "tro_ly_id", nullable = false)
    private User troLy;

    @ManyToOne
    @JoinColumn(name = "hoat_dong_id", nullable = false)
    private HoatDongNgoaiKhoa hoatDongNgoaiKhoa;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "baiViet")
    @JsonIgnore
    private Set<Comment> commentSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "baiViet")
    @JsonIgnore
    private Set<Like> likeSet;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getTroLy() {
        return troLy;
    }

    public void setTroLy(User troLy) {
        this.troLy = troLy;
    }

    public HoatDongNgoaiKhoa getHoatDongNgoaiKhoa() {
        return hoatDongNgoaiKhoa;
    }

    public void setHoatDongNgoaiKhoa(HoatDongNgoaiKhoa hoatDongNgoaiKhoa) {
        this.hoatDongNgoaiKhoa = hoatDongNgoaiKhoa;
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

    public Set<Comment> getCommentSet() {
        return commentSet;
    }

    public void setCommentSet(Set<Comment> commentSet) {
        this.commentSet = commentSet;
    }

    public Set<Like> getLikeSet() {
        return likeSet;
    }

    public void setLikeSet(Set<Like> likeSet) {
        this.likeSet = likeSet;
    }
}
