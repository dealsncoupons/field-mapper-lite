// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.sample1.hydrate.entity.Membership;

public class MembershipBuilder {
  private UUID id;

  private LocalDate dateCreated;

  private LocalDateTime lastUpdated;

  private UUID member;

  private UUID club;

  private String alias;

  private String status;

  private MembershipBuilder() {
  }

  public static MembershipBuilder newBuilder() {
    return new MembershipBuilder();
  }

  public MembershipBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public MembershipBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public MembershipBuilder lastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public MembershipBuilder member(UUID member) {
    this.member = member;
    return this;
  }

  public MembershipBuilder club(UUID club) {
    this.club = club;
    return this;
  }

  public MembershipBuilder alias(String alias) {
    this.alias = alias;
    return this;
  }

  public MembershipBuilder status(String status) {
    this.status = status;
    return this;
  }

  public Membership build() {
    return new Membership(id,dateCreated,lastUpdated,member,club,alias,status);
  }
}
