package com.alrgv.messageservice.backend.test;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.repository.MessageRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryTest.class);
    private static List<Message> userMessages = new ArrayList<>();

    @Resource(name = "messageRepository")
    private MessageRepository repository;

    private static void delimiter() {
        log.info("");
        log.info("");
        log.info("====================================================================================================================");
    }

    @Test
    public void A() {
        delimiter();
        log.info("Method A (select)");
        Page<Message> page = repository.findMessagesByUserIdAndParams(PageRequest.of(0, 10),
                1L,
                Lists.list(true, null),
                Lists.list(true, null));
        Assertions.assertThat(page).isNotNull();
        page.getContent().forEach(message -> log.info(message.toString()));
    }

    @Test
    public void B() throws Exception {
        delimiter();
        log.info("Method B (insert data)");
        log.info("Take data from JSON");
        JsonArray jsonArray = new JsonParser()
                .parse(new FileReader("C:\\Users\\Alex\\Desktop\\MOCK_DATA_2.json"))
                .getAsJsonArray();

        List<Message> messages = new ArrayList<>();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Message message = new Message();

            Account from = new Account();
            from.setId(jsonObject.get("from").getAsInt());
            message.setFrom(from);

            Account to = new Account();
            to.setId(jsonObject.get("to").getAsInt());
            message.setTo(to);

            Timestamp sent = Timestamp.valueOf(jsonObject.get("sent").getAsString());
            message.setSent(sent);

            message.setSubject(jsonObject.get("subject").getAsString());
            message.setMessageText(jsonObject.get("messageText").getAsString());
            message.setSendStatus(jsonObject.get("sendStatus").getAsInt());
            message.setReadStatus(jsonObject.get("readStatus").getAsBoolean());
            message.setStarred(jsonObject.get("starred").getAsBoolean());
            message.setImportant(jsonObject.get("important").getAsBoolean());

            messages.add(message);
        }

        log.info("Data from JSON has extracted. Size = {}", messages.size());
        log.info("Starting insert message instances into database.");
        for (Message message : messages) {
            Message temp = repository.save(message, true);
            log.info("Intermediate result: {}", temp);
            userMessages.add(temp);
        }
        log.info("All messages inserted now.");
    }

    @Test
    public void C() {
        delimiter();
        log.info("Method C (update)");
/*        log.info("User messages size = {}", userMessages.size());
        Assert.assertFalse(userMessages.isEmpty());*/

        log.info("Get from database message");
        Message message = repository.findMessagesByUserIdAndParams(PageRequest.of(2, 10),
                1L,
                Lists.list(true, null),
                Lists.list(true, null)).getContent().get(9);

        log.info("Current message: {}", message);
        message.setSubject("Random subject");
        message.setMessageText("Hello. My name is alex. What is ur name?");
        message.setImportant(!message.isImportant());
        message.setStarred(!message.isStarred());
        message.setReadStatus(!message.isReadStatus());

        log.info("Save message");
        repository.save(message, false);
        log.info("After save: {}", message);
    }

    @Test
    public void d() {
        delimiter();
        log.info("Method D (delete)");
        /*        Assert.assertFalse(userMessages.isEmpty());*/

        log.info("Delete all inserted by test entities");
        int affectedRows = repository.deleteAll(userMessages);
        log.info("Deleted {} rows from database.", affectedRows);
        delimiter();
    }
}
