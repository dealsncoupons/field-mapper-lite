package works.hop.javro.jdbc.sample;

public interface Accessible {

    default <O> O get(String property){return null;};

    default <O> void set(String property, O value){};
}
