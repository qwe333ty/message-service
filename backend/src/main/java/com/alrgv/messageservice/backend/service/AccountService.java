package com.alrgv.messageservice.backend.service;

import com.alrgv.messageservice.backend.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AccountService {
    Account save(Account account);

    Iterable<Account> saveAll(Iterable<Account> accounts);

    Optional<Account> findById(Integer id);

    Page<Account> findAll(Pageable pageable);

    void delete(Integer id);
}
