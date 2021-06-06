// This entity class is AUTO-GENERATED, so there's no point of modifying it
package works.hop.sample.app.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TaskTO implements ITaskTO {
    public UUID id;

    public String name;

    public Boolean completed;

    public LocalDate dateCreated;

    public Task nextTask;

    public Task parentTask;

    public List<Task> subTasks;

    public TaskTO() {
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

    public Task getNextTask() {
        return this.nextTask;
    }

    public void setNextTask(Task nextTask) {
        this.nextTask = nextTask;
    }

    public Task getParentTask() {
        return this.parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public ITaskTO source() {
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
                source().setNextTask((works.hop.sample.app.model.Task) value);
                break;
            case "parentTask":
                source().setParentTask((works.hop.sample.app.model.Task) value);
                break;
            case "subTasks":
                source().setSubTasks((java.util.List<works.hop.sample.app.model.Task>) value);
                break;
            default:
                break;
        }
    }
}
