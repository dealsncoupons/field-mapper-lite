package works.hop.javro.jdbc.template;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UpdateTemplateTest {

    @Test
    public void testPrepareQuery() {
        String tableName = "testTable";
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("name", "James");
        fields.put("age", 20);
        Map<String, Object> idFields = Map.of("id", UUID.randomUUID());
        String query = UpdateTemplate.prepareQuery(tableName, idFields, fields);
        System.out.println(query);
        assertEquals(query, "update testTable set name = ?, age = ? where id = ?");
    }
}