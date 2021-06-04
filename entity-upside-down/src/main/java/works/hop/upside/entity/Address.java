// This entity class is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

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
import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Embeddable;
import works.hop.javro.jdbc.annotation.Metadata;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrate;
import works.hop.upside.context.LocalCache;
import works.hop.upside.relations.EntityInfo;
import works.hop.upside.relations.FieldInfo;
import works.hop.upside.relations.FieldInfoBuilder;

@Embeddable
public class Address implements IAddress {
  private static final Logger log = LoggerFactory.getLogger(Address.class);

  @Column("addr_city")
  private String city;

  @Column("addr_state_prov")
  private String state;

  @Column("addr_zip_code")
  private String zipCode;

  @Metadata
  private final EntityInfo entityInfo;

  public Address() {
    this.entityInfo = initEntityInfo();
  }

  public Address(final String city, final String state, final String zipCode) {
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.entityInfo = initEntityInfo();
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
    return this.entityInfo;
  }

  @Override
  public EntityInfo initEntityInfo() {
    List<FieldInfo> fields = new ArrayList<>();
    EntityInfo entityInfo = new EntityInfo();
    entityInfo.setTableName("");
    FieldInfo city = FieldInfoBuilder.builder().name("city").columnName("addr_city").build();
    fields.add(city);
    FieldInfo state = FieldInfoBuilder.builder().name("state").columnName("addr_state_prov").build();
    fields.add(state);
    FieldInfo zipCode = FieldInfoBuilder.builder().name("zipCode").columnName("addr_zip_code").build();
    fields.add(zipCode);
    entityInfo.setFields(fields);
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
  public <E extends Hydrate> E select(ResultSet rs, DbSelect resolver, Connection connection,
      LocalCache cache) {
    try {
        for(FieldInfo field : getEntityInfo().getFields()){
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
