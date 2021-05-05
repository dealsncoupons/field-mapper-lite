package works.hop.field.data.fetcher.impl;

import works.hop.field.data.fetcher.FetchProducer;

import java.util.function.Function;

public class FetchProducerImpl<T> implements FetchProducer<T> {

    @Override
    public String produce(String fieldName, Function<T, String> resolver) {
        return null;
    }
}
