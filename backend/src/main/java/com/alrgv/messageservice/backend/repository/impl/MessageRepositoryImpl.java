package com.alrgv.messageservice.backend.repository.impl;

import com.alrgv.messageservice.backend.entity.Account;
import com.alrgv.messageservice.backend.entity.Message;
import com.alrgv.messageservice.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private static final String SQL_SELECT_BY_ID_UN_PAGEABLE = "SELECT * FROM message m " +
            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
            "INNER JOIN user toUser on m.to_user = toUser.id " +
            "WHERE fromUser.id = :uId";

    private static final String SQL_SELECT_BY_ID_PAGEABLE = "SELECT * FROM message m " +
            "INNER JOIN user fromUser on m.from_user = fromUser.id " +
            "INNER JOIN user toUser on m.to_user = toUser.id " +
            "WHERE fromUser.id = :uId LIMIT :off, :cnt";

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private MappingSqlQueryImpl findByIdPageable, findByIdUnPageable;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.findByIdPageable = new MappingSqlQueryImpl(dataSource,
                SQL_SELECT_BY_ID_PAGEABLE,
                new SqlParameter("uId", Types.BIGINT),
                new SqlParameter("off", Types.BIGINT),
                new SqlParameter("cnt", Types.BIGINT));

        this.findByIdUnPageable = new MappingSqlQueryImpl(dataSource,
                SQL_SELECT_BY_ID_UN_PAGEABLE,
                new SqlParameter("uId", Types.BIGINT));
    }

    @Override
    public Long count(@NonNull Long id) {
        try {
            PreparedStatement countStatement =
                    dataSource
                            .getConnection()
                            .prepareStatement("SELECT COUNT(*) FROM message m " +
                                    "INNER JOIN user fromUser on m.from_user = fromUser.id " +
                                    "WHERE fromUser.id = ?");
            countStatement.setLong(1, id);
            ResultSet rs = countStatement.executeQuery();
            return rs.getLong(1);
        } catch (SQLException e) {
            return 0L;
        }
    }

    /**
     * {@link org.springframework.data.repository.support.PageableExecutionUtils}
     * {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} 331 row
     */
    @Override
    public Page<Message> findMessagesByUserId(@NonNull Pageable pageable, @NonNull Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("uId", id);
        if (pageable.isPaged()) {
            params.put("off", pageable.getOffset());
            params.put("cnt", pageable.getPageSize());
        }

        MappingSqlQueryImpl query = pageable.isPaged() ? findByIdPageable : findByIdUnPageable;
        List<Message> userMessages = query.executeByNamedParam(params);

        return PageableExecutionUtils.getPage(userMessages, pageable, () -> count(id));
    }

    private class MappingSqlQueryImpl extends MappingSqlQuery<Message> {

        MappingSqlQueryImpl(DataSource ds, String sql) {
            setDataSource(ds);
            setSql(sql);
            afterPropertiesSet();
        }

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
