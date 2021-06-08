// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.sample1.hydrate.entity.Account;
import works.hop.sample1.hydrate.entity.Member;

public class AccountBuilder {
  private UUID id;

  private LocalDate dateCreated;

  private LocalDateTime lastUpdated;

  private String username;

  private String accessCode;

  private Member member;

  private AccountBuilder() {
  }

  public static AccountBuilder newBuilder() {
    return new AccountBuilder();
  }

  public AccountBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public AccountBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public AccountBuilder lastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public AccountBuilder username(String username) {
    this.username = username;
    return this;
  }

  public AccountBuilder accessCode(String accessCode) {
    this.accessCode = accessCode;
    return this;
  }

  public AccountBuilder member(Member member) {
    this.member = member;
    return this;
  }

  public Account build() {
    return new Account(id,dateCreated,lastUpdated,username,accessCode,member);
  }
}
