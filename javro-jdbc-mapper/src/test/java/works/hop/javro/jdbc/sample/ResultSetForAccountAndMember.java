package works.hop.javro.jdbc.sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

public class ResultSetForAccountAndMember {

    Map<Integer, Object> accounts = new LinkedHashMap<>();
    Map<Integer, Object> members = new LinkedHashMap<>();

    @Before
    public void setUp() {
        Map<String, Object> steve = new HashMap<>();
        UUID steveUUID = UUID.randomUUID();
        UUID steveMemberId = UUID.randomUUID();
        steve.put("id", steveUUID);
        steve.put("date_created", LocalDate.now());
        steve.put("last_updated", LocalDateTime.now());
        steve.put("access_code", "steve-xys");
        steve.put("username", "steve");
        steve.put("member_id", steveMemberId);
        accounts.put(0, steve);

        Map<String, Object> agies = new HashMap<>();
        UUID agiesUUID = UUID.randomUUID();
        UUID agiesMemberId = UUID.randomUUID();
        agies.put("id", agiesUUID);
        agies.put("date_created", LocalDate.now());
        agies.put("last_updated", LocalDateTime.now());
        agies.put("access_code", "agies-xys");
        agies.put("username", "agies");
        agies.put("member_id", agiesMemberId);
        accounts.put(1, agies);

        Map<String, Object> carlos = new HashMap<>();
        UUID carlosUUID = UUID.randomUUID();
        UUID carlosMemberId = UUID.randomUUID();
        carlos.put("id", carlosUUID);
        carlos.put("date_created", LocalDate.now());
        carlos.put("last_updated", LocalDateTime.now());
        carlos.put("access_code", "carlos-xys");
        carlos.put("username", "carlos");
        carlos.put("member_id", carlosMemberId);
        accounts.put(2, carlos);

        Map<String, Object> steveMember = new HashMap<>();
        steveMember.put("id", steveMemberId);
        steveMember.put("date_created", LocalDate.now());
        steveMember.put("last_updated", LocalDateTime.now());
        steveMember.put("full_name", "Steve Mikes");
        steveMember.put("email_addr", "steve.mikes@email.com");
        steveMember.put("city", "Chicago");
        steveMember.put("state_prov", "IL");
        steveMember.put("zip_code", "60606");
        members.put(0, steveMember);

        Map<String, Object> agiesMember = new HashMap<>();
        agiesMember.put("id", agiesMemberId);
        agiesMember.put("date_created", LocalDate.now());
        agiesMember.put("last_updated", LocalDateTime.now());
        agiesMember.put("full_name", "Agies Miller");
        agiesMember.put("email_addr", "agies.miller@email.com");
        agiesMember.put("city", "Dallas");
        agiesMember.put("state_prov", "TX");
        agiesMember.put("zip_code", "78558");
        members.put(1, agiesMember);

        Map<String, Object> carlosMember = new HashMap<>();
        carlosMember.put("id", carlosMemberId);
        carlosMember.put("date_created", LocalDate.now());
        carlosMember.put("last_updated", LocalDateTime.now());
        carlosMember.put("full_name", "Carlos Machiu");
        carlosMember.put("email_addr", "marlos.machiu@email.com");
        carlosMember.put("city", "Pasadena");
        carlosMember.put("state_prov", "CA");
        carlosMember.put("zip_code", "90029");
        members.put(2, carlosMember);
    }

    @After
    public void tearDown() {
        accounts.clear();
        members.clear();
    }

    public ResultSetMetaData createResultSetMetadata(List<String> columns) throws SQLException {
        ResultSetMetaData rsm = mock(ResultSetMetaData.class);
        when(rsm.getColumnCount()).thenReturn(columns.size());
        when(rsm.getColumnName(anyInt())).thenAnswer(inv -> {
            int index = inv.getArgument(0);
            return columns.get(index);
        });
        return rsm;
    }

    public ResultSet accountsResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        AtomicInteger size = new AtomicInteger(accounts.size());
        when(rs.next()).thenAnswer(inv -> size.getAndDecrement() > 0);

        List<String> columns = List.of("id", "date_created", "last_updated", "username", "access_code", "member");
        ResultSetMetaData rsm = createResultSetMetadata(columns);
        when(rs.getMetaData()).thenReturn(rsm);

        doAnswer(invocation -> {
            Map<String, Object> account = (Map<String, Object>) accounts.get(size.get());
            String key = invocation.getArgument(0);
            Class<?> type = invocation.getArgument(1);
            return type.cast(account.get(key));
        }).when(rs).getObject(anyString(), any(Class.class));
        return rs;
    }

    public ResultSet membersResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        AtomicInteger size = new AtomicInteger(members.size());
        when(rs.next()).thenAnswer(inv -> size.getAndDecrement() > 0);

        List<String> columns = List.of("id", "date_created", "last_updated", "full_name", "email_addr", "address");
        ResultSetMetaData rsm = createResultSetMetadata(columns);
        when(rs.getMetaData()).thenReturn(rsm);

        doAnswer(invocation -> {
            Map<String, Object> member = (Map<String, Object>) members.get(size.get());
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
        MapResultSetToEntity<IAccount> mapper = new MapResultSetToEntity<>();
        IAccount entity = mapper.mapRsToEntity(accountsResultSet(), IAccount.class);
        System.out.println("IAccount id from resultset -> " + entity.getId());
    }

    @Test
    public void testMapMemberFromResultSetToEntity() throws SQLException {
        MapResultSetToEntity<IMember> mapper = new MapResultSetToEntity<>();
        IMember entity = mapper.mapRsToEntity(membersResultSet(), IMember.class);
        System.out.println("IMember id from resultset -> " + entity.getId());
    }
}
