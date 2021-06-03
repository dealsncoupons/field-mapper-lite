package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrant;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.LocalCache;
import works.hop.upside.entity.contract.IAssignment;
import works.hop.upside.relations.EntityQuery;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Assignment implements IAssignment {

    private static final Logger log = LoggerFactory.getLogger(Assignment.class);

    private UUID id;
    private Task task;
    private User assignee;
    private Date dateAssigned;

    public Assignment() {
        super();
    }

    public Assignment(UUID id, Task task, User assignee, Date dateAssigned) {
        this.id = id;
        this.task = task;
        this.assignee = assignee;
        this.dateAssigned = dateAssigned;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public User getAssignee() {
        return assignee;
    }

    @Override
    public Date getDateAssigned() {
        return dateAssigned;
    }

    @Override
    public Assignment select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        synchronized (this) {
            try {
                this.id = rs.getObject("id", UUID.class);
                if (cache.get(this.id, "tbl_assignment").isPresent()) {
                    return (Assignment) cache.get(this.id, "tbl_assignment").get();
                } else {
                    cache.add(this.id, this, "tbl_assignment");
                    this.dateAssigned = rs.getDate("date_assigned");
                    if (rs.getObject("task_id", UUID.class) != null) {
                        UUID taskId = rs.getObject("task_id", UUID.class);
                        this.task = resolver.selectByIdColumn("tbl_task", "id", taskId, Task.class, connection);
                    }
                    if (rs.getObject("assignee_id", UUID.class) != null) {
                        UUID assigneeId = rs.getObject("assignee_id", UUID.class);
                        Optional<Hydrant> cached = cache.get(assigneeId, "tbl_assignment");
                        this.assignee = resolver.selectByIdColumn("tbl_user", "id", assigneeId, User.class, connection);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot resolve a property", e);
            }
        }
        return this;
    }

    @Override
    public Assignment insert(Connection connection) {
        if (this.task != null) {
            this.task = InsertTemplate.insertOne(this.task, connection);
        }
        if (this.assignee != null) {
            this.assignee = InsertTemplate.insertOne(this.assignee, connection);
        }

        String query = EntityQuery.getInstance().insertOne("tbl_assignment", new String[]{"task_id", "assignee_id"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (this.task != null) {
                ps.setObject(1, this.task.getId());
            }
            if (this.assignee != null) {
                ps.setObject(2, this.assignee.getId());
            }

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
        if (columnValues.containsKey("task")) {
            this.task = (Task) columnValues.get("task");
        }
        if (columnValues.containsKey("assignee")) {
            this.assignee = (User) columnValues.get("assignee");
        }
        if (columnValues.containsKey("dateAssigned")) {
            this.dateAssigned = (Date) columnValues.get("dateAssigned");
        }

        String query = EntityQuery.getInstance().updateOne("tbl_assignment", new String[]{"id"}, new String[]{"task_id", "assignee_id", "date_assigned"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, this.task != null ? this.task.getId() : null);
            ps.setObject(2, this.assignee != null ? this.assignee.getId() : null);
            ps.setObject(3, this.dateAssigned);
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
        String query = EntityQuery.getInstance().deleteOne("tbl_assignment", new String[]{"id"});
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
        this.dateAssigned = new Date(record.getInt64("date_created"));
        return (E) this;
    }
}
