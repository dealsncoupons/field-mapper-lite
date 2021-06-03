package works.hop.upside.entity.contract;

import works.hop.upside.context.Hydrant;
import works.hop.upside.entity.Task;
import works.hop.upside.entity.User;

import java.util.Date;
import java.util.UUID;

public interface IAssignment extends Hydrant {
    UUID getId();

    Task getTask();

    User getAssignee();

    Date getDateAssigned();
}
