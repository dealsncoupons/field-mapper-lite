package works.hop.upside.context;

import works.hop.upside.entity.*;
import works.hop.upside.entity.contract.*;

public class EntityInstance {

    public static <T extends Hydrant> T create(Class<?> type) {
        if (IUser.class.isAssignableFrom(type)) {
            return (T) new User();
        }
        if (IAccount.class.isAssignableFrom(type)) {
            return (T) new Account();
        }
        if (ITask.class.isAssignableFrom(type)) {
            return (T) new Task();
        }
        if (IAssignee.class.isAssignableFrom(type)) {
            return (T) new Assignee();
        }
        if (IAssignment.class.isAssignableFrom(type)) {
            return (T) new Assignment();
        }
        throw new RuntimeException("Unexpected class - " + type.getName());
    }
}
