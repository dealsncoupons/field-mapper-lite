package works.hop.upside;

import works.hop.upside.context.DbSelect;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.UpdateTemplate;
import works.hop.upside.entity.Account;
import works.hop.upside.entity.Address;
import works.hop.upside.entity.Task;
import works.hop.upside.entity.User;
import works.hop.upside.relations.EntityInfo;
import works.hop.upside.relations.EntityMetadata;
import works.hop.upside.relations.FieldInfo;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class Main {

    public static final DbSelect dbSelect = new DbSelect();

    public static void main(String[] args) {
//        retrieveUserEntity();
//        retrieveAccountEntity();
//        retrieveTaskEntity();
//
        createUserEntity();
        createAccountEntity();
        createTaskEntity();

    }

    private static void createTaskEntity() {
        Task serveMilk = new Task(null, "Serve hot milk", false, null, null, Collections.emptyList());
        Task servePancake = new Task(null, "Serve while hot", false, serveMilk, null, Collections.emptyList());
        Task addCondiments = new Task(null, "Slurp some syrup", false, servePancake, null, Collections.emptyList());
        Task flipPancake = new Task(null, "Flip after a minute", false, addCondiments, null, Collections.emptyList());
        Task pourMix = new Task(null, "Pour in the mix", false, flipPancake, null, Collections.emptyList());
        Task heatUpSkillet = new Task(null, "Heat up skillet", false, pourMix, null, Collections.emptyList());
        Task addEggs = new Task(null, "Add eggs into mix", false, heatUpSkillet, null, Collections.emptyList());
        Task beatFlour = new Task(null, "Mix flour and water", false, addEggs, null, Collections.emptyList());
        Task makeBreakfast = new Task(null, "Make pancake breakfast", false, beatFlour, null, Collections.emptyList());

        Task created = InsertTemplate.insertOne(makeBreakfast);
        System.out.println("Created entity with id -> " + created.getId());

        serveMilk = UpdateTemplate.updateOne(serveMilk, Map.of("id", serveMilk.getId(), "dependsOn", makeBreakfast));
        System.out.println("Updated entity with id -> " + serveMilk.getId());
        servePancake = UpdateTemplate.updateOne(servePancake, Map.of("id", servePancake.getId(), "dependsOn", addCondiments));
        System.out.println("Updated entity with id -> " + servePancake.getId());
        addCondiments = UpdateTemplate.updateOne(addCondiments, Map.of("id", addCondiments.getId(), "dependsOn", flipPancake));
        System.out.println("Updated entity with id -> " + addCondiments.getId());
        flipPancake = UpdateTemplate.updateOne(flipPancake, Map.of("id", flipPancake.getId(), "dependsOn", pourMix));
        System.out.println("Updated entity with id -> " + flipPancake.getId());
        pourMix = UpdateTemplate.updateOne(pourMix, Map.of("id", pourMix.getId(), "dependsOn", heatUpSkillet));
        System.out.println("Updated entity with id -> " + pourMix.getId());
        heatUpSkillet = UpdateTemplate.updateOne(heatUpSkillet, Map.of("id", heatUpSkillet.getId(), "dependsOn", addEggs));
        System.out.println("Updated entity with id -> " + heatUpSkillet.getId());
        addEggs = UpdateTemplate.updateOne(addEggs, Map.of("id", addEggs.getId(), "dependsOn", beatFlour));
        System.out.println("Updated entity with id -> " + addEggs.getId());
        beatFlour = UpdateTemplate.updateOne(beatFlour, Map.of("id", beatFlour.getId(), "dependsOn", makeBreakfast));
        System.out.println("Updated entity with id -> " + beatFlour.getId());

        EntityInfo entityInfo = EntityMetadata.getInstance().entityInfoByType(Task.class);
        UUID searchId = makeBreakfast.getId();
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Task entity = dbSelect.selectByIdColumn(entityInfo.getTableName(), pkColumn, searchId, Task.class);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void createUserEntity() {
        Address address = new Address("Madison", "WI", "53718");
        User entity = new User(null, "Jua", "Kiasi", "jua.kiasi@email.com", address);
        User created = InsertTemplate.insertOne(entity);
        System.out.println("Created entity with id -> " + created.getId());

        User updated = UpdateTemplate.updateOne(created, Map.of("firstName", "Jani"));
        System.out.println("Updated entity id -> " + updated.getId() + " with firstName " + updated.getFirstName());
    }

    private static void createAccountEntity() {
        Address address = new Address("Madison", "WI", "53716");
        User user = new User(null, "Jua", "kazi", "jua.kazi@email.com", address);

        Account account = new Account(null, "juakazi", "kazi_code", user);
        Account created = InsertTemplate.insertOne(account);
        System.out.println("Created entity with id -> " + created.getId());
    }

    private static void retrieveUserEntity() {
        EntityInfo entityInfo = EntityMetadata.getInstance().entityInfoByType(User.class);
        String searchId = "690581e2-c09d-11eb-ad8b-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            User entity = dbSelect.selectByIdColumn(entityInfo.getTableName(), pkColumn, pkValue, User.class);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void retrieveAccountEntity() {
        EntityInfo entityInfo = EntityMetadata.getInstance().entityInfoByType(Account.class);
        String searchId = "69058aca-c09d-11eb-ad8b-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Account entity = dbSelect.selectByIdColumn(entityInfo.getTableName(), pkColumn, pkValue, Account.class);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void retrieveTaskEntity() {
        EntityInfo entityInfo = EntityMetadata.getInstance().entityInfoByType(Task.class);
        String searchId = "6905f92e-c09d-11eb-ad8b-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Task entity = dbSelect.selectByIdColumn(entityInfo.getTableName(), pkColumn, pkValue, Task.class);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }
}
