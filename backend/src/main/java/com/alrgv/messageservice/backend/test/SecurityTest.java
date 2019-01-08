package com.alrgv.messageservice.backend.test;

import com.alrgv.messageservice.backend.controller.AuthenticationController;
import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.RestPageImpl;
import com.alrgv.messageservice.backend.security.JwtTokenUtil;
import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityTest {

    @LocalServerPort
    private int port;

    private static final String urlWOPort = "http://localhost:";
    private static final String auth = "Bearer ";

    private static final Logger log = LoggerFactory.getLogger(SecurityTest.class);

    @Test
    public void A() {
        RestTemplate restTemplate = new RestTemplate();
        log.info("URI: {}", (urlWOPort + port + "/token/generate-token"));
        String token = restTemplate.postForEntity(urlWOPort + port + "/token/generate-token",
                new AuthenticationController.UsernamePassword("alexa", "alexa"),
                String.class).getBody();
        HttpHeaders httpHeaders = new HttpHeaders();
        assertThat(token).isNotEqualTo(null);
        assertThat(token).isNotEqualTo("");
        log.info("Valid token for current user is equal to {}", token);

        if (!httpHeaders.containsKey("Content-Type")) {
            httpHeaders.add("Content-Type", "application/json");
        } else {
            httpHeaders.set("Content-Type", "application/json");
        }
        assertThat(httpHeaders.get("Content-Type")).isEqualTo(Collections.singletonList("application/json"));

        httpHeaders.add("Authorization", auth + token);
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        RestPageImpl<Account> accounts = restTemplate.exchange(urlWOPort + port + "/api/account",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<RestPageImpl<Account>>() {
                }).getBody();
        assertThat(accounts.getContent().size()).isEqualTo(2);
    }
}
