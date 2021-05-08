package works.hop.field.fetcher;

public interface FetchCoupler<S, T> {

    void fetch(FetchContext context, FetchProducer<S> provider, FetchConsumer<T> consumer);
}
