package com.alrgv.messageservice.backend.repository;

import com.alrgv.messageservice.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public interface MessageRepository {

    long count(long id);

    Page<Message> findMessagesByUserIdAndParams(@NonNull Pageable pageable,
                                                @NonNull Long id,
                                                @NonNull List<Object> importantParams,
                                                @NonNull List<Object> starredParams);

    <S extends Message> S save(S message, boolean isMerge) throws RuntimeException;

    int delete(long id) throws Exception;

    int deleteAll(Iterable<? extends Message> messages) throws RuntimeException;
}
