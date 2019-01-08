package com.alrgv.messageservice.backend.service.impl;

import com.alrgv.messageservice.backend.entity.AccountAuthority;
import com.alrgv.messageservice.backend.repository.AuthorityRepository;
import com.alrgv.messageservice.backend.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityRepository repository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and hasAuthority('READ')")
    @Override
    public Iterable<AccountAuthority> findAll() {
        return repository.findAll();
    }
}
