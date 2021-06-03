package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.*;
import works.hop.upside.entity.contract.IAssignee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Assignee implements IAssignee {

    private User user;
    private Collection<Assignment> assignments;

    public Assignee() {
        super();
    }

    public Assignee(User user, Collection<Assignment> assignments) {
        this.user = user;
        this.assignments = assignments;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Collection<Assignment> getAssignments() {
        return assignments;
    }

    @Override
    public Assignee select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        synchronized (this) {
            try {
                UUID userId = rs.getObject("user_id", UUID.class);
                this.user = resolver.selectByIdColumn("tbl_user", "id", userId, User.class, connection);
                this.assignments = resolver.selectByJoinColumn("tbl_assignment", "task_id", "tbl_task", "id", "user_id", userId, Assignment.class, connection);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot resolve a property", e);
            }
        }
        return this;
    }

    @Override
    public Assignee insert(Connection connection) {
        if (this.user != null) {
            this.user = InsertTemplate.insertOne(this.user, connection);
        }
        if (!this.assignments.isEmpty()) {
            this.assignments = this.assignments.stream().map(task -> InsertTemplate.insertOne(task, connection)).collect(Collectors.toList());
        }
        return this;
    }

    @Override
    public <E extends Hydrant> E update(Map<String, Object> columnValues, Connection connection) {
        if (columnValues.containsKey("user")) {
            this.user = UpdateTemplate.updateOne(this.user, columnValues, connection);
        }
        if (columnValues.containsKey("assignments")) {
            this.assignments = this.assignments.stream().map(task -> UpdateTemplate.updateOne(task, columnValues, connection)).collect(Collectors.toList());
        }

        return (E) this;
    }

    @Override
    public <E extends Hydrant> E delete(Connection connection) {
        this.assignments.forEach(task -> DeleteTemplate.deleteOne(task, connection));
        return (E) this;
    }

    @Override
    public <E extends Hydrant> E refresh(Struct record) {
        return (E) this;
    }
}
