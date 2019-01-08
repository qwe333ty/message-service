package com.alrgv.messageservice.backend.controller;

import com.alrgv.messageservice.backend.entity.AccountAuthority;
import com.alrgv.messageservice.backend.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authority")
public class AuthorityController {

    private AuthorityService service;

    @Autowired
    public AuthorityController(AuthorityService service) {
        this.service = service;
    }

    @RequestMapping(value = "/all")
    public Iterable<AccountAuthority> findAll() {
        return service.findAll();
    }
}
