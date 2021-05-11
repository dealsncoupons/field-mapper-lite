package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.sample.EntityProxyFactory;
import works.hop.javro.jdbc.sample.EntitySourceFactory;
import works.hop.javro.jdbc.sample.template.InsertTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TodosApp {

    public static void main(String[] args) {
        //createEntityProxies();
//        createEntitySources();
        ITodo task1 = new Todo("make marmalade", false);
        ITodo saved = InsertTemplate.insertOne(task1);
        System.out.println("saved -> " + saved.getName());
    }

    public static void createEntitySources() {
        Todo task1 = new Todo("make breakfast", false);
        Todo task11 = new Todo("pour milk", false);
        Todo task12 = new Todo("make pancake", false);
        Todo task121 = new Todo("heat up skillet", false);
        Todo task122 = new Todo("pour prepared mix", false);
        Todo task123 = new Todo("cook to golden brown", false);
        Todo task13 = new Todo("serve when ready", false);

        task1.getSubTasks().add(EntitySourceFactory.create(task11));
        task1.getSubTasks().add(EntitySourceFactory.create(task12));
        task1.getSubTasks().add(EntitySourceFactory.create(task13));

        task12.getSubTasks().add(EntitySourceFactory.create(task121));
        task12.getSubTasks().add(EntitySourceFactory.create(task122));
        task12.getSubTasks().add(EntitySourceFactory.create(task123));

        ITodo theTask = EntitySourceFactory.create(task1);
        printTasks("", List.of(theTask));
    }

    public static void createEntityProxies() {
        Map<String, Object> task1 = new HashMap<>();
        task1.put("name", "make breakfast");
        task1.put("completed", false);

        Map<String, Object> task11 = new HashMap<>();
        task11.put("name", "pour milk");
        task11.put("completed", false);

        Map<String, Object> task12 = new HashMap<>();
        task12.put("name", "make pancake");
        task12.put("completed", false);

        Map<String, Object> task121 = new HashMap<>();
        task121.put("name", "heat up skillet");
        task121.put("completed", false);

        Map<String, Object> task122 = new HashMap<>();
        task122.put("name", "pour prepared mix");
        task122.put("completed", false);

        Map<String, Object> task123 = new HashMap<>();
        task123.put("name", "cook to golden brown");
        task123.put("completed", false);

        Map<String, Object> task13 = new HashMap<>();
        task13.put("name", "serve when ready");
        task13.put("completed", false);

        List<ITodo> task1SubList = new LinkedList<>();
        task1SubList.add(EntityProxyFactory.create(ITodo.class, task11));
        task1SubList.add(EntityProxyFactory.create(ITodo.class, task12));
        task1SubList.add(EntityProxyFactory.create(ITodo.class, task13));
        task1.put("subTasks", task1SubList);

        List<ITodo> task12SubList = new LinkedList<>();
        task12SubList.add(EntityProxyFactory.create(ITodo.class, task121));
        task12SubList.add(EntityProxyFactory.create(ITodo.class, task122));
        task12SubList.add(EntityProxyFactory.create(ITodo.class, task123));
        task12.put("subTasks", task12SubList);

        ITodo theTask = EntityProxyFactory.create(ITodo.class, task1);
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