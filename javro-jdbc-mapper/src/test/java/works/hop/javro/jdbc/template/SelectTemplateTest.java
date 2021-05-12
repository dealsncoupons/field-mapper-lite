package works.hop.javro.jdbc.template;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SelectTemplateTest {

    @Test
    public void prepareQuery() {
        String tableName = "testTable";
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("name", "James");
        fields.put("age", 20);
        Map<String, Object> idFields = Map.of("id", UUID.randomUUID());
        String query = "to be completed"; //SelectTemplate.prepareQuery(tableName, fields);
        System.out.println(query);
        assertEquals(query, "to be completed");
    }
}