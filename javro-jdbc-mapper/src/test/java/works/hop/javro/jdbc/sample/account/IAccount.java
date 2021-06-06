package works.hop.javro.jdbc.sample.account;

import works.hop.javro.jdbc.sample.Unreflect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IAccount extends Unreflect {
    UUID getId();

    void setId(UUID id);

    LocalDate getDateCreated();

    void setDateCreated(LocalDate dateCreated);

    LocalDateTime getLastUpdated();

    void setLastUpdated(LocalDateTime lastUpdated);

    String getUsername();

    void setUsername(String username);

    String getAccessCode();

    void setAccessCode(String accessCode);

    IMember getMember();

    void setMember(IMember member);
}
