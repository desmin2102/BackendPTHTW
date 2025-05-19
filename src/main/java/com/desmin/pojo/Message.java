/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "message_type", nullable = false)
    private String messageType = "TEXT";

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "is_bot", nullable = false)
    private boolean isBot = false;

    @Column(nullable = false)
    private String status = "SENT";

    // Constructors
    public Message() {}

    public Message(ChatRoom room, User sender, String content, String fileUrl, String messageType, boolean isBot) {
        this.room = room;
        this.sender = sender;
        this.content = content;
        this.fileUrl = fileUrl;
        this.messageType = messageType;
        this.isBot = isBot;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatRoom getRoom() { return room; }
    public void setRoom(ChatRoom room) { this.room = room; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isBot() { return isBot; }
    public void setBot(boolean isBot) { this.isBot = isBot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // toString
    @Override
    public String toString() {
        return "Message{" +
               "id=" + id +
               ", room=" + room +
               ", sender=" + sender +
               ", content='" + content + '\'' +
               ", fileUrl='" + fileUrl + '\'' +
               ", messageType='" + messageType + '\'' +
               ", timestamp=" + timestamp +
               ", isBot=" + isBot +
               ", status='" + status + '\'' +
               '}';
    }
}