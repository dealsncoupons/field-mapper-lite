package works.hop.javro.jdbc.sample.account;

import org.junit.Test;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.MapResultSetToEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ResultSetMultipleRows {

    public static ResultSetMetaData createResultSetMetadata(List<String> columns) throws SQLException {
        ResultSetMetaData rsm = mock(ResultSetMetaData.class);
        when(rsm.getColumnCount()).thenReturn(columns.size());
        when(rsm.getColumnName(anyInt())).thenAnswer(inv -> {
            int index = inv.getArgument(0);
            return columns.get(index);
        });
        return rsm;
    }

    public static ResultSet accountsResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Map<Integer, Map<String, Object>> accounts = MockAccounts.fetchAccountsData();
        AtomicInteger size = new AtomicInteger(accounts.size());
        when(rs.next()).thenAnswer(inv -> size.getAndDecrement() > 0);

        List<String> columns = List.of("id", "date_created", "last_updated", "username", "access_code", "member");
        ResultSetMetaData rsm = createResultSetMetadata(columns);
        when(rs.getMetaData()).thenReturn(rsm);

        doAnswer(invocation -> {
            Map<String, Object> account = accounts.get(size.get());
            String key = invocation.getArgument(0);
            Class<?> type = invocation.getArgument(1);
            return type.cast(account.get(key));
        }).when(rs).getObject(anyString(), any(Class.class));
        return rs;
    }

    public static ResultSet membersResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Map<Integer, Map<String, Object>> members = MockAccounts.fetchMembersData();
        AtomicInteger size = new AtomicInteger(members.size());
        when(rs.next()).thenAnswer(inv -> size.getAndDecrement() > 0);

        List<String> columns = List.of("id", "date_created", "last_updated", "full_name", "email_addr", "city", "state_prov", "zip_code");
        ResultSetMetaData rsm = createResultSetMetadata(columns);
        when(rs.getMetaData()).thenReturn(rsm);

        doAnswer(invocation -> {
            Map<String, Object> member = members.get(size.get());
            String key = invocation.getArgument(0);
            Class<?> type = invocation.getArgument(1);
            return type.cast(member.get(key));
        }).when(rs).getObject(anyString(), any(Class.class));
        return rs;
    }

    @Test
    public void testValuesFromAccountsResultSet() throws SQLException {
        ResultSet rs = accountsResultSet();
        while (rs.next()) {
            UUID id = rs.getObject("id", UUID.class);
            System.out.println("id -> " + id.toString());
            LocalDate date_created = rs.getObject("date_created", LocalDate.class);
            System.out.println("date_created -> " + date_created);
            LocalDateTime last_updated = rs.getObject("last_updated", LocalDateTime.class);
            System.out.println("last_updated -> " + last_updated);
        }
    }

    @Test
    public void testValuesFromMembersResultSet() throws SQLException {
        ResultSet rs = membersResultSet();
        while (rs.next()) {
            UUID id = rs.getObject("id", UUID.class);
            System.out.println("id -> " + id.toString());
            LocalDate date_created = rs.getObject("date_created", LocalDate.class);
            System.out.println("date_created -> " + date_created);
            LocalDateTime last_updated = rs.getObject("last_updated", LocalDateTime.class);
            System.out.println("last_updated -> " + last_updated);
        }
    }

    @Test
    public void testMapAccountFromResultSetToEntity() throws SQLException {
        MapResultSetToEntity mapper = new MapResultSetToEntity(Collections.emptyMap());
        Connection conn = mock(Connection.class);
        IAccount entity = mapper.mapRsToEntity(accountsResultSet(), IAccount.class, conn);
        System.out.println("IAccount id from resultset -> " + entity.getId());
    }

    @Test
    public void testMapAccountFromResultSetToEntityCollection() throws SQLException {
        MapResultSetToEntity mapper = new MapResultSetToEntity(Collections.emptyMap());
        Connection conn = mock(Connection.class);
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(IAccount.class);
        Collection<IAccount> entities = mapper.mapRsToEntityCollection(accountsResultSet(), IAccount.class, entityInfo, conn);
        System.out.println("IAccount collection size -> " + entities.size());
        for (IAccount entity : entities) {
            System.out.println("IAccount id from resultset -> " + entity.getId());
            System.out.println("IAccount's username -> " + entity.getUsername());
            System.out.println("IAccount's member full name -> " + entity.getMember().getFullName());
        }
    }

    @Test
    public void testMapMemberFromResultSetToEntity() throws SQLException {
        MapResultSetToEntity mapper = new MapResultSetToEntity(Collections.emptyMap());
        Connection conn = mock(Connection.class);
        IMember entity = mapper.mapRsToEntity(membersResultSet(), IMember.class, conn);
        System.out.println("IMember id from resultset -> " + entity.getId());
        System.out.println("IMember's city -> " + entity.getAddress().getCity());
    }

    @Test
    public void testMapMemberFromResultSetToEntityCollection() throws SQLException {
        MapResultSetToEntity mapper = new MapResultSetToEntity(Collections.emptyMap());
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(IMember.class);
        Connection conn = mock(Connection.class);
        Collection<IMember> entities = mapper.mapRsToEntityCollection(membersResultSet(), IMember.class, entityInfo, conn);
        System.out.println("IMember collection size -> " + entities.size());
        for (IMember entity : entities) {
            System.out.println("IMember id from resultset -> " + entity.getId());
            System.out.println("IMember's city -> " + entity.getAddress().getCity());
        }
    }
}
