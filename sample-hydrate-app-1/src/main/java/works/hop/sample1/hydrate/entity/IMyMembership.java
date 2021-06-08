// This interface is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import works.hop.hydrate.jdbc.context.Hydrate;

public interface IMyMembership extends Hydrate {
  UUID getId();

  LocalDate getDateCreated();

  LocalDateTime getLastUpdated();

  Member getMember();

  Collection<Club> getClub();

  String getAlias();

  String getStatus();
}
