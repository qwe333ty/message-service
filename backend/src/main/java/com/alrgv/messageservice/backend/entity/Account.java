package com.alrgv.messageservice.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "message_service")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonNull
    @Column(name = "id")
    private Integer id;

    @NonNull
    @Column(name = "user_name")
    private String username;

    @NonNull
    @Column(name = "password")
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private Collection<AccountAuthority> authorities;

}
