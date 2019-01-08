package com.alrgv.messageservice.backend.controller;

import com.alrgv.messageservice.backend.security.JwtTokenUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/token")
public class AuthenticationController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsernamePassword {
        private String username;
        private String password;
    }

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    @RequestMapping(value = "/generate-token", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UsernamePassword userDetails) throws AuthenticationException {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDetails.username,
                            userDetails.password)
            );
            final String token = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.ok("");
        }
    }

    @RequestMapping(value = "/expDate/{token}", method = RequestMethod.GET)
    public ResponseEntity<?> GetExpDate(@PathVariable String token) {
        token = token.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        return ResponseEntity.ok(jwtTokenUtil.getExpirationDateFromToken(token));
    }

}