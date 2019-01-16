package com.alrgv.messageservice.backend.controller;

import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.service.MessageService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private static final Map<Integer, List<Boolean>> TRANSLATOR = new HashMap<>();

    private MessageService service;

    @Autowired
    public MessageController(MessageService service) {
        this.service = service;
        TRANSLATOR.put(1, Lists.list(false, null));
        TRANSLATOR.put(2, Lists.list(true, null));
        TRANSLATOR.put(3, Lists.list(true, false));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<Message> messagePage(Pageable pageable,
                                     @RequestParam(name = "user-id") Long userId,
            /* 1 - false, null | 2 - true, false | 3 - true, false*/
                                     @RequestParam(name = "importance") int importanceStatus,
                                     @RequestParam(name = "starred") int starredStatus) {
        return service.findMessagesByUserIdAndParams(pageable, userId, TRANSLATOR.get(importanceStatus), TRANSLATOR.get(starredStatus));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/count")
    public ResponseEntity<Long> countMessagesByUserId(@RequestParam(name = "user-id") Long userId) {
        long num = service.count(userId);
        return new ResponseEntity<>(num, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Message create(@RequestBody Message message) {
        return service.save(message, true);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Message update(@RequestBody Message message) {
        return service.save(message, false);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Long> delete(@PathVariable(name = "id") Long id) {
        long affectedRows = service.delete(id);
        return new ResponseEntity<>(affectedRows, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userIds}")
    public ResponseEntity<Long> deleteAll(@PathVariable(name = "userIds") List<Long> userIds) {
        long affectedRows = service.deleteAll(userIds);
        return new ResponseEntity<>(affectedRows, HttpStatus.OK);
    }
}
