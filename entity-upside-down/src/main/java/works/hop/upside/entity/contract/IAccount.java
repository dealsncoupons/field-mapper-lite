package works.hop.upside.entity.contract;

import works.hop.upside.context.Hydrant;
import works.hop.upside.entity.User;

import java.util.Date;
import java.util.UUID;

public interface IAccount extends Hydrant {
    UUID getId();

    String getUsername();

    String getPassword();

    User getUser();

    Date getDateCreated();
}
