// AUTO-GENERATED by JavaPoet
package works.hop.field.jdbc.entity;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Embedded;
import works.hop.field.jdbc.annotation.Id;
import works.hop.field.jdbc.annotation.Table;

@Table("tbl_member")
public class Member {
  @Id
  public UUID id;

  @Column("date_created")
  public LocalDate dateCreated;

  @Column("last_updated")
  public LocalDateTime lastUpdated;

  @Column("full_name")
  public String fullName;

  @Column("email_addr")
  public String emailAddr;

  @Embedded
  public Address address;

  public Member() {
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

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String setFullName() {
    return this.fullName;
  }

  public void setEmailAddr(String emailAddr) {
    this.emailAddr = emailAddr;
  }

  public String setEmailAddr() {
    return this.emailAddr;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Address setAddress() {
    return this.address;
  }
}