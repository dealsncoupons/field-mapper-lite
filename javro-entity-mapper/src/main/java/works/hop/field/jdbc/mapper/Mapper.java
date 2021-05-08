package works.hop.field.jdbc.mapper;

public interface Mapper<A, B> {

    B mapAtoB(A source, Class<B> target);

    void mapField(String inA, String inB);
}
