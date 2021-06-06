package works.hop.javro.jdbc.sample.todo;

import works.hop.javro.jdbc.annotation.Query;
import works.hop.javro.jdbc.template.CrudRepo;

import java.util.List;
import java.util.UUID;

public interface TodoRepo extends CrudRepo<Todo, UUID> {

    @Query("with recursive tasks as(\n" +
            "SELECT t.*, 1 as level, '/' as path FROM tbl_task t                                                                                                                                                                    \n" +
            "where t.parent_task is null                                                                                                                                                            \n" +
            "UNION  ALL                                                                                                                                                                               \n" +
            "SELECT t1.*, (r.level + 1) as level, (r.path || '/' || t1.name) as path FROM tasks r   \n" +
            "JOIN tbl_task t1 ON r.id = t1.parent_task \n" +
            ")                                                                                                                                                                                     \n" +
            "SELECT * FROM tasks")
    List<Todo> hierarchy();
}
