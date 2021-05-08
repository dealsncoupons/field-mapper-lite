package works.hop.field.fetcher.impl;

import works.hop.field.fetcher.FetchProducer;

import java.util.function.Function;

public class FetchProducerImpl<T> implements FetchProducer<T> {

    @Override
    public String produce(String fieldName, Function<T, String> resolver) {
        return null;
    }
}
