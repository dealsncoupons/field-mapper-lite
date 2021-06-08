// This interface is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity;

import java.lang.String;
import java.time.LocalDate;
import java.util.UUID;
import works.hop.hydrate.jdbc.context.Hydrate;

public interface IUser extends Hydrate {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getEmailAddress();

  Address getAddress();

  LocalDate getDateCreated();
}