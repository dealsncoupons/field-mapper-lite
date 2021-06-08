// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity.builder;

import java.lang.Boolean;
import java.lang.String;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import works.hop.sample2.hydrate.entity.Task;

public class TaskBuilder {
  private UUID id;

  private String name;

  private Boolean completed;

  private LocalDate dateCreated;

  private Task nextTask;

  private Task dependsOn;

  private Collection<Task> subTasks;

  private TaskBuilder() {
  }

  public static TaskBuilder newBuilder() {
    return new TaskBuilder();
  }

  public TaskBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public TaskBuilder name(String name) {
    this.name = name;
    return this;
  }

  public TaskBuilder completed(Boolean completed) {
    this.completed = completed;
    return this;
  }

  public TaskBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public TaskBuilder nextTask(Task nextTask) {
    this.nextTask = nextTask;
    return this;
  }

  public TaskBuilder dependsOn(Task dependsOn) {
    this.dependsOn = dependsOn;
    return this;
  }

  public TaskBuilder subTasks(Collection<Task> subTasks) {
    this.subTasks = subTasks;
    return this;
  }

  public Task build() {
    return new Task(id,name,completed,dateCreated,nextTask,dependsOn,subTasks);
  }
}
