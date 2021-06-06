// This entity class is AUTO-GENERATED, so there's no point of modifying it
package works.hop.sample.app.model;

import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.annotation.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Table("tbl_task")
public class Task implements ITask {
    @Id
    public UUID id;

    public String name;

    public Boolean completed;

    @Column("date_created")
    public LocalDate dateCreated;

    @Column("next_task")
    public UUID nextTask;

    @Column("parent_task")
    public UUID parentTask;

    @JoinColumn("parent_task")
    public List<Task> subTasks;

    public Task() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public UUID getNextTask() {
        return this.nextTask;
    }

    public void setNextTask(UUID nextTask) {
        this.nextTask = nextTask;
    }

    public UUID getParentTask() {
        return this.parentTask;
    }

    public void setParentTask(UUID parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public ITask source() {
        return this;
    }

    public <O> O get(String property) {
        switch (property) {
            case "id":
                return (O) source().getId();
            case "name":
                return (O) source().getName();
            case "completed":
                return (O) source().getCompleted();
            case "dateCreated":
                return (O) source().getDateCreated();
            case "nextTask":
                return (O) source().getNextTask();
            case "parentTask":
                return (O) source().getParentTask();
            case "subTasks":
                return (O) source().getSubTasks();
            default:
                return null;
        }
    }

    public <O> void set(String property, O value) {
        switch (property) {
            case "id":
                source().setId((java.util.UUID) value);
                break;
            case "name":
                source().setName((java.lang.String) value);
                break;
            case "completed":
                source().setCompleted((java.lang.Boolean) value);
                break;
            case "dateCreated":
                source().setDateCreated((java.time.LocalDate) value);
                break;
            case "nextTask":
                source().setNextTask((java.util.UUID) value);
                break;
            case "parentTask":
                source().setParentTask((java.util.UUID) value);
                break;
            case "subTasks":
                source().setSubTasks((java.util.List<works.hop.sample.app.model.Task>) value);
                break;
            default:
                break;
        }
    }
}
