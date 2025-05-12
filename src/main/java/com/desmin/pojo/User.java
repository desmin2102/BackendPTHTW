package com.desmin.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByHo", query = "SELECT u FROM User u WHERE u.ho = :ho"),
    @NamedQuery(name = "User.findByMssv", query = "SELECT u FROM User u WHERE u.mssv = :mssv"),
    @NamedQuery(name = "User.findByLop", query = "SELECT u FROM User u WHERE u.lop = :lop"),
    @NamedQuery(name = "User.findByKhoa", query = "SELECT u FROM User u WHERE u.khoaPhuTrach = :khoaPhuTrach"),
    @NamedQuery(name = "User.findByTen", query = "SELECT u FROM User u WHERE u.ten = :ten"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByActive", query = "SELECT u FROM User u WHERE u.active = :active"),
    @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role = :role"),
    @NamedQuery(name = "User.findByAvatarUrl", query = "SELECT u FROM User u WHERE u.avatarUrl = :avatarUrl")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    private String username;

    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    private String email;

    @Basic(optional = false)
    @Column(name = "active")
    private Boolean active;

    @Basic(optional = false)
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Basic(optional = false)
    @Column(name = "mssv", unique = true)
    private String mssv;

    @Basic(optional = false)
    @Column
    private String ho;

    @Basic(optional = false)
    @Column
    private String ten;

    @ManyToOne
    @JoinColumn(name = "lop_id")
    private Lop lop;

    @ManyToOne
    @JoinColumn(name = "khoa_phu_trach_id")
    private Khoa khoaPhuTrach;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<Comment> commentSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<Like> likeSet;

    @Transient
    private MultipartFile file;

    public enum Role {
        SINH_VIEN, TRO_LY_SINH_VIEN, CVCTSV, ADMIN
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (role == Role.SINH_VIEN) {
            if (mssv == null || ho == null || ten == null || lop == null) {
                throw new IllegalStateException("Thông tin sinh viên không đầy đủ");
            }
            if (!email.endsWith("@school.edu.vn")) {
                throw new IllegalStateException("Email phải thuộc domain @school.edu.vn");
            }
            if (!username.equals(email)) {
                throw new IllegalStateException("Username phải giống email cho sinh viên");
            }
            if (khoaPhuTrach != null) {
                throw new IllegalStateException("Sinh viên không được có khoa phụ trách");
            }
        } else if (role == Role.TRO_LY_SINH_VIEN) {
            if (khoaPhuTrach == null) {
                throw new IllegalStateException("Trợ lý sinh viên phải có khoa phụ trách");
            }
            if (lop != null || mssv != null || ho != null || ten != null) {
                throw new IllegalStateException("Trợ lý sinh viên không được có thông tin sinh viên");
            }
        } else {
            if (lop != null || khoaPhuTrach != null || mssv != null || ho != null || ten != null) {
                throw new IllegalStateException("CVCTSV hoặc ADMIN không được có thông tin sinh viên hoặc khoa phụ trách");
            }
        }
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Lop getLop() {
        return lop;
    }

    public void setLop(Lop lop) {
        this.lop = lop;
    }

    public Khoa getKhoaPhuTrach() {
        return khoaPhuTrach;
    }

    public void setKhoaPhuTrach(Khoa khoaPhuTrach) {
        this.khoaPhuTrach = khoaPhuTrach;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
