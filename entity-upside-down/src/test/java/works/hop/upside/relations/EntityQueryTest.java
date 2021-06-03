package works.hop.upside.relations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityQueryTest {

    @Test
    public void oneToOne() {
        String expected = "select SRC_TBL.* from tbl_task SRC_TBL " +
                "where SRC_TBL.next_task = ?";

        EntityQuery eq = EntityQuery.getInstance();
        String actual = eq.oneToOne("tbl_task", "next_task");
        assertEquals(expected, actual);
    }

    @Test
    public void manyToOne() {
        String expected = "select SRC_TBL.* from tbl_task SRC_TBL " +
                "inner join tbl_task JOIN_TBL on SRC_TBL.id = JOIN_TBL.next_task " +
                "where SRC_TBL.next_task = ?";

        EntityQuery eq = EntityQuery.getInstance();
        String actual = eq.manyToOne("tbl_task", "id", "tbl_task", "next_task", "next_task");
        assertEquals(expected, actual);
    }

    @Test
    public void insertOne() {
        String expected = "insert into tbl_user ( first_name, last_name, email_address, addr_city, addr_state_prov, addr_zip_code ) " +
                "values ( ?, ?, ?, ?, ?, ? ) on conflict do nothing returning id::uuid";

        EntityQuery eq = EntityQuery.getInstance();
        String actual = eq.insertOne("tbl_user", new String[]{"first_name", "last_name", "email_address", "addr_city",
                "addr_state_prov", "addr_zip_code"});
        assertEquals(expected, actual);
    }

    @Test
    public void updateOne() {
        String tableName = "tbl_task";
        String[] idColumns = new String[]{"id"};
        String[] valueColumns = new String[]{"name", "done", "next_task", "parent_task"};

        String expected = "update tbl_task set name = ?, done = ?, next_task = ?, parent_task = ? where id = ?";
        String actual = EntityQuery.getInstance().updateOne(tableName, idColumns, valueColumns);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteOne() {
        String tableName = "tbl_task";
        String[] idColumns = new String[]{"id"};

        String expected = "delete from tbl_task where id = ?";
        String actual = EntityQuery.getInstance().deleteOne(tableName, idColumns);
        assertEquals(expected, actual);
    }
}