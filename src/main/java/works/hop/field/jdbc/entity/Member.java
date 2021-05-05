package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Embedded;
import works.hop.field.jdbc.annotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("tbl_member")
public class Member extends BaseEntity {

    @Column("full_name")
    public String fullName;
    @Column("email_addr")
    public String emailAddr;
    @Embedded
    public Address address;

    public Member() {
        super();
    }

    public Member(UUID id, LocalDate dateCreated, LocalDateTime lastUpdated, String fullName, String emailAddr, Address address) {
        super(id, dateCreated, lastUpdated);
        this.fullName = fullName;
        this.emailAddr = emailAddr;
        this.address = address;
    }
}
