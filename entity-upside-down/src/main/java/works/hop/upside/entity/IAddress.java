// This interface is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

import java.lang.String;
import works.hop.upside.context.Hydrate;
import works.hop.upside.relations.EntityInfo;

public interface IAddress extends Hydrate {
  String getCity();

  String getState();

  String getZipCode();

  EntityInfo initEntityInfo();
}
