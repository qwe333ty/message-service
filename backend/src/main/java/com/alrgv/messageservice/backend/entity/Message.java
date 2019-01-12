package com.alrgv.messageservice.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "message", schema = "message_service")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user")
    private Account from;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private Account to;

    @Column(name = "sent")
    private Timestamp sent;

    @Column(name = "subject")
    private String subject;

    @Column(name = "messageText")
    private String messageText;

    @Column(name = "send_status")
    private int sendStatus;

    @Column(name = "read_status")
    private boolean readStatus;

    @Column(name = "starred")
    private boolean starred;

    @Column(name = "important")
    private boolean important;
}
