package works.hop.javro.jdbc.sample;

import works.hop.javro.jdbc.EntityInfo;
import works.hop.javro.jdbc.sample.account.IAccount;
import works.hop.javro.jdbc.sample.account.IAddress;
import works.hop.javro.jdbc.sample.account.IMember;
import works.hop.javro.jdbc.sample.todo.ITodo;
import works.hop.javro.jdbc.sample.todo.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityMetadata {

    public static Function<Class<?>, EntityInfo> getEntityInfo = entityType -> {
        if (IAccount.class.equals(entityType)) {
            return accountInfo().get();
        }
        if (IMember.class.equals(entityType)) {
            return memberInfo().get();
        }
        if (IAddress.class.equals(entityType)) {
            return addressInfo().get();
        }
        if(Todo.class.equals(entityType)){
            return todoInfo().get();
        }
        throw new RuntimeException("Unknown entity type - " + entityType.getName());
    };

    private static Supplier<EntityInfo> accountInfo() {
        FieldInfo id = new FieldInfo(true, UUID.class, "id", "id");
        FieldInfo dateCreated = new FieldInfo(LocalDate.class, "dateCreated", "date_created");
        FieldInfo lastUpdated = new FieldInfo(LocalDateTime.class, "lastUpdated", "last_updated");
        FieldInfo username = new FieldInfo(String.class, "username", "username");
        FieldInfo accessCode = new FieldInfo(String.class, "accessCode", "access_code");
        FieldInfo member = new FieldInfo(IMember.class, "member", "member_id", true, false, "");
        return () -> new EntityInfo("tbl_account", List.of(id, dateCreated, lastUpdated, username, accessCode, member));
    }

    private static Supplier<EntityInfo> memberInfo() {
        FieldInfo id = new FieldInfo(true, UUID.class, "id", "id");
        FieldInfo dateCreated = new FieldInfo(LocalDate.class, "dateCreated", "date_created");
        FieldInfo lastUpdated = new FieldInfo(LocalDateTime.class, "lastUpdated", "last_updated");
        FieldInfo fullName = new FieldInfo(String.class, "fullName", "full_name");
        FieldInfo emailAddr = new FieldInfo(String.class, "emailAddr", "email_addr");
        FieldInfo address = new FieldInfo(IAddress.class, "address", "address", true);
        return () -> new EntityInfo("table_member", List.of(id, dateCreated, lastUpdated, fullName, emailAddr, address));
    }

    private static Supplier<EntityInfo> addressInfo() {
        FieldInfo city = new FieldInfo(String.class, "city", "city");
        FieldInfo state = new FieldInfo(String.class, "state", "state_prov");
        FieldInfo zip = new FieldInfo(String.class, "zipCode", "zip_code");
        return () -> new EntityInfo("", List.of(city, state, zip));
    }

    private static Supplier<EntityInfo> todoInfo() {
        FieldInfo city = new FieldInfo(true, UUID.class, "id", "id");
        FieldInfo state = new FieldInfo(String.class, "name", "name");
        FieldInfo zip = new FieldInfo(Boolean.class, "completed", "completed");
        FieldInfo subTasks = new FieldInfo(ITodo.class, "subTasks", "parent_task", true, true, "");
        return () -> new EntityInfo("tbl_task", List.of(city, state, zip, subTasks));
    }
}
