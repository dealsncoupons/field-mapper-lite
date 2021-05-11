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
    public List<ITodo> getSubTasks() {
        return this.subTasks;
    }

    @Override
    public void setSubTasks(List<ITodo> subTasks) {
        this.subTasks = subTasks;
    }
}
