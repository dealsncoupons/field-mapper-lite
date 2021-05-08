package works.hop.field.fetcher.impl;

import works.hop.field.fetcher.FetchConsumer;
import works.hop.field.fetcher.FetchContext;
import works.hop.field.fetcher.FetchCoupler;
import works.hop.field.fetcher.FetchProducer;

public class FetchCouplerImpl<S, T> implements FetchCoupler<S, T> {

    @Override
    public void fetch(FetchContext context, FetchProducer<S> provider, FetchConsumer<T> consumer) {

    }
}
