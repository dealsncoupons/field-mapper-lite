package works.hop.field.jdbc.example;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskE extends TaskF {

    String description;
    TaskG gTask;
    List<TaskG> gTaskList;

    public TaskE() {
        super();
    }

    public TaskE(String description, TaskG gTask, List<TaskG> gTaskList) {
        this.description = description;
        this.gTask = gTask;
        this.gTaskList = gTaskList;
    }

    public TaskE(Long id, TaskG taskG, Map<String, TaskG> gTaskMap, String description, TaskG gTask, List<TaskG> gTaskList, Set<TaskG> gTaskSet) {
        super(id, taskG, gTaskMap, gTaskSet);
        this.description = description;
        this.gTask = gTask;
        this.gTaskList = gTaskList;
    }
}
