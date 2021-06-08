// This interface is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.hydrate.jdbc.context.Hydrate;

public interface IAssignment extends Hydrate {
  UUID getId();

  Task getTask();

  User getAssignee();

  LocalDateTime getDateAssigned();
}
