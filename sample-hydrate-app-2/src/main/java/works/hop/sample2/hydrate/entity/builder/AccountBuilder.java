// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.util.UUID;
import works.hop.sample2.hydrate.entity.Account;
import works.hop.sample2.hydrate.entity.User;

public class AccountBuilder {
  private UUID id;

  private String username;

  private String password;

  private User user;

  private LocalDate dateCreated;

  private AccountBuilder() {
  }

  public static AccountBuilder newBuilder() {
    return new AccountBuilder();
  }

  public AccountBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public AccountBuilder username(String username) {
    this.username = username;
    return this;
  }

  public AccountBuilder password(String password) {
    this.password = password;
    return this;
  }

  public AccountBuilder user(User user) {
    this.user = user;
    return this;
  }

  public AccountBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public Account build() {
    return new Account(id,username,password,user,dateCreated);
  }
}
