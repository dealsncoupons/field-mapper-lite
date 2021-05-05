package works.hop.field.data.fetcher;

public interface FetchCoupler<S, T> {

    void fetch(FetchContext context, FetchProducer<S> provider, FetchConsumer<T> consumer);
}
