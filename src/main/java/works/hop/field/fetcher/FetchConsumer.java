package works.hop.field.fetcher;

import java.util.function.Consumer;

public interface FetchConsumer<T> {

    void consume(T target, String field, Consumer<String> consumer);
}
