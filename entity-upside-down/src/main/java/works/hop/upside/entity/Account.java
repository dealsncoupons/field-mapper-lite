package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrant;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.LocalCache;
import works.hop.upside.entity.contract.IAccount;
import works.hop.upside.relations.EntityQuery;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class Account implements IAccount {

    private static final Logger log = LoggerFactory.getLogger(Account.class);

    private UUID id;
    private String username;
    private String password;
    private User user;
    private Date dateCreated;

    public Account() {
        super();
    }

    public Account(UUID id, String username, String password, User user) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.user = user;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public Account select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        synchronized (this) {
            try {
                this.id = rs.getObject("id", UUID.class);
                if (cache.get(this.id, "tbl_account").isPresent()) {
                    return (Account) cache.get(this.id, "tbl_account").get();
                } else {
                    cache.add(this.id, this, "tbl_account");
                    this.username = rs.getString("user_name");
                    this.password = rs.getString("access_code");
                    UUID userId = rs.getObject("user_id", UUID.class);
                    this.user = resolver.selectByIdColumn("tbl_user", "id", userId, User.class, connection);
                    this.dateCreated = rs.getDate("date_created");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot resolve a property", e);
            }
        }
        return this;
    }

    @Override
    public Account insert(Connection connection) {
        if (this.user != null) {
            this.user = InsertTemplate.insertOne(this.user, connection);
        }
        String query = "insert into tbl_account (user_name, access_code, user_id) " +
                "values (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.username);
            ps.setString(2, this.password);
            ps.setObject(3, this.user.getId());

            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after insert operation", rowsAffected);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    this.id = UUID.fromString(keys.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn("Could not retrieve generated id value", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing insert query", e);
        }

        return this;
    }

    @Override
    public <E extends Hydrant> E update(Map<String, Object> columnValues, Connection connection) {
        if (columnValues.containsKey("username")) {
            this.username = (String) columnValues.get("username");
        }
        if (columnValues.containsKey("password")) {
            this.password = (String) columnValues.get("password");
        }
        if (columnValues.containsKey("user")) {
            this.user = (User) columnValues.get("user");
        }

        String query = EntityQuery.getInstance().updateOne("tbl_account", new String[]{"id"}, new String[]{"username", "password", "user_id"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.username);
            ps.setString(2, this.password);
            ps.setObject(3, this.user != null ? this.user.getId() : null);
            ps.setObject(4, this.getId());

            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after update operation", rowsAffected);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    this.id = UUID.fromString(keys.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn("Could not retrieve generated id value", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing update query", e);
        }

        return (E) this;
    }

    @Override
    public <E extends Hydrant> E delete(Connection connection) {
        String query = EntityQuery.getInstance().deleteOne("tbl_account", new String[]{"id"});
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, this.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing delete query", e);
        }
        return (E) this;
    }

    @Override
    public <E extends Hydrant> E refresh(Struct record) {
        this.username = record.getString("user_name");
        this.password = record.getString("access_code");
        this.dateCreated = new Date(record.getInt64("date_created"));
        return (E) this;
    }
}
