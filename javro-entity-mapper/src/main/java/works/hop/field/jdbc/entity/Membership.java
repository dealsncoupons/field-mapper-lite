// AUTO-GENERATED by JavaPoet
package works.hop.field.jdbc.entity;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Id;
import works.hop.field.jdbc.annotation.Table;

@Table("tbl_membership")
public class Membership {
  @Column("date_created")
  public LocalDate dateCreated;

  @Column("last_updated")
  public LocalDateTime lastUpdated;

  @Id
  @Column("member_id")
  public UUID member;

  @Id
  @Column("club_id")
  public UUID club;

  @Column("member_alias")
  public String alias;

  @Column("member_status")
  public String status;

  public Membership() {
  }

  public void setDateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
  }

  public LocalDate setDateCreated() {
    return this.dateCreated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public LocalDateTime setLastUpdated() {
    return this.lastUpdated;
  }

  public void setMember(UUID member) {
    this.member = member;
  }

  public UUID setMember() {
    return this.member;
  }

  public void setClub(UUID club) {
    this.club = club;
  }

  public UUID setClub() {
    return this.club;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String setAlias() {
    return this.alias;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String setStatus() {
    return this.status;
  }
}