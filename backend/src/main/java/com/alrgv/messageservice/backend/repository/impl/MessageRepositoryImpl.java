package com.alrgv.messageservice.backend.repository.impl;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Long count(@NonNull Long id) {
        return jdbcTemplate
                .queryForObject("SELECT COUNT(*) FROM message m " +
                        "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                        "WHERE fromUser.id = " + id, Long.class);
    }

    @Override
    public Page<Message> findMessagesByUserId(@NonNull Pageable pageable, @NonNull Long id) {
        ResultSetExtractor<List<Message>> resultSetExtractor =
                rs -> {
                    ArrayList<Message> messages = new ArrayList<>();
                    while (rs.next()) {
                        Message message = new Message(
                                rs.getLong("id"),
                                new Account(rs.getInt(11),
                                        rs.getString(12),
                                        rs.getString(13)),
                                new Account(rs.getInt(14),
                                        rs.getString(15),
                                        rs.getString(16)),
                                rs.getTimestamp("sent"),
                                rs.getString("subject"),
                                rs.getString("message_text"),
                                rs.getInt("send_status"),
                                rs.getBoolean("read_status"),
                                rs.getBoolean("starred"),
                                rs.getBoolean("important")
                        );
                        messages.add(message);
                    }
                    return messages;
                };

/**
 * {@link org.springframework.data.repository.support.PageableExecutionUtils}
 * {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} 331 row
 */
        List<Message> userMessages =
                jdbcTemplate.query(getCreator(pageable),
                        getSetter(pageable, id),
                        resultSetExtractor);
        if (userMessages == null)
            userMessages = Collections.emptyList();

        return PageableExecutionUtils.getPage(userMessages, pageable, () -> count(id));
    }

    private PreparedStatementCreator getCreator(Pageable pageable) {
        if (!pageable.isPaged()) {
            return connection -> connection
                    .prepareStatement("SELECT * FROM message m " +
                            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                            "INNER JOIN user toUser on m.to_user = toUser.id " +
                            "WHERE fromUser.id = ? ");
        } else {
            return connection -> connection
                    .prepareStatement("SELECT * FROM message m " +
                            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                            "INNER JOIN user toUser on m.to_user = toUser.id " +
                            "WHERE fromUser.id = ? LIMIT ?, ?");
        }
    }

    private PreparedStatementSetter getSetter(Pageable pageable, Long id) {
        return preparedStatement -> {
            preparedStatement.setLong(1, id);
            if (pageable.isPaged()) {
                preparedStatement.setLong(2, pageable.getOffset());
                preparedStatement.setLong(3, pageable.getPageSize());
            }
        };
    }
}
