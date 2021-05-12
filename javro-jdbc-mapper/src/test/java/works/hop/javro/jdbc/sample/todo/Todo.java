package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.annotation.Table;

import java.util.ArrayList;
import java.util.UUID;

@Table("tbl_task")
public class Todo implements ITodo {

    UUID id;
    String name;
    Boolean completed;
    ITodo nextTask;
    ArrayList<ITodo> subTasks = new ArrayList<>();

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
    public ArrayList<ITodo> getSubTasks() {
        return this.subTasks;
    }

    @Override
    public void setSubTasks(ArrayList<ITodo> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public <O> O get(String property) {
        switch (property) {
            case "id":
                return (O) ref().getId();
            case "name":
                return (O) ref().getName();
            case "completed":
                return (O) ref().getCompleted();
            case "nextTask":
                return (O) ref().getNextTask();
            case "subTasks":
                return (O) ref().getSubTasks();
            default:
                return null;
        }
    }

    @Override
    public <O> void set(String property, O value) {
        switch (property) {
            case "id":
                ref().setId((UUID) value);
            case "name":
                ref().setName((String) value);
            case "completed":
                ref().setCompleted((Boolean) value);
            case "nextTask":
                ref().setNextTask((ITodo) value);
            case "subTasks":
                ref().setSubTasks((ArrayList<ITodo>) value);
            default:
                break;
        }
    }

    @Override
    public ITodo ref() {
        return this;
    }
}
