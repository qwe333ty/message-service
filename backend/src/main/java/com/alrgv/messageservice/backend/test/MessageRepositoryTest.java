package com.alrgv.messageservice.backend.test;

import com.alrgv.messageservice.backend.repository.MessageRepository;
import org.assertj.core.util.Lists;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryTest.class);

    @Resource(name = "messageRepository")
    private MessageRepository repository;

    @Test
    public void A() {
        log.info("=====================================================================");
        repository.findMessagesByUserIdAndParams(PageRequest.of(0, 10),
                1L,
                Lists.list(true, null),
                Lists.list(true, null)).getContent().forEach(message -> log.info(message.toString()));
        log.info("=====================================================================");
    }
}
