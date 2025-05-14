package com.desmin.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "bai_viet")
@NamedQueries({
    @NamedQuery(name = "BaiViet.findAll", query = "SELECT b FROM BaiViet b"),
    @NamedQuery(name = "BaiViet.findById", query = "SELECT b FROM BaiViet b WHERE b.id = :id"),
    @NamedQuery(name = "BaiViet.findBaiVietByHoatDongId", query = "SELECT b FROM BaiViet b WHERE b.hoatDongNgoaiKhoa.id = :hoatDongId"),})
public class BaiViet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User troLy;

    @OneToOne
    @JoinColumn(name = "hoat_dong_id", nullable = false, unique = true)
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    @JsonIgnore
    @Transient
    private MultipartFile file;

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

    /**
     * @return the file
     */
    public MultipartFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
