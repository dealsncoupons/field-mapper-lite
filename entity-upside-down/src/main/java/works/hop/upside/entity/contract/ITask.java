package works.hop.upside.entity.contract;

import works.hop.upside.context.Hydrant;
import works.hop.upside.entity.Task;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface ITask extends Hydrant {
    UUID getId();

    String getName();

    Boolean getCompleted();

    Task getNextTask();

    Task getDependsOn();

    Date getDateCreated();

    Collection<Task> getSubTasks();
}
