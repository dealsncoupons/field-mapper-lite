// AUTO-GENERATED by JavaPoet
package works.hop.javro.jdbc.entity;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.annotation.Table;

@Table("tbl_account")
public class Account {
  @Id
  public UUID id;

  @Column("date_created")
  public LocalDate dateCreated;

  @Column("last_updated")
  public LocalDateTime lastUpdated;

  public String username;

  @Column("access_code")
  public String accessCode;

  @JoinColumn("member_id")
  public Member member;

  public Account() {
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID setId() {
    return this.id;
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

  public void setUsername(String username) {
    this.username = username;
  }

  public String setUsername() {
    return this.username;
  }

  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  public String setAccessCode() {
    return this.accessCode;
  }

  public void setMember(Member member) {
    this.member = member;
  }

  public Member setMember() {
    return this.member;
  }
}
