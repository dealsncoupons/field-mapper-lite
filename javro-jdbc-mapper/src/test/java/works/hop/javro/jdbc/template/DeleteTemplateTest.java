package works.hop.javro.jdbc.template;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class DeleteTemplateTest {

    @Test
    public void prepareQuery() {
        String tableName = "testTable";
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("id", UUID.randomUUID());
        String query = DeleteTemplate.prepareQuery(tableName, fields);
        System.out.println(query);
        assertEquals(query, "delete from testTable where id = ?");
    }
}