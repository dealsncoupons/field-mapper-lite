package works.hop.field.jdbc.template;

import java.util.List;
import java.util.Optional;

public interface CrudRepo<T, I> {

    T save(T entity);

    T update(T entity);

    T deleteById(I id);

    int delete(T entity);

    Optional<T> findById(I id);

    List<T> findAll();
}
