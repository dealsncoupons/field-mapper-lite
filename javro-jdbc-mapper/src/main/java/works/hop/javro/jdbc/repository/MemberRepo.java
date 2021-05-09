package works.hop.javro.jdbc.repository;

import works.hop.javro.jdbc.annotation.Query;
import works.hop.javro.jdbc.entity.Member;
import works.hop.javro.jdbc.template.CrudRepo;

import java.util.UUID;

public interface MemberRepo extends CrudRepo<Member, UUID> {

    @Query("select * from tbl_member where email_addr = ?")
    Member findByEmail(String email);
}
