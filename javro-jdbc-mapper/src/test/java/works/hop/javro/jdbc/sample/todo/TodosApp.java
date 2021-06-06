package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.sample.template.SelectTemplate;
import works.hop.javro.jdbc.sample.template.UpdateTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TodosApp {

    public static void main(String[] args) {
        //createEntityProxies();
        createEntitySources();
//        ITodo task1 = new Todo("make marmalade", false);
//        ITodo saved = InsertTemplate.insertOne(task1);
//        System.out.println("saved -> " + saved.getId());
    }

    public static void createEntitySources() {
//        Todo task1 = new Todo("make breakfast", false);
//        Todo task11 = new Todo("pour milk", false);
//        Todo task12 = new Todo("make pancake", false);
//        Todo task121 = new Todo("heat up skillet", false);
//        Todo task122 = new Todo("pour prepared mix", false);
//        Todo task123 = new Todo("cook to golden brown", false);
//        Todo task13 = new Todo("serve when ready", false);
//
//        task1.getSubTasks().add(task11);
//        task1.getSubTasks().add(task12);
//        task1.getSubTasks().add(task13);
//
//        task12.getSubTasks().add(task121);
//        task12.getSubTasks().add(task122);
//        task12.getSubTasks().add(task123);
//
//        ITodo theTask = EntitySourceFactory.create(task1);
//        printTasks("", List.of(theTask));
//        ITodo saved = InsertTemplate.insertOne(task1);
//        System.out.println("saved -> " + saved.getId());

        //select task just created
        UUID makeBreakfastId = UUID.fromString("fab3906e-b50a-11eb-b13c-0242ac110002"); //saved.getId()
        ITodo makeBreakfast = SelectTemplate.selectOne(ITodo.class, new Object[]{makeBreakfastId}, new HashMap<>());
        System.out.println("selected -> " + makeBreakfast.getId());

        UUID makePancakeId = UUID.fromString("fab6a862-b50a-11eb-b13c-0242ac110002");
        ITodo makePancake = SelectTemplate.selectOne(ITodo.class, new Object[]{makePancakeId}, new HashMap<>());
        System.out.println("selected -> " + makePancake.getId());

        //modify found task
        makeBreakfast.setNextTask(makePancake);

        //updated modified task
        UpdateTemplate.updateOne(makeBreakfast);
        ITodo updatedTask = SelectTemplate.selectOne(ITodo.class, new Object[]{makeBreakfast.getId()}, new HashMap<>());
        System.out.println("updated -> " + updatedTask.getId());
    }

    public static void createEntityProxies() {
        Todo task1 = new Todo();
        task1.set("name", "make breakfast");
        task1.set("completed", false);

        Todo task11 = new Todo();
        task11.set("name", "pour milk");
        task11.set("completed", false);

        Todo task12 = new Todo();
        task12.set("name", "make pancake");
        task12.set("completed", false);

        Todo task121 = new Todo();
        task121.set("name", "heat up skillet");
        task121.set("completed", false);

        Todo task122 = new Todo();
        task122.set("name", "pour prepared mix");
        task122.set("completed", false);

        Todo task123 = new Todo();
        task123.set("name", "cook to golden brown");
        task123.set("completed", false);

        Todo task13 = new Todo();
        task13.set("name", "serve when ready");
        task13.set("completed", false);

        List<ITodo> task1SubList = new LinkedList<>();
        task1SubList.add(task11);
        task1SubList.add(task12);
        task1SubList.add(task13);
        task1.set("subTasks", task1SubList);

        List<ITodo> task12SubList = new LinkedList<>();
        task12SubList.add(task121);
        task12SubList.add(task122);
        task12SubList.add(task123);
        task12.set("subTasks", task12SubList);

        ITodo theTask = task1;
        printTasks("", List.of(theTask));
    }

    public static void printTasks(String indent, List<ITodo> tasks) {
        tasks.forEach(task -> {
            System.out.println(indent + "task name = " + task.getName());
            System.out.println(indent + "task completed? = " + task.getCompleted());
            if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
                printTasks(indent + "-- ", task.getSubTasks());
            }
        });
    }
}
