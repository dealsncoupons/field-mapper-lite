package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.sample.Unreflect;

import java.util.ArrayList;
import java.util.UUID;

public interface ITodo extends Unreflect {

    UUID getId();

    void setId(UUID id);

    String getName();

    void setName(String name);

    Boolean getCompleted();

    void setCompleted(Boolean completed);

    ITodo getNextTask();

    void setNextTask(ITodo nextTask);

    ArrayList<ITodo> getSubTasks();

    void setSubTasks(ArrayList<ITodo> subTasks);
}
