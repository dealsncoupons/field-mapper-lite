package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrant;
import works.hop.upside.context.LocalCache;
import works.hop.upside.entity.contract.IUser;
import works.hop.upside.relations.EntityQuery;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class User implements IUser {

    private static final Logger log = LoggerFactory.getLogger(User.class);

    private UUID id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private Address address;
    private Date dateCreated;

    public User() {
        super();
    }

    public User(UUID id, String firstName, String lastName, String emailAddress, Address address) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.address = address;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public java.util.Date getDateCreated() {
        return this.dateCreated;
    }

    @Override
    public User select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        synchronized (this) {
            try {
                this.id = rs.getObject("id", UUID.class);
                if (cache.get(this.id, "tbl_user").isPresent()) {
                    return (User) cache.get(this.id, "tbl_user").get();
                } else {
                    cache.add(this.id, this, "tbl_user");
                    this.firstName = rs.getString("first_name");
                    this.lastName = rs.getString("last_name");
                    this.emailAddress = rs.getString("email_address");
                    this.address = new Address().select(rs);
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
    public User insert(Connection connection) {
        String query = "insert into tbl_user (first_name, last_name, email_address, addr_city, addr_state_prov, addr_zip_code) " +
                "values (?, ?, ?, ?, ?, ?) on conflict (email_address) do nothing";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.firstName);
            ps.setString(2, this.lastName);
            ps.setString(3, this.emailAddress);
            ps.setString(4, this.address.getCity());
            ps.setString(5, this.address.getState());
            ps.setString(6, this.address.getZipCode());

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
        if (columnValues.containsKey("firstName")) {
            this.firstName = (String) columnValues.get("firstName");
        }
        if (columnValues.containsKey("lastName")) {
            this.lastName = (String) columnValues.get("lastName");
        }
        if (columnValues.containsKey("emailAddress")) {
            this.emailAddress = (String) columnValues.get("emailAddress");
        }
        if (columnValues.containsKey("address")) {
            this.address = (Address) columnValues.get("address");
        }

        String query = EntityQuery.getInstance().updateOne("tbl_user", new String[]{"id"}, new String[]{"first_name", "last_name", "email_address", "addr_city", "addr_state_prov", "addr_zip_code"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.firstName);
            ps.setString(2, this.lastName);
            ps.setString(3, this.emailAddress);
            ps.setObject(4, this.address.getCity());
            ps.setObject(5, this.address.getState());
            ps.setObject(6, this.address.getZipCode());
            ps.setObject(7, this.getId());

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
        String query = EntityQuery.getInstance().deleteOne("tbl_user", new String[]{"id"});
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
        this.firstName = record.getString("first_name");
        this.lastName = record.getString("last_name");
        this.emailAddress = record.getString("email_address");
        this.dateCreated = new Date(record.getInt64("date_created"));
        this.address = this.address.refresh(record);
        return (E) this;
    }
}
