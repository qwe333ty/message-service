package com.alrgv.messageservice.backend.repository;

import com.alrgv.messageservice.backend.entity.AccountAuthority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<AccountAuthority, Integer> {
}
