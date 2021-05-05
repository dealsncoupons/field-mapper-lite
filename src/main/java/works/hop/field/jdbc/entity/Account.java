package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.JoinColumn;
import works.hop.field.jdbc.annotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("tbl_account")
public class Account extends BaseEntity {

    public String username;
    @Column("access_code")
    public String accessCode;
    @JoinColumn("member_id")
    public Member member;

    public Account() {
        super();
    }

    public Account(UUID id, LocalDate dateCreated, LocalDateTime lastUpdated, String username, String accessCode, Member member) {
        super(id, dateCreated, lastUpdated);
        this.username = username;
        this.accessCode = accessCode;
        this.member = member;
    }
}
