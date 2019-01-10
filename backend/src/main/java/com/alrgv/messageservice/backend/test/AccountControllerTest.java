package com.alrgv.messageservice.backend.test;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.AccountAuthority;
import com.alrgv.messageservice.backend.entity.RestPageImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AccountControllerTest.class);

    @LocalServerPort
    private int port;

    @Test
    public void A() throws Exception {
        log.info("Port={}", port);

        RestTemplate restTemplate = new RestTemplate();
        Page<Account> page = restTemplate.exchange(
                "http://localhost:" + port + "/api/account?page=0",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestPageImpl<Account>>() {
                })
                .getBody();

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void B() throws Exception {
        Account account = new Account();
        account.setUsername("zubr");
        account.setPassword("belarus");

        AccountAuthority f = new AccountAuthority();
        f.setId(1);
        f.setAuthrty("ROLE_USER");
        f.setAccounts(Collections.singletonList(account));

        account.setAuthorities(Collections.singletonList(f));

        RestTemplate restTemplate = new RestTemplate();
        Account response = restTemplate
                .postForEntity(
                        "http://localhost:" + port + "/api/account",
                        account,
                        Account.class)
                .getBody();
        assertThat(response.getUsername()).isEqualTo("zubr");
        assertThat(response.getPassword()).isEqualTo("belarus");

        Page<Account> page = restTemplate.exchange(
                "http://localhost:" + port + "/api/account?page=0",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestPageImpl<Account>>() {
                })
                .getBody();

        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void C() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete("http://localhost:" + port + "/api/account/4");

        A();
    }
}
