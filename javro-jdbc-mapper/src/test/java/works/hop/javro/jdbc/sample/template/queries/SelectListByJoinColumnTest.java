package works.hop.javro.jdbc.sample.template.queries;

import io.vavr.Tuple3;
import org.junit.Test;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.account.IAccount;
import works.hop.javro.jdbc.sample.account.IMember;

import static org.junit.Assert.assertEquals;

public class SelectListByJoinColumnTest {

    @Test
    public void testQuery(){
        EntityInfo memberInfo = EntityMetadata.getEntityInfo.apply(IMember.class);;
        EntityInfo accountInfo = EntityMetadata.getEntityInfo.apply(IAccount.class);;
        String query = SelectListByJoinColumn.get().apply(new Tuple3<>(memberInfo, accountInfo, "member_id"));
        assertEquals("select l.id from table_member l inner join tbl_account r on r.member_id = l.id where l.id = ?::uuid", query);
    }
}
