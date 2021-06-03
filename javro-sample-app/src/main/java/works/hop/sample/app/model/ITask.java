// This entity interface is AUTO-GENERATED, so there's no point of modifying it
package works.hop.sample.app.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ITask extends Unreflect {
    UUID getId();

    void setId(UUID id);

    String getName();

    void setName(String name);

    Boolean getCompleted();

    void setCompleted(Boolean completed);

    LocalDate getDateCreated();

    void setDateCreated(LocalDate dateCreated);

    UUID getNextTask();

    void setNextTask(UUID nextTask);

    UUID getParentTask();

    void setParentTask(UUID parentTask);

    List<Task> getSubTasks();

    void setSubTasks(List<Task> subTasks);
}
