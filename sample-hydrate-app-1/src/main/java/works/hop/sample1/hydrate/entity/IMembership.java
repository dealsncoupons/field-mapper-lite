// This interface is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.hydrate.jdbc.context.Hydrate;

public interface IMembership extends Hydrate {
  UUID getId();

  LocalDate getDateCreated();

  LocalDateTime getLastUpdated();

  UUID getMember();

  UUID getClub();

  String getAlias();

  String getStatus();
}
