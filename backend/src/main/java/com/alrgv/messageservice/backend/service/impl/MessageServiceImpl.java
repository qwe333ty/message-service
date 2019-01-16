package com.alrgv.messageservice.backend.service.impl;

import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.repository.MessageRepository;
import com.alrgv.messageservice.backend.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource(name = "messageRepositoryImpl")
    private MessageRepository repository;

    @Override
    public long count(long userId) {
        return repository.count(userId);
    }

    @Override
    public Page<Message> findMessagesByUserIdAndParams(Pageable pageable, Long id, List<Boolean> importantParams, List<Boolean> starredParams) {
        return repository.findMessagesByUserIdAndParams(pageable, id, importantParams, starredParams);
    }

    @Override
    public <S extends Message> S save(S message, boolean isMerge) {
        return repository.save(message, isMerge);
    }

    @Override
    public int delete(long id) {
        return repository.delete(id);
    }

    @Override
    public int deleteAll(Iterable<Long> messages) {
        return repository.deleteAll(messages);
    }
}
