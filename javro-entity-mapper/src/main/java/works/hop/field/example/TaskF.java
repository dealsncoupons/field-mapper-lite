package works.hop.field.example;

import java.util.Map;
import java.util.Set;

public class TaskF {

    Long id;
    TaskG taskG;
    Map<String, TaskG> gTaskMap;
    Set<TaskG> gTaskSet;

    public TaskF() {
        super();
    }

    public TaskF(Long id, TaskG taskG, Map<String, TaskG> gTaskMap, Set<TaskG> gTaskSet) {
        this.id = id;
        this.taskG = taskG;
        this.gTaskMap = gTaskMap;
        this.gTaskSet = gTaskSet;
    }
}
