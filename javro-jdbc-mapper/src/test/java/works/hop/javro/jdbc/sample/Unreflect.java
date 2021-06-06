package works.hop.javro.jdbc.sample;

public interface Unreflect {

    default <O> O get(String property) {
        return null;
    }

    default <O> void set(String property, O value) {
    }

    default <S extends Unreflect> S source() {
        return null;
    }
}
