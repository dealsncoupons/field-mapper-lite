package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import works.hop.javro.jdbc.annotation.Metadata;
import works.hop.upside.context.*;
import works.hop.upside.relations.EntityInfo;
import works.hop.upside.relations.FieldInfo;
import works.hop.upside.relations.FieldInfoBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Assignee implements IAssignee {

    @Metadata
    private final EntityInfo entityInfo;
    private User user;
    private Collection<Assignment> assignments;

    public Assignee() {
        this.entityInfo = initEntityInfo();
    }

    public Assignee(User user, Collection<Assignment> assignments) {
        this.user = user;
        this.assignments = assignments;
        this.entityInfo = initEntityInfo();
    }

    @Override
    public EntityInfo initEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_assignment");
        FieldInfo user = FieldInfoBuilder.builder().relational(true).name("user").columnName("assignee_id").type(User.class).build();
        FieldInfo tasks = FieldInfoBuilder.builder().relational(true).collection(true).name("assignments").columnName("task_id").type(Task.class).build();
        entityInfo.setFields(List.of(user, tasks));
        return entityInfo;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Collection<Assignment> getAssignments() {
        return assignments;
    }

    public EntityInfo getEntityInfo() {
        return this.entityInfo;
    }

    @Override
    public <O> O get(String property) {
        if (property.equals("users")) {
            return (O) user;
        }
        if (property.equals("assignments")) {
            return (O) assignments;
        }
        return null;
    }

    @Override
    public <O> void set(String property, O value) {
        if (property.equals("users")) {
            this.user = (User) value;
        }
        if (property.equals("assignments")) {
            this.assignments = (Collection<Assignment>) value;
        }
    }

    @Override
    public <E extends Hydrate> E select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        UUID id = user.getId();
        this.user = resolver.selectByIdColumn(new User(), "tbl_user", "id", id, connection);
        this.assignments = resolver.selectByJoinColumn(Assignment::new, "tbl_assignment", "task_id", "tbl_task", "id", "user_id", id, connection);
        return (E) this;
    }

    @Override
    public <E extends Hydrate> E insert(Connection connection) {
        if (this.user != null) {
            this.user = InsertTemplate.insertOne(this.user, connection);
        }
        if (!this.assignments.isEmpty()) {
            this.assignments = this.assignments.stream().map(task -> InsertTemplate.insertOne(task, connection)).collect(Collectors.toList());
        }
        return (E) this;
    }

    @Override
    public <E extends Hydrate> E update(Map<String, Object> columnValues, Connection connection) {
        if (columnValues.containsKey("user")) {
            this.user = UpdateTemplate.updateOne(this.user, columnValues, connection);
        }
        if (columnValues.containsKey("assignments")) {
            this.assignments = this.assignments.stream().map(task -> UpdateTemplate.updateOne(task, columnValues, connection)).collect(Collectors.toList());
        }

        return (E) this;
    }

    @Override
    public <E extends Hydrate> E delete(Connection connection) {
        this.assignments.forEach(task -> DeleteTemplate.deleteOne(task, connection));
        return (E) this;
    }

    @Override
    public <E extends Hydrate> E refresh(Struct record) {
        return (E) this;
    }
}
