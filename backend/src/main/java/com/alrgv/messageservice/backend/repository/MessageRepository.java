package com.alrgv.messageservice.backend.repository;

import com.alrgv.messageservice.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface MessageRepository {

    Long count(@NonNull Long id);

    Page<Message> findMessagesByUserId(@NonNull Pageable pageable, @NonNull Long id);
}
