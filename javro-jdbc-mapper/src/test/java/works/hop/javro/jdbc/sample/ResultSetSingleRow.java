package works.hop.javro.jdbc.sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ResultSetSingleRow {

    Map<String, Object> task = new HashMap<>();

    @Before
    public void setUp(){
        task.put("name", "make breakfast");
        task.put("completed", false);
    }

    @After
    public void tearDown(){
        task.clear();
    }

    public ResultSet mockResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Class<?> type = invocation.getArgument(1);
            return type.cast(task.get(key));
        }).when(rs).getObject(anyString(), any(Class.class));
        return rs;
    }

    @Test
    public void testValuesFromResultSet() throws SQLException {
        ResultSet rs = mockResultSet();
        if(rs.next()){
            String name = rs.getObject("name", String.class);
            assertEquals(name, "make breakfast");
            Boolean completed = rs.getObject("completed", Boolean.class);
            assertFalse(completed);
        }
    }
}
