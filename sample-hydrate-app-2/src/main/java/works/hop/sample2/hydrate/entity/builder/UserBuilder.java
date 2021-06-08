// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.util.UUID;
import works.hop.sample2.hydrate.entity.Address;
import works.hop.sample2.hydrate.entity.User;

public class UserBuilder {
  private UUID id;

  private String firstName;

  private String lastName;

  private String emailAddress;

  private Address address;

  private LocalDate dateCreated;

  private UserBuilder() {
  }

  public static UserBuilder newBuilder() {
    return new UserBuilder();
  }

  public UserBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public UserBuilder firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public UserBuilder lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public UserBuilder emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  public UserBuilder address(Address address) {
    this.address = address;
    return this;
  }

  public UserBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public User build() {
    return new User(id,firstName,lastName,emailAddress,address,dateCreated);
  }
}
