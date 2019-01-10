package com.alrgv.messageservice.backend.service.impl;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.repository.AccountRepository;
import com.alrgv.messageservice.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsServiceImpl")
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private AccountRepository repository;

    @Autowired
    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE')")
    @Override
    public Account create(Account account) {
        return repository.save(account);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('EDIT')")
    @Override
    public Account save(Account account) {
        return repository.save(account);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('EDIT')")
    @Override
    public Iterable<Account> saveAll(Iterable<Account> accounts) {
        return repository.saveAll(accounts);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and hasAuthority('READ')")
    @Override
    public Optional<Account> findById(Integer id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and hasAuthority('READ')")
    @Override
    public Page<Account> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE')")
    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE')")
    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Account> loadByUsername(String username) {
        return repository.findAccountByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = repository.findAccountByUsername(username);
        if (!accountOptional.isPresent())
            throw new UsernameNotFoundException("User not found.");

        Account account = accountOptional.get();
        return new User(account.getUsername(), account.getPassword(), account.getAuthorities());
    }
}