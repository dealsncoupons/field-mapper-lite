package works.hop.field.jdbc.dto;

import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.JoinColumn;
import works.hop.field.jdbc.entity.Club;
import works.hop.field.jdbc.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MyMembership {

    @JoinColumn("member_id")
    public Member member;
    @JoinColumn(value = "club_id", fkTable = "tbl_membership")
    public List<Club> club;
    @Column("member_alias")
    public String alias;
    @Column("member_status")
    public String status;
    @Column("date_created")
    public LocalDate dateCreated;
    @Column("last_updated")
    public LocalDateTime lastUpdated;

    public MyMembership(Member member, List<Club> club, String alias, String status) {
        this.member = member;
        this.club = club;
        this.alias = alias;
        this.status = status;
    }

    public MyMembership() {
    }
}
