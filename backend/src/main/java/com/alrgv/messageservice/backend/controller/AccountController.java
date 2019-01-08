package com.alrgv.messageservice.backend.controller;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Resource(name = "userDetailsServiceImpl")
    private AccountService service;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Optional<Account> findById(@PathVariable(name = "id") Integer id) {
        return service.findById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<Account> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Account create(@RequestBody Account account) {
        return service.create(account);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Account save(@RequestBody Account account) {
        return service.save(account);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save-all")
    public Iterable<Account> saveAll(@RequestBody Iterable<Account> accounts) {
        return service.saveAll(accounts);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable(name = "id") Integer id) {
        service.delete(id);
    }
}
