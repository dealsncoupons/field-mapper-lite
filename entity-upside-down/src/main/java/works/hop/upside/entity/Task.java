package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrant;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.LocalCache;
import works.hop.upside.entity.contract.ITask;
import works.hop.upside.relations.EntityQuery;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Task implements ITask {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private UUID id;
    private String name;
    private Boolean completed;
    private Task nextTask;
    private Task dependsOn;
    private Date dateCreated;
    private Collection<Task> subTasks;

    public Task() {
        super();
    }

    public Task(UUID id, String name, Boolean completed, Task nextTask, Task dependsOn, Collection<Task> subTasks) {
        this.id = id;
        this.name = name;
        this.completed = completed;
        this.nextTask = nextTask;
        this.dependsOn = dependsOn;
        this.subTasks = subTasks;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean getCompleted() {
        return completed;
    }

    @Override
    public Task getNextTask() {
        return nextTask;
    }

    @Override
    public Task getDependsOn() {
        return dependsOn;
    }

    @Override
    public java.util.Date getDateCreated() {
        return this.dateCreated;
    }

    @Override
    public Collection<Task> getSubTasks() {
        return subTasks;
    }

    @Override
    public Task select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache) {
        synchronized (this) {
            try {
                this.id = rs.getObject("id", UUID.class);
                if (cache.get(this.id, "tbl_task").isPresent()) {
                    return (Task) cache.get(this.id, "tbl_task").get();
                } else {
                    cache.add(this.id, this, "tbl_task");
                    this.name = rs.getString("name");
                    this.completed = rs.getBoolean("done");
                    this.dateCreated = rs.getDate("date_created");
                    if (rs.getObject("next_task", UUID.class) != null) {
                        UUID nextTaskId = rs.getObject("next_task", UUID.class);
                        this.nextTask = resolver.selectByIdColumn("tbl_task", "id", nextTaskId, Task.class, connection);
                    }
                    if (rs.getObject("parent_task", UUID.class) != null) {
                        UUID parentTaskId = rs.getObject("parent_task", UUID.class);
                        this.dependsOn = resolver.selectByIdColumn("tbl_task", "id", parentTaskId, Task.class, connection);
                    }
                    this.subTasks = resolver.selectByJoinColumn("tbl_task", "id", "tbl_task", "parent_task", "parent_task", this.id, Task.class, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot resolve a property", e);
            }
        }
        return this;
    }

    @Override
    public Task insert(Connection connection) {
        if (this.nextTask != null) {
            this.nextTask = InsertTemplate.insertOne(this.nextTask, connection);
        }
        if (this.dependsOn != null) {
            this.dependsOn = InsertTemplate.insertOne(this.dependsOn, connection);
        }
        if (!this.subTasks.isEmpty()) {
            this.subTasks = this.subTasks.stream().map(task -> InsertTemplate.insertOne(task, connection)).collect(Collectors.toList());
        }

        String query = EntityQuery.getInstance().insertOne("tbl_task", new String[]{"name", "done", "next_task", "parent_task"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.name);
            ps.setBoolean(2, this.completed);
            ps.setObject(3, this.nextTask != null ? this.nextTask.getId() : null);
            ps.setObject(4, this.dependsOn != null ? this.dependsOn.getId() : null);

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
        if (columnValues.containsKey("name")) {
            this.name = (String) columnValues.get("name");
        }
        if (columnValues.containsKey("completed")) {
            this.completed = (Boolean) columnValues.get("completed");
        }
        if (columnValues.containsKey("nextTask")) {
            this.nextTask = (Task) columnValues.get("nextTask");
        }
        if (columnValues.containsKey("dependsOn")) {
            this.dependsOn = (Task) columnValues.get("dependsOn");
        }
        String query = EntityQuery.getInstance().updateOne("tbl_task", new String[]{"id"}, new String[]{"name", "done", "next_task", "parent_task"});
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.name);
            ps.setBoolean(2, this.completed);
            ps.setObject(3, this.nextTask != null ? this.nextTask.getId() : null);
            ps.setObject(4, this.dependsOn != null ? this.dependsOn.getId() : null);
            ps.setObject(5, this.getId());

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
        String query = EntityQuery.getInstance().deleteOne("tbl_task", new String[]{"id"});
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
        this.name = record.getString("name");
        this.completed = record.getBoolean("done");
        this.dateCreated = new Date(record.getInt64("date_created"));
        return (E) this;
    }
}
