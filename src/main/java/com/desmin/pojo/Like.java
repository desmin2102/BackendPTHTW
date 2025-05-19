package com.desmin.pojo;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "like_interaction", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bai_viet_id"}))
@NamedQueries({
    @NamedQuery(
        name = "Like.existsByUserAndBaiViet",
        query = "SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId AND l.baiViet.id = :baiVietId"
    ),
    @NamedQuery(
        name = "Like.deleteByUserAndBaiViet",
        query = "DELETE FROM Like l WHERE l.user.id = :userId AND l.baiViet.id = :baiVietId"
    ),
    @NamedQuery(
        name = "Like.countByBaiVietId",
        query = "SELECT COUNT(l) FROM Like l WHERE l.baiViet.id = :baiVietId"
    )
})
public class Like implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bai_viet_id", nullable = false)
    private BaiViet baiViet;

      public Like(){
        
    }
    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BaiViet getBaiViet() {
        return baiViet;
    }

    public void setBaiViet(BaiViet baiViet) {
        this.baiViet = baiViet;
    }
}
