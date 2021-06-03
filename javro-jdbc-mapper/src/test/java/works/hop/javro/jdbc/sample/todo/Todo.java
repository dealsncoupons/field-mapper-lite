package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table("tbl_task")
public class Todo implements ITodo {

    UUID id;
    String name;
    Boolean completed;
    ITodo nextTask;
    ITodo parentTask;
    List<ITodo> subTasks = new ArrayList<>();

    public Todo() {
        this(null, null, null);
    }

    public Todo(String name, Boolean completed) {
        this(null, name, completed);
    }

    public Todo(UUID id, String name, Boolean completed) {
        this.id = id;
        this.name = name;
        this.completed = completed;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean getCompleted() {
        return this.completed;
    }

    @Override
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public ITodo getNextTask() {
        return this.nextTask;
    }

    @Override
    public void setNextTask(ITodo nextTask) {
        this.nextTask = nextTask;
    }

    @Override
    public ITodo getParentTask() {
        return parentTask;
    }

    @Override
    public void setParentTask(ITodo parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public List<ITodo> getSubTasks() {
        return this.subTasks;
    }

    @Override
    public void setSubTasks(List<ITodo> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public <O> O get(String property) {
        switch (property) {
            case "id":
                return (O) source().getId();
            case "name":
                return (O) source().getName();
            case "completed":
                return (O) source().getCompleted();
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

    @Override
    public <O> void set(String property, O value) {
        switch (property) {
            case "id":
                source().setId((UUID) value);
                break;
            case "name":
                source().setName((String) value);
                break;
            case "completed":
                source().setCompleted((Boolean) value);
                break;
            case "nextTask":
                source().setNextTask((ITodo) value);
                break;
            case "parentTask":
                source().setParentTask((ITodo) value);
                break;
            case "subTasks":
                source().setSubTasks((List<ITodo>) value);
                break;
            default:
                break;
        }
    }

    @Override
    public ITodo source() {
        return this;
    }
}
