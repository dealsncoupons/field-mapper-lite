package works.hop.field.data.fetcher.impl;

import works.hop.field.data.fetcher.FetchConsumer;
import works.hop.field.data.fetcher.FetchContext;
import works.hop.field.data.fetcher.FetchCoupler;
import works.hop.field.data.fetcher.FetchProducer;

public class FetchCouplerImpl<S, T> implements FetchCoupler<S, T> {

    @Override
    public void fetch(FetchContext context, FetchProducer<S> provider, FetchConsumer<T> consumer) {

    }
}
