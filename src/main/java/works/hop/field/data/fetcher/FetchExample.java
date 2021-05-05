package works.hop.field.data.fetcher;

import works.hop.field.jdbc.example.ItemC;

public class FetchExample {

    static class ExampleCoupler implements FetchCoupler<ItemC, ItemC> {

        @Override
        public void fetch(FetchContext context, FetchProducer<ItemC> provider, FetchConsumer<ItemC> consumer) {
            
        }
    }

    public static void main(String[] args) {

    }
}
