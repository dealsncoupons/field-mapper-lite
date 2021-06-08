// This entity class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.hydrate.api.annotation.Column;
import works.hop.hydrate.api.annotation.Embeddable;
import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.Hydrate;
import works.hop.hydrate.jdbc.context.LocalCache;
import works.hop.hydrate.jdbc.relations.EntityInfo;
import works.hop.hydrate.jdbc.relations.FieldInfo;
import works.hop.hydrate.jdbc.relations.FieldInfoBuilder;

@Embeddable
public class Address implements IAddress {
  private static final Logger log = LoggerFactory.getLogger(Address.class);

  public static final EntityInfo entityInfo = initEntityInfo();

  private String city;

  @Column("state_prov")
  private String state;

  @Column("zip_code")
  private String zipCode;

  public Address() {
  }

  public Address(final String city, final String state, final String zipCode) {
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
  }

  private static EntityInfo initEntityInfo() {
    List<FieldInfo> fields = new ArrayList<>();
    EntityInfo entityInfo = new EntityInfo();
    entityInfo.setTableName("");
    FieldInfo city = FieldInfoBuilder.builder().name("city").build();
    fields.add(city);
    FieldInfo state = FieldInfoBuilder.builder().name("state").columnName("state_prov").build();
    fields.add(state);
    FieldInfo zipCode = FieldInfoBuilder.builder().name("zipCode").columnName("zip_code").build();
    fields.add(zipCode);
    entityInfo.setFields(fields);
    return entityInfo;
  }

  @Override
  public String getCity() {
    return this.city;
  }

  @Override
  public String getState() {
    return this.state;
  }

  @Override
  public String getZipCode() {
    return this.zipCode;
  }

  @Override
  public EntityInfo getEntityInfo() {
    return entityInfo;
  }

  public <O> O get(String property) {
    switch (property) {
      case "city": 
      return (O) this.city;
      case "state": 
      return (O) this.state;
      case "zipCode": 
      return (O) this.zipCode;
      default: 
      return null;
    }
  }

  public <O> void set(String property, O value) {
    switch (property) {
      case "city": 
      this.city = (java.lang.String) value; 
      break;
      case "state": 
      this.state = (java.lang.String) value; 
      break;
      case "zipCode": 
      this.zipCode = (java.lang.String) value; 
      break;
      default: 
      break;
    }
  }

  @Override
  public <E extends Hydrate> E refresh(Struct record) {
     entityInfo.getFields().forEach(field -> {
         if(!field.isCollection){
             set(field.name, field.type.cast(record.get(field.columnName)));
         }
     });
     return (E) this;
  }

  @Override
  public <E extends Hydrate> E insert(Connection connection) {
    log.warn("For embeddable entities, the insert method is not usable");
    return (E) this;
  }

  @Override
  public <E extends Hydrate> E select(ResultSet rs, DbSelector resolver, Connection connection,
      LocalCache cache) {
    try {
        for(FieldInfo field : entityInfo.getFields()){
            set(field.name, rs.getObject(field.columnName, field.type));
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Cannot resolve a property", e);
    }
    return (E)this;
  }

  @Override
  public <E extends Hydrate> E update(Map<String, Object> columnValues, Connection connection) {
    log.warn("For embeddable entities, the update method is not usable");
    return (E) this;
  }

  @Override
  public <E extends Hydrate> E delete(Connection connection) {
    log.warn("For embeddable entities, the delete method is not usable");
    return (E) this;
  }
}
