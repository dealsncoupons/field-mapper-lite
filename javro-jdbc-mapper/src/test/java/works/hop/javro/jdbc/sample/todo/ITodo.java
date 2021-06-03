package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.sample.Unreflect;

import java.util.List;
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

    ITodo getParentTask();

    void setParentTask(ITodo parentTask);

    List<ITodo> getSubTasks();

    void setSubTasks(List<ITodo> subTasks);
}
