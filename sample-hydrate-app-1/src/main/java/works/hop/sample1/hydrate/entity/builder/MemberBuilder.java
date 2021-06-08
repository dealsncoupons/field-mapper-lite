// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.sample1.hydrate.entity.Address;
import works.hop.sample1.hydrate.entity.Member;

public class MemberBuilder {
  private UUID id;

  private LocalDate dateCreated;

  private LocalDateTime lastUpdated;

  private String fullName;

  private String emailAddr;

  private Address address;

  private MemberBuilder() {
  }

  public static MemberBuilder newBuilder() {
    return new MemberBuilder();
  }

  public MemberBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public MemberBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public MemberBuilder lastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public MemberBuilder fullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  public MemberBuilder emailAddr(String emailAddr) {
    this.emailAddr = emailAddr;
    return this;
  }

  public MemberBuilder address(Address address) {
    this.address = address;
    return this;
  }

  public Member build() {
    return new Member(id,dateCreated,lastUpdated,fullName,emailAddr,address);
  }
}
