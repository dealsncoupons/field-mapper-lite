// This interface is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

import java.lang.String;
import java.time.LocalDate;
import java.util.UUID;
import works.hop.upside.context.Hydrate;
import works.hop.upside.relations.EntityInfo;

public interface IUser extends Hydrate {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getEmailAddress();

  Address getAddress();

  LocalDate getDateCreated();

  EntityInfo initEntityInfo();
}
