package works.hop.field.fetcher;

import works.hop.field.example.ItemC;

public class FetchExample {

    public static void main(String[] args) {

    }

    static class ExampleCoupler implements FetchCoupler<ItemC, ItemC> {

        @Override
        public void fetch(FetchContext context, FetchProducer<ItemC> provider, FetchConsumer<ItemC> consumer) {

        }
    }
}
