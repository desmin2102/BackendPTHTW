package com.desmin.pojo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@NamedQuery(
        name = "ChatRoom.findByParticipantId",
        query = "SELECT cr FROM ChatRoom cr WHERE cr.sinhVien.id = :userId "
)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firebase_room_id", nullable = false, unique = true)
    private String firebaseRoomId;

    @ManyToOne
    @JoinColumn(name = "sinh_vien_id", nullable = false)
    private User sinhVien;
    
    @ManyToOne
    @JoinColumn(name = "khoa_id", nullable = false)
    private Khoa khoa;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public ChatRoom() {
    }

    public ChatRoom(String firebaseRoomId, User sinhVien, Khoa khoa) {
        this.firebaseRoomId = firebaseRoomId;
        this.sinhVien = sinhVien;
        this.khoa = khoa;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirebaseRoomId() {
        return firebaseRoomId;
    }

    public void setFirebaseRoomId(String firebaseRoomId) {
        this.firebaseRoomId = firebaseRoomId;
    }

    public User getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(User sinhVien) {
        this.sinhVien = sinhVien;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "ChatRoom{"
                + "id=" + id
                + ", firebaseRoomId='" + firebaseRoomId + '\''
                + ", sinhVien=" + sinhVien
                + ", createdAt=" + createdAt
                + '}';
    }

    /**
     * @return the khoaId
     */
    public Khoa getKhoa() {
        return khoa;
    }

    /**
     * @param khoaId the khoaId to set
     */
    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    /**
     * @return the khoaId
     */

}
