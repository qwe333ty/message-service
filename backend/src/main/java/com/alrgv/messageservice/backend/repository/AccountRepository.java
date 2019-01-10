package com.alrgv.messageservice.backend.repository;

import com.alrgv.messageservice.backend.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends
        CrudRepository<Account, Integer>,
        PagingAndSortingRepository<Account, Integer> {

    Optional<Account> findAccountByUsername(String username);
}
