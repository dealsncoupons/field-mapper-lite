package works.hop.upside.entity.contract;

import works.hop.upside.context.Hydrant;
import works.hop.upside.entity.Assignment;
import works.hop.upside.entity.User;

import java.util.Collection;

public interface IAssignee extends Hydrant {
    User getUser();

    Collection<Assignment> getAssignments();
}
