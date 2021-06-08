// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import works.hop.sample1.hydrate.entity.Address;

public class AddressBuilder {
  private String city;

  private String state;

  private String zipCode;

  private AddressBuilder() {
  }

  public static AddressBuilder newBuilder() {
    return new AddressBuilder();
  }

  public AddressBuilder city(String city) {
    this.city = city;
    return this;
  }

  public AddressBuilder state(String state) {
    this.state = state;
    return this;
  }

  public AddressBuilder zipCode(String zipCode) {
    this.zipCode = zipCode;
    return this;
  }

  public Address build() {
    return new Address(city,state,zipCode);
  }
}
