package works.hop.javro.jdbc.repository;

import works.hop.javro.jdbc.annotation.Query;
import works.hop.javro.jdbc.dto.MyMembership;
import works.hop.javro.jdbc.template.CrudRepo;

import java.util.UUID;

public interface MyMembershipRepo extends CrudRepo<MyMembership, UUID> {

    @Query("select * from tbl_membership where member_alias = ?")
    MyMembership findMyMemberships(String alias);
}
