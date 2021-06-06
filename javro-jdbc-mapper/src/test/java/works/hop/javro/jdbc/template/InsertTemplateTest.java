package works.hop.javro.jdbc.template;

import org.junit.Test;
import works.hop.javro.jdbc.template.toremove.InsertTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InsertTemplateTest {

    @Test
    public void prepareQuery() {
        String tableName = "testTable";
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("name", "James");
        fields.put("age", 20);
        String query = InsertTemplate.prepareQuery(tableName, fields);
        System.out.println(query);
        assertEquals(query, "insert into testTable ( name, age ) values ( ?,? ) on conflict do nothing returning id");
    }
}