// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import works.hop.sample1.hydrate.entity.Club;
import works.hop.sample1.hydrate.entity.Member;
import works.hop.sample1.hydrate.entity.MyMembership;

public class MyMembershipBuilder {
  private UUID id;

  private LocalDate dateCreated;

  private LocalDateTime lastUpdated;

  private Member member;

  private Collection<Club> club;

  private String alias;

  private String status;

  private MyMembershipBuilder() {
  }

  public static MyMembershipBuilder newBuilder() {
    return new MyMembershipBuilder();
  }

  public MyMembershipBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public MyMembershipBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public MyMembershipBuilder lastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public MyMembershipBuilder member(Member member) {
    this.member = member;
    return this;
  }

  public MyMembershipBuilder club(Collection<Club> club) {
    this.club = club;
    return this;
  }

  public MyMembershipBuilder alias(String alias) {
    this.alias = alias;
    return this;
  }

  public MyMembershipBuilder status(String status) {
    this.status = status;
    return this;
  }

  public MyMembership build() {
    return new MyMembership(id,dateCreated,lastUpdated,member,club,alias,status);
  }
}
