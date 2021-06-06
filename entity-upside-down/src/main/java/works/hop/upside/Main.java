package works.hop.upside;

import works.hop.upside.context.DbSelect;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.UpdateTemplate;
import works.hop.upside.entity.Account;
import works.hop.upside.entity.Address;
import works.hop.upside.entity.Task;
import works.hop.upside.entity.User;
import works.hop.upside.relations.EntityInfo;
import works.hop.upside.relations.FieldInfo;

import java.time.LocalDate;
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
        Task serveMilk = new Task(null, "Serve hot milk", false, LocalDate.now(), null, null, Collections.emptyList());
        Task servePancake = new Task(null, "Serve while hot", false, LocalDate.now(), serveMilk, null, Collections.emptyList());
        Task addCondiments = new Task(null, "Slurp some syrup", false, LocalDate.now(), servePancake, null, Collections.emptyList());
        Task flipPancake = new Task(null, "Flip after a minute", false, LocalDate.now(), addCondiments, null, Collections.emptyList());
        Task pourMix = new Task(null, "Pour in the mix", false, LocalDate.now(), flipPancake, null, Collections.emptyList());
        Task heatUpSkillet = new Task(null, "Heat up skillet", false, LocalDate.now(), pourMix, null, Collections.emptyList());
        Task addEggs = new Task(null, "Add eggs into mix", false, LocalDate.now(), heatUpSkillet, null, Collections.emptyList());
        Task beatFlour = new Task(null, "Mix flour and water", false, LocalDate.now(), addEggs, null, Collections.emptyList());
        Task makeBreakfast = new Task(null, "Make pancake breakfast", false, LocalDate.now(), beatFlour, null, Collections.emptyList());

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

        Task taskToFind = new Task();
        EntityInfo entityInfo = taskToFind.getEntityInfo();
        UUID searchId = makeBreakfast.getId();
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Task entity = dbSelect.selectByIdColumn(taskToFind, entityInfo.getTableName(), pkColumn, searchId);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void createUserEntity() {
        Address address = new Address("Madison", "WI", "53718");
        User entity = new User(null, "Jua", "Kiasi", "jua.kiasi@email.com", address, LocalDate.now());
        User created = InsertTemplate.insertOne(entity);
        System.out.println("Created entity with id -> " + created.getId());

        User updated = UpdateTemplate.updateOne(created, Map.of("firstName", "Jani"));
        System.out.println("Updated entity id -> " + updated.getId() + " with firstName " + updated.getFirstName());
    }

    private static void createAccountEntity() {
        Address address = new Address("Madison", "WI", "53716");
        User user = new User(null, "Jua", "kazi", "jua.kazi@email.com", address, LocalDate.now());

        Account account = new Account(null, "juakazi", "kazi_code", user, LocalDate.now());
        Account created = InsertTemplate.insertOne(account);
        System.out.println("Created entity with id -> " + created.getId());
    }

    private static void retrieveUserEntity() {
        User user = new User();
        EntityInfo entityInfo = user.getEntityInfo();
        String searchId = "39b8408e-c6db-11eb-81b0-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            User entity = dbSelect.selectByIdColumn(user, entityInfo.getTableName(), pkColumn, pkValue);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void retrieveAccountEntity() {
        Account account = new Account();
        EntityInfo entityInfo = account.getEntityInfo();
        String searchId = "39c373fa-c6db-11eb-81b0-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Account entity = dbSelect.selectByIdColumn(account, entityInfo.getTableName(), pkColumn, pkValue);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);
        }
    }

    private static void retrieveTaskEntity() {
        Task task = new Task();
        EntityInfo entityInfo = task.getEntityInfo();
        String searchId = "39c743ea-c6db-11eb-81b0-0242ac110002";
        UUID pkValue = UUID.fromString(searchId);
        FieldInfo idField = entityInfo.getFields().stream().filter(
                field -> field.isId
        ).findFirst().orElse(null);
        if (idField != null) {
            String pkColumn = idField.columnName;
            Task entity = dbSelect.selectByIdColumn(task, entityInfo.getTableName(), pkColumn, pkValue);
            System.out.println("Found entity -> " + entity.getId().toString() + ", expected UUID -> " + searchId);

            //delete task
//            Task deleted = DeleteTemplate.deleteOne(entity);
//            System.out.println("Deleted entity -> " + deleted.getId().toString());
        }
    }
}
