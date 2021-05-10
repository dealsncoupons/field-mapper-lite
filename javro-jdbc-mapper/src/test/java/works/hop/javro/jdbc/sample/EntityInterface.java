package works.hop.javro.jdbc.sample;

import java.util.List;

public interface EntityInterface {

    String getName();

    Boolean getCompleted();

    List<EntityInterface> getSubTasks();
}
