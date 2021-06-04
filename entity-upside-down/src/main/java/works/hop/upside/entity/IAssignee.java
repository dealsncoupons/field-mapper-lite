package works.hop.upside.entity;

import works.hop.upside.context.Hydrate;
import works.hop.upside.relations.EntityInfo;

import java.util.Collection;

public interface IAssignee extends Hydrate {

    User getUser();

    Collection<Assignment> getAssignments();

    EntityInfo initEntityInfo();

    EntityInfo getEntityInfo();
}
