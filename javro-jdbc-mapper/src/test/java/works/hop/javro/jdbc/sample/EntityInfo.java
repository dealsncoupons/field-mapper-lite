package works.hop.javro.jdbc.sample;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityInfo {

    public static Function<Class<?>, List<FieldInfo>> getEntityInfo = entityType -> {
        if (IAccount.class.equals(entityType)) {
            return accountInfo().get();
        }
        if(IMember.class.equals(entityType)){
            return memberInfo().get();
        }
        if(IAddress.class.equals(entityType)){
            return addressInfo().get();
        }
        throw new RuntimeException("Unknown entity type - " + entityType.getName());
    };

    private static Supplier<List<FieldInfo>> accountInfo(){
        FieldInfo id = new FieldInfo(UUID.class, "id", "id", false, false, false, "");
        FieldInfo dateCreated = new FieldInfo(LocalDate.class, "dateCreated", "date_created", false, false, false, "");
        FieldInfo lastUpdated = new FieldInfo(LocalDateTime.class, "lastUpdated", "last_updated", false, false, false, "");
        FieldInfo username = new FieldInfo(String.class, "username", "username", false, false, false, "");
        FieldInfo accessCode = new FieldInfo(String.class, "accessCode", "access_code", false, false, false, "");
        FieldInfo member = new FieldInfo(IMember.class, "member", "member_id", false, true, false, "");
        return () -> List.of(id, dateCreated, lastUpdated, username, accessCode, member);
    }

    private static Supplier<List<FieldInfo>> memberInfo() {
        FieldInfo id = new FieldInfo(UUID.class, "id", "id", false, false, false, "");
        FieldInfo dateCreated = new FieldInfo(LocalDate.class, "dateCreated", "date_created", false, false, false, "");
        FieldInfo lastUpdated = new FieldInfo(LocalDateTime.class, "lastUpdated", "last_updated", false, false, false, "");
        FieldInfo fullName = new FieldInfo(String.class, "fullName", "full_name", false, false, false, "");
        FieldInfo emailAddr = new FieldInfo(String.class, "emailAddr", "email_addr", false, false, false, "");
        FieldInfo address = new FieldInfo(IAddress.class, "address", "address", true, false, false, "");
        return () -> List.of(id, dateCreated, lastUpdated, fullName, emailAddr, address);
    }

    private static Supplier<List<FieldInfo>> addressInfo() {
        FieldInfo city = new FieldInfo(String.class, "city", "city", false, false, false, "");
        FieldInfo state = new FieldInfo(String.class, "state", "state_prov", false, false, false, "");
        FieldInfo zip = new FieldInfo(String.class, "zipCode", "zip_code", false, false, false, "");
        return () -> List.of(city, state, zip);
    }
}
