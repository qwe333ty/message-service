package com.alrgv.messageservice.backend.repository;

import com.alrgv.messageservice.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public interface MessageRepository {

    long count(long userId);

    Page<Message> findMessagesByUserIdAndParams(@NonNull Pageable pageable,
                                                @NonNull Long id,
                                                @NonNull List<Boolean> importantParams,
                                                @NonNull List<Boolean> starredParams);

    <S extends Message> S save(S message, boolean isMerge);

    int delete(long id);

    int deleteAll(Iterable<Long> messages);
}
