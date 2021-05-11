package works.hop.javro.jdbc.sample.account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MockAccounts {

    private static final Map<Integer, Map<String, Object>> accounts = new LinkedHashMap<>();
    private static final Map<Integer, Map<String, Object>> members = new LinkedHashMap<>();

    static {
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

    public static Map<Integer, Map<String, Object>> fetchAccountsData() {
        return accounts;
    }

    public static Map<Integer, Map<String, Object>> fetchMembersData() {
        return members;
    }
}
