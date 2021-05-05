package works.hop.field.data.fetcher.impl;

import works.hop.field.data.fetcher.FetchConsumer;

import java.util.function.Consumer;

public class FetchConsumerImpl<T> implements FetchConsumer<T> {

    @Override
    public void consume(T target, String field, Consumer<String> consumer) {

    }
}
