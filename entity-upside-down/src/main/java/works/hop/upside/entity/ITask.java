// This interface is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

import java.lang.Boolean;
import java.lang.String;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import works.hop.upside.context.Hydrate;
import works.hop.upside.relations.EntityInfo;

public interface ITask extends Hydrate {
  UUID getId();

  String getName();

  Boolean getCompleted();

  LocalDate getDateCreated();

  Task getNextTask();

  Task getDependsOn();

  Collection<Task> getSubTasks();

  EntityInfo initEntityInfo();
}
