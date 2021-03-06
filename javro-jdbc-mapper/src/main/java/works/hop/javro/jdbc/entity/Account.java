// AUTO-GENERATED by JavaPoet
package works.hop.javro.jdbc.entity;

import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.annotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessCode() {
        return this.accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
