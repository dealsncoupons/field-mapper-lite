package works.hop.upside.entity.contract;

import works.hop.upside.context.Hydrant;
import works.hop.upside.entity.Address;

import java.util.Date;
import java.util.UUID;

public interface IUser extends Hydrant {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getEmailAddress();

    Address getAddress();

    Date getDateCreated();
}
