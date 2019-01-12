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
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rogovoy Alexandr
 * @see com.alrgv.messageservice.backend.repository.MessageRepository
 * <p>
 * JDBC implementation of MessageRepository
 */
@Repository("messageRepository")
public class MessageRepositoryImpl implements MessageRepository {

    private static final String SQL_SELECT_BY_ID_UN_PAGEABLE = "SELECT * FROM message m " +
            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
            "INNER JOIN user toUser on m.to_user = toUser.id " +
            "WHERE fromUser.id = :uId AND m.important in (:ff, :sf) AND m.starred in (:fs, :ss)";

    private static final String SQL_SELECT_BY_ID_PAGEABLE = "SELECT * FROM message m " +
            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
            "INNER JOIN user toUser on m.to_user = toUser.id " +
            "WHERE fromUser.id = :uId AND m.important in (:ff, :sf) AND m.starred in (:fs, :ss) " +
            "LIMIT :off, :cnt";

    private Connection connection;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private MappingSqlQueryImpl findByIdPageable, findByIdUnPageable;
    private PreparedStatementCreator updatePreparedStatementCreator,
            insertPreparedStatementCreator, countPreparedStatementCreator;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.findByIdPageable = new MappingSqlQueryImpl(dataSource,
                SQL_SELECT_BY_ID_PAGEABLE,
                new SqlParameter("uId", Types.BIGINT),
                new SqlParameter("off", Types.BIGINT),
                new SqlParameter("cnt", Types.BIGINT),
                new SqlParameter("ff", Types.BOOLEAN),
                new SqlParameter("sf", Types.BOOLEAN),
                new SqlParameter("fs", Types.BOOLEAN),
                new SqlParameter("ss", Types.BOOLEAN));

        this.findByIdUnPageable = new MappingSqlQueryImpl(dataSource,
                SQL_SELECT_BY_ID_UN_PAGEABLE,
                new SqlParameter("uId", Types.BIGINT),
                new SqlParameter("ff", Types.BOOLEAN),
                new SqlParameter("sf", Types.BOOLEAN),
                new SqlParameter("fs", Types.BOOLEAN),
                new SqlParameter("ss", Types.BOOLEAN));

        this.updatePreparedStatementCreator = con -> con.prepareStatement("UPDATE message m " +
                "SET from_user = ? AND to_user = ? AND sent = ? AND subject = ? " +
                "AND message_text = ? AND send_status = ? AND read_status = ? " +
                "AND starred = ? AND important = ? " +
                "WHERE from_user = ?");
        this.insertPreparedStatementCreator = con -> con.prepareStatement(
                "INSERT INTO message (from_user, to_user, sent, subject, message_text, send_status, " +
                        "read_status, starred, important) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        this.countPreparedStatementCreator = con -> con.prepareStatement(
                "SELECT COUNT(*) FROM message m " +
                        "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                        "WHERE fromUser.id = ?");
    }

    @Override
    public Long count(@NonNull Long id) {
        try {
            PreparedStatement countStatement =
                    countPreparedStatementCreator.createPreparedStatement(connection);
            countStatement.setLong(1, id);
            ResultSet rs = countStatement.executeQuery();
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            return 0L;
        }
    }

    /**
     * {@link org.springframework.data.repository.support.PageableExecutionUtils}
     * {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} 331 row
     * <p>
     * If we have no needed data in pageable then use findByIdUnPageable, otherwise findByIdPageable
     */
    @Override
    public Page<Message> findMessagesByUserIdAndParams(@NonNull Pageable pageable,
                                                       @NonNull Long id,
                                                       @NonNull List<Object> importantParams,
                                                       @NonNull List<Object> starredParams) {
        Map<String, Object> params = new HashMap<>();
        params.put("uId", id);

        // f - first, s - second. For example, ff - first param from first 'in'
        params.put("ff", importantParams.get(0));
        params.put("sf", importantParams.get(1));
        params.put("fs", starredParams.get(0));
        params.put("ss", starredParams.get(1));

        if (pageable.isPaged()) {
            params.put("off", pageable.getOffset());
            params.put("cnt", pageable.getPageSize());
        }

        MappingSqlQuery<Message> query = pageable.isPaged() ? findByIdPageable : findByIdUnPageable;
        List<Message> userMessages = query.executeByNamedParam(params);

        return PageableExecutionUtils.getPage(userMessages, pageable, () -> count(id));
    }

    @Override
    public Message save(Message message, boolean isMerge) throws SQLException {
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

        if (isMerge) {
            PreparedStatement preparedStatement =
                    insertPreparedStatementCreator.createPreparedStatement(connection);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                message.setId(resultSet.getLong(1));
                return message;
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } else {
            jdbcTemplate.query(updatePreparedStatementCreator,
                    setter, resultSet -> resultSet.getLong(1));
            return message;
        }
    }

    private class MappingSqlQueryImpl extends MappingSqlQuery<Message> {

        MappingSqlQueryImpl(DataSource ds, String sql, SqlParameter... parameters) {
            setDataSource(ds);
            setSql(sql);
            setParameters(parameters);
            afterPropertiesSet();
        }

        @Override
        protected Message mapRow(ResultSet rs, int i) throws SQLException {
            return new Message(
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
        }
    }
}
