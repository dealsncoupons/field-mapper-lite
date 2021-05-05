package works.hop.field.data.fetcher;

import java.util.function.Function;

public interface FetchProducer<T> {

    String produce(String fieldName, Function<T, String> resolver);
}
