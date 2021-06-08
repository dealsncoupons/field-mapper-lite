// This interface is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity;

import java.lang.Boolean;
import java.lang.String;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import works.hop.hydrate.jdbc.context.Hydrate;

public interface ITask extends Hydrate {
  UUID getId();

  String getName();

  Boolean getCompleted();

  LocalDate getDateCreated();

  Task getNextTask();

  Task getDependsOn();

  Collection<Task> getSubTasks();
}
