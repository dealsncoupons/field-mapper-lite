package works.hop.field.fetcher;

public interface FetchContext {

    <S> S source();

    <T> T target();

    FetchCache localCache();

    <T, R> FetchFunction<T, R> resolver(String key);

    void mapLtoR(String left, String right);

    <T, R> void mapLtoR(String left, String right, FetchFunction<T, R> resolver);
}
