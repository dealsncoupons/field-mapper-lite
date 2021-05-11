package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.sample.Accessible;

import java.util.List;
import java.util.UUID;

public interface ITodo extends Accessible {

    UUID getId();

    void setId(UUID id);

    String getName();

    void setName(String name);

    Boolean getCompleted();

    void setCompleted(Boolean completed);

    List<ITodo> getSubTasks();

    void setSubTasks(List<ITodo> subTasks);
}
