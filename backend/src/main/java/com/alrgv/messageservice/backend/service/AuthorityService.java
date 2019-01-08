package com.alrgv.messageservice.backend.service;

import com.alrgv.messageservice.backend.entity.AccountAuthority;
import org.springframework.stereotype.Service;

public interface AuthorityService {
    Iterable<AccountAuthority> findAll();
}
