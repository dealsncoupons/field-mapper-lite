package works.hop.field.jdbc.example;

import java.util.List;
import java.util.Map;

public class TaskA extends TaskB {

    String description;
    TaskC cTask;
    List<TaskC> cTaskList;

    public TaskA() {
        super();
    }

    public TaskA(String description, TaskC cTask, List<TaskC> cTaskList) {
        this.description = description;
        this.cTask = cTask;
        this.cTaskList = cTaskList;
    }

    public TaskA(Long id, TaskC taskC, Map<String, TaskC> cTaskMap, String description, TaskC cTask, List<TaskC> cTaskList, Map<TaskC, Integer> cIntMap) {
        super(id, taskC, cTaskMap, cIntMap);
        this.description = description;
        this.cTask = cTask;
        this.cTaskList = cTaskList;
    }
}
