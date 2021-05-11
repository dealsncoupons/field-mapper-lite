package works.hop.javro.jdbc.sample;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IMember {
    UUID getId();

    void setId(UUID id);

    LocalDate getDateCreated();

    void setDateCreated(LocalDate dateCreated);

    LocalDateTime getLastUpdated();

    void setLastUpdated(LocalDateTime lastUpdated);

    String getFullName();

    void setFullName(String fullName);

    String getEmailAddr();

    void setEmailAddr(String emailAddr);

    IAddress getAddress();

    void setAddress(IAddress address);
}
