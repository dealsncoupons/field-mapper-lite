// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample2.hydrate.entity.builder;

import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.sample2.hydrate.entity.Assignment;
import works.hop.sample2.hydrate.entity.Task;
import works.hop.sample2.hydrate.entity.User;

public class AssignmentBuilder {
  private UUID id;

  private Task task;

  private User assignee;

  private LocalDateTime dateAssigned;

  private AssignmentBuilder() {
  }

  public static AssignmentBuilder newBuilder() {
    return new AssignmentBuilder();
  }

  public AssignmentBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public AssignmentBuilder task(Task task) {
    this.task = task;
    return this;
  }

  public AssignmentBuilder assignee(User assignee) {
    this.assignee = assignee;
    return this;
  }

  public AssignmentBuilder dateAssigned(LocalDateTime dateAssigned) {
    this.dateAssigned = dateAssigned;
    return this;
  }

  public Assignment build() {
    return new Assignment(id,task,assignee,dateAssigned);
  }
}
