package works.hop.javro.jdbc.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreatingEntitiesApp {

    public static void main(String[] args) {
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
        task122.put("name", "pour mixed ingredients");
        task122.put("completed", false);

        Map<String, Object> task123 = new HashMap<>();
        task123.put("name", "serve when ready");
        task123.put("completed", false);

        List<EntityInterface> task1SubList = new LinkedList<>();
        task1SubList.add(EntityProxyFactory.create(EntityInterface.class, task11));
        task1SubList.add(EntityProxyFactory.create(EntityInterface.class, task12));
        task1.put("subTasks", task1SubList);

        List<EntityInterface> task12SubList = new LinkedList<>();
        task12SubList.add(EntityProxyFactory.create(EntityInterface.class, task121));
        task12SubList.add(EntityProxyFactory.create(EntityInterface.class, task122));
        task12SubList.add(EntityProxyFactory.create(EntityInterface.class, task123));
        task12.put("subTasks", task12SubList);

        EntityInterface theTask = EntityProxyFactory.create(EntityInterface.class, task1);
        printTasks("", List.of(theTask));
    }

    public static void printTasks(String indent, List<EntityInterface> tasks){
        tasks.forEach(task -> {
            System.out.println(indent + "task name = " + task.getName());
            System.out.println(indent + "task completed? = " + task.getCompleted());
            if(task.getSubTasks() != null && !task.getSubTasks().isEmpty()){
                printTasks(indent + "-- ", task.getSubTasks());
            }
        });
    }
}
