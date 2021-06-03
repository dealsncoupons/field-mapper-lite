package works.hop.upside.context;

import org.apache.kafka.connect.data.Struct;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

public interface Hydrant {

    <E extends Hydrant> E select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache);

    <E extends Hydrant> E insert(Connection connection);

    <E extends Hydrant> E update(Map<String, Object> columnValues, Connection connection);

    <E extends Hydrant> E delete(Connection connection);

    <E extends Hydrant> E refresh(Struct record);
}
