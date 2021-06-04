// This interface is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.upside.context.Hydrate;
import works.hop.upside.relations.EntityInfo;

public interface IAssignment extends Hydrate {
  UUID getId();

  Task getTask();

  User getAssignee();

  LocalDateTime getDateAssigned();

  EntityInfo initEntityInfo();
}
