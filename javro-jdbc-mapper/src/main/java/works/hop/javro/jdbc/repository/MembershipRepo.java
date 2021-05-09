package works.hop.javro.jdbc.repository;

import works.hop.javro.jdbc.annotation.Query;
import works.hop.javro.jdbc.entity.Membership;
import works.hop.javro.jdbc.template.CrudRepo;

import java.util.List;
import java.util.UUID;

public interface MembershipRepo extends CrudRepo<Membership, UUID> {

    @Query("select * from tbl_membership where member_id = ?::uuid and club_id = ?::uuid")
    Membership findById(UUID member, UUID club);

    @Query("select * from tbl_membership where member_alias = ?")
    Membership findByMemberAlias(String alias);

    @Query("select * from tbl_membership where member_id = ?::uuid")
    List<Membership> findByMemberId(UUID member);
}
