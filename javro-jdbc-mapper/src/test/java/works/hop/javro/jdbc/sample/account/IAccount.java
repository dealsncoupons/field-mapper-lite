package works.hop.javro.jdbc.sample.account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IAccount {
    void setId(UUID id);

    UUID getId();

    void setDateCreated(LocalDate dateCreated);

    LocalDate getDateCreated();

    void setLastUpdated(LocalDateTime lastUpdated);

    LocalDateTime getLastUpdated();

    void setUsername(String username);

    String getUsername();

    void setAccessCode(String accessCode);

    String getAccessCode();

    void setMember(IMember member);

    IMember getMember();
}
