package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Id;
import works.hop.field.jdbc.annotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("tbl_membership")
public class Membership extends BaseEntity {

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
        super();
    }

    public Membership(UUID id, LocalDate dateCreated, LocalDateTime lastUpdated, UUID member, UUID club, String alias, String status) {
        super(id, dateCreated, lastUpdated);
        this.member = member;
        this.club = club;
        this.alias = alias;
        this.status = status;
    }
}
