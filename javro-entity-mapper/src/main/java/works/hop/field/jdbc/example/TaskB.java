package works.hop.field.jdbc.example;

import java.util.Map;

public class TaskB {

    Long id;
    TaskC taskC;
    Map<String, TaskC> cTaskMap;
    Map<TaskC, Integer> cIntMap;

    public TaskB() {
        super();
    }

    public TaskB(Long id, TaskC taskC, Map<String, TaskC> cTaskMap, Map<TaskC, Integer> cIntMap) {
        this.id = id;
        this.taskC = taskC;
        this.cTaskMap = cTaskMap;
        this.cIntMap = cIntMap;
    }
}
