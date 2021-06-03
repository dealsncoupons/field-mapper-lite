package works.hop.upside.relations;

import works.hop.upside.entity.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EntityMetadata {

    private static final Object lock = new Object();
    public static EntityMetadata instance;

    private EntityMetadata() {
    }

    public static synchronized EntityMetadata getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new EntityMetadata();
            }
        }
        return instance;
    }

    public EntityInfo userEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_user");
        FieldInfo id = FieldInfoBuilder.builder().isId(true).name("id").type(UUID.class).build();
        FieldInfo firstName = FieldInfoBuilder.builder().name("firstName").columnName("first_name").build();
        FieldInfo lastName = FieldInfoBuilder.builder().name("lastName").columnName("last_name").build();
        FieldInfo emailAddress = FieldInfoBuilder.builder().name("emailAddress").columnName("email_address").build();
        FieldInfo address = FieldInfoBuilder.builder().embedded(true).embeddedField(builder ->
                builder.name("city").columnName("addr_city").build()).embeddedField(builder ->
                builder.name("state").columnName("addr_state_prov").build()).embeddedField(builder ->
                builder.name("zipCode").columnName("addr_zip_code").build())
                .type(Address.class)
                .build();
        entityInfo.setFields(List.of(id, firstName, lastName, emailAddress, address));
        return entityInfo;
    }

    public EntityInfo accountEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_account");
        FieldInfo id = FieldInfoBuilder.builder().isId(true).name("id").type(UUID.class).build();
        FieldInfo username = FieldInfoBuilder.builder().name("username").columnName("user_name").build();
        FieldInfo password = FieldInfoBuilder.builder().name("password").columnName("access_code").build();
        FieldInfo user = FieldInfoBuilder.builder().relational(true).name("user").columnName("user_id").type(User.class).build();
        entityInfo.setFields(List.of(id, username, password, user));
        return entityInfo;
    }

    public EntityInfo taskEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_task");
        FieldInfo id = FieldInfoBuilder.builder().isId(true).name("id").type(UUID.class).build();
        FieldInfo username = FieldInfoBuilder.builder().name("name").build();
        FieldInfo password = FieldInfoBuilder.builder().name("completed").columnName("done").build();
        FieldInfo nextTask = FieldInfoBuilder.builder().relational(true).name("nextTask").columnName("next_task").type(Task.class).build();
        FieldInfo parentTask = FieldInfoBuilder.builder().relational(true).name("parentTask").columnName("parent_task").type(Task.class).build();
        FieldInfo subTasks = FieldInfoBuilder.builder().collection(true).name("subTasks").type(Task.class).build();
        entityInfo.setFields(List.of(id, username, password, nextTask, parentTask, subTasks));
        return entityInfo;
    }

    public EntityInfo assignmentEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_assignment");
        FieldInfo id = FieldInfoBuilder.builder().isId(true).name("id").type(UUID.class).build();
        FieldInfo dateAssigned = FieldInfoBuilder.builder().name("dateAssigned").columnName("date_assigned").type(Date.class).build();
        FieldInfo task = FieldInfoBuilder.builder().relational(true).name("task").columnName("task_id").type(Task.class).build();
        FieldInfo assignee = FieldInfoBuilder.builder().relational(true).name("assignee").columnName("assignee_id").type(User.class).build();
        entityInfo.setFields(List.of(id, dateAssigned, task, assignee));
        return entityInfo;
    }

    public EntityInfo assigneeEntityInfo() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("tbl_assignment");
        FieldInfo user = FieldInfoBuilder.builder().relational(true).name("user").columnName("assignee_id").type(User.class).build();
        FieldInfo tasks = FieldInfoBuilder.builder().relational(true).collection(true).name("assignments").columnName("task_id").type(Task.class).build();
        entityInfo.setFields(List.of(user, tasks));
        return entityInfo;
    }

    public EntityInfo entityInfoByType(Class<?> entityType) {
        if (User.class.equals(entityType)) {
            return userEntityInfo();
        }
        if (Account.class.equals(entityType)) {
            return accountEntityInfo();
        }
        if (Task.class.equals(entityType)) {
            return taskEntityInfo();
        }
        if (Assignment.class.equals(entityType)) {
            return assignmentEntityInfo();
        }
        if (Assignee.class.equals(entityType)) {
            return assigneeEntityInfo();
        }
        throw new RuntimeException(String.format("No entity type info found for the type %s", entityType.getName()));
    }
}
