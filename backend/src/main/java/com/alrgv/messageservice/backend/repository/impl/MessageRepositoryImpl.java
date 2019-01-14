package com.alrgv.messageservice.backend.repository.impl;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.repository.MessageRepository;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rogovoy Alexandr
 * @see com.alrgv.messageservice.backend.repository.MessageRepository
 * <p>
 * JDBC implementation of MessageRepository
 */
@Repository("messageRepository")
public class MessageRepositoryImpl implements MessageRepository {

    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryImpl.class);

    private Connection connection;
    private PreparedStatementCreator updatePreparedStatementCreator,
            insertPreparedStatementCreator, countPreparedStatementCreator,
            findPreparedStatementCreator, deletePreparedStatementCreator;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();

        this.updatePreparedStatementCreator = con -> con.prepareStatement("UPDATE message m " +
                "SET from_user = ?, to_user = ?, sent = ?, subject = ?, " +
                "message_text = ?, send_status = ?, read_status = ?, " +
                "starred = ?, important = ? " +
                "WHERE m.id = ?");
        this.insertPreparedStatementCreator = con -> con.prepareStatement(
                "INSERT INTO message (from_user, to_user, sent, subject, message_text, send_status, " +
                        "read_status, starred, important) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        this.countPreparedStatementCreator = con -> con.prepareStatement(
                "SELECT COUNT(*) FROM message m " +
                        "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                        "WHERE fromUser.id = ?");
        this.findPreparedStatementCreator = con -> con.prepareStatement(
                "SELECT * FROM message m " +
                        "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                        "INNER JOIN user toUser on m.to_user = toUser.id " +
                        "WHERE fromUser.id = ? AND m.important in (?, ?) AND m.starred in (?, ?) LIMIT ? OFFSET ?");
        this.deletePreparedStatementCreator = con -> con.prepareStatement(
                "DELETE m FROM message m WHERE m.id = ?"
        );
    }

    @Override
    public long count(long id) {
        try {
            PreparedStatement countStatement =
                    countPreparedStatementCreator.createPreparedStatement(connection);
            countStatement.setLong(1, id);
            ResultSet rs = countStatement.executeQuery();

            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            log.error("SQL state: {}, error code: {}. Error: {}",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * {@link org.springframework.data.repository.support.PageableExecutionUtils}
     * {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} 331 row
     */
    @Override
    public Page<Message> findMessagesByUserIdAndParams(@NonNull Pageable pageable,
                                                       @NonNull Long id,
                                                       @NonNull List<Object> importantParams,
                                                       @NonNull List<Object> starredParams) {
        if (!pageable.isPaged())
            return null;
        Assertions.assertThat(importantParams.size()).isEqualTo(2);
        Assertions.assertThat(starredParams.size()).isEqualTo(2);

        List<Message> userMessages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    findPreparedStatementCreator.createPreparedStatement(connection);
            preparedStatement.setLong(1, id);
            preparedStatement.setObject(2, importantParams.get(0));
            preparedStatement.setObject(3, importantParams.get(1));
            preparedStatement.setObject(4, starredParams.get(0));
            preparedStatement.setObject(5, starredParams.get(1));
            preparedStatement.setInt(6, pageable.getPageSize());
            preparedStatement.setObject(7, pageable.getOffset());

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                        rs.getLong(1),
                        new Account(rs.getInt(11),
                                rs.getString(12),
                                rs.getString(13)),
                        new Account(rs.getInt(14),
                                rs.getString(15),
                                rs.getString(16)),
                        rs.getTimestamp(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getBoolean(8),
                        rs.getBoolean(9),
                        rs.getBoolean(10));
                userMessages.add(message);
            }
        } catch (SQLException e) {
            log.error("Possibly, can't find messages with method params: {}, {}, {}, {} OR read this: {} WHERE sql state = {}",
                    pageable, id, importantParams, starredParams, e.getMessage(), e.getSQLState());
            e.printStackTrace();
        }

        return PageableExecutionUtils.getPage(userMessages, pageable, () -> count(id));
    }

    @Override
    public <S extends Message> S save(S message, boolean isMerge) {
        PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setLong(1, message.getFrom().getId());
            preparedStatement.setLong(2, message.getTo().getId());
            preparedStatement.setTimestamp(3, message.getSent());
            preparedStatement.setString(4, message.getSubject());
            preparedStatement.setString(5, message.getMessageText());
            preparedStatement.setInt(6, message.getSendStatus());
            preparedStatement.setBoolean(7, message.isReadStatus());
            preparedStatement.setBoolean(8, message.isStarred());
            preparedStatement.setBoolean(9, message.isImportant());
            if (!isMerge)
                preparedStatement.setLong(10, message.getId());
        };

        try {
            PreparedStatement preparedStatement;
            if (isMerge) {
                preparedStatement = insertPreparedStatementCreator.createPreparedStatement(connection);
                setter.setValues(preparedStatement);

                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    message.setId(resultSet.getLong(1));
                    return message;
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            } else {
                preparedStatement = updatePreparedStatementCreator.createPreparedStatement(connection);
                setter.setValues(preparedStatement);

                preparedStatement.executeUpdate();
                return message;
            }
        } catch (SQLException e) {
            log.error("SQL state: {}, error code: {}. Error: {}",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
            e.printStackTrace();
            message.setId(-1L);
            throw new RuntimeException();
        }
    }

    //add response entity with a representation of the status of the action (200)
    @Override
    public int delete(long id) throws RuntimeException {
        try {
            PreparedStatement preparedStatement =
                    deletePreparedStatementCreator.createPreparedStatement(connection);
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL state: {}, error code: {}. Error: {}",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public int deleteAll(Iterable<? extends Message> messages) throws RuntimeException {
        PreparedStatement preparedStatement = null;
        int count = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = deletePreparedStatementCreator.createPreparedStatement(connection);

            int size = 0;
            for (Message message : messages) {
                preparedStatement.setLong(1, message.getId());
                preparedStatement.addBatch();
                size++;
            }
            if (size == 0)
                return 0;

            int[] successSqlEx = preparedStatement.executeBatch();
            for (int i : successSqlEx) {
                count += i;
            }
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("SQL state: {}, error code: {}. Error: {}",
                    e.getSQLState(), e.getErrorCode(), e.getErrorCode(), e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("SQL state: {}, error code: {}. Error: {}",
                        e.getSQLState(), e.getErrorCode(), e.getErrorCode(), e.getMessage());
                e1.printStackTrace();
                throw new RuntimeException();
            }
            throw new RuntimeException();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("SQL state: {}, error code: {}. Error: {}",
                        e.getSQLState(), e.getErrorCode(), e.getErrorCode(), e.getMessage());
                e.printStackTrace();
            }

            try {
                if (preparedStatement != null && !preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error("SQL state: {}, error code: {}. Error: {}",
                        e.getSQLState(), e.getErrorCode(), e.getErrorCode(), e.getMessage());
                e.printStackTrace();
            }
        }
        return count;
    }
}
