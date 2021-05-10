package works.hop.javro.jdbc.sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ResultSetMultipleRows {

    Map<Integer, Object> rows = new HashMap<>();

    @Before
    public void setUp(){
        Map<String, Object> task1 = new HashMap<>();
        task1.put("name", "make breakfast");
        task1.put("completed", false);
        rows.put(0, task1);

        Map<String, Object> task11 = new HashMap<>();
        task11.put("name", "pour milk");
        task11.put("completed", false);
        rows.put(1, task11);

        Map<String, Object> task12 = new HashMap<>();
        task12.put("name", "make pancake");
        task12.put("completed", false);
        rows.put(2, task12);

        Map<String, Object> task121 = new HashMap<>();
        task121.put("name", "heat up skillet");
        task121.put("completed", false);
        rows.put(3, task121);

        Map<String, Object> task122 = new HashMap<>();
        task122.put("name", "pour mixed ingredients");
        task122.put("completed", false);
        rows.put(4, task122);

        Map<String, Object> task123 = new HashMap<>();
        task123.put("name", "serve when ready");
        task123.put("completed", false);
        rows.put(5, task123);
    }

    @After
    public void tearDown(){
        rows.clear();
    }

    public ResultSet mockResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        AtomicInteger size = new AtomicInteger(rows.size());
        when(rs.next()).thenAnswer(inv -> size.getAndDecrement() > 0);

        Map<String, Object> row = (Map<String, Object>) rows.get(size.get());
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Class<?> type = invocation.getArgument(1);
            return type.cast(row.get(key));
        }).when(rs).getObject(anyString(), any(Class.class));
        return rs;
    }

    @Test
    public void testValuesFromResultSet() throws SQLException {
        ResultSet rs = mockResultSet();
        while(rs.next()){
            String name = rs.getObject("name", String.class);
            assertEquals(name, "make breakfast");
            Boolean completed = rs.getObject("completed", Boolean.class);
            assertFalse(completed);
        }
    }
}
