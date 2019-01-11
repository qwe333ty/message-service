package com.alrgv.messageservice.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "message", schema = "message_service")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private Account from;
    private Account to;
    private Timestamp sent;
    private String subject;
    private String messageText;
    private int sendStatus;
    private boolean readStatus;
    private boolean starred;
    private boolean important;
}
