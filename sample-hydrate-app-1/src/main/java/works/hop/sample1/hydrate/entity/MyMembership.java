// This entity class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity;

import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.NoSuchMethodException;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.hydrate.api.annotation.Column;
import works.hop.hydrate.api.annotation.Id;
import works.hop.hydrate.api.annotation.JoinColumn;
import works.hop.hydrate.api.annotation.Table;
import works.hop.hydrate.api.annotation.Temporal;
import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.Hydrate;
import works.hop.hydrate.jdbc.context.InsertTemplate;
import works.hop.hydrate.jdbc.context.LocalCache;
import works.hop.hydrate.jdbc.relations.EntityInfo;
import works.hop.hydrate.jdbc.relations.EntityQuery;
import works.hop.hydrate.jdbc.relations.FieldInfo;
import works.hop.hydrate.jdbc.relations.FieldInfoBuilder;

@Table("tbl_membership")
public class MyMembership implements IMyMembership {
  private static final Logger log = LoggerFactory.getLogger(MyMembership.class);

  public static final EntityInfo entityInfo = initEntityInfo();

  @Id
  private UUID id;

  @Column(
      value = "date_created",
      updatable = false
  )
  @Temporal
  private LocalDate dateCreated;

  @Column("last_updated")
  @Temporal
  private LocalDateTime lastUpdated;

  @JoinColumn(
      value = "member_id",
      fkTable = "tbl_member"
  )
  private Member member;

  @JoinColumn(
      value = "club_id",
      fkTable = "tbl_membership",
      manyToOne = true
  )
  private Collection<Club> club;

  @Column("member_alias")
  private String alias;

  @Column("member_status")
  private String status;

  public MyMembership() {
  }

  public MyMembership(final UUID id, final LocalDate dateCreated, final LocalDateTime lastUpdated,
      final Member member, final Collection<Club> club, final String alias, final String status) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.lastUpdated = lastUpdated;
    this.member = member;
    this.club = club;
    this.alias = alias;
    this.status = status;
  }

  private static EntityInfo initEntityInfo() {
    List<FieldInfo> fields = new ArrayList<>();
    EntityInfo entityInfo = new EntityInfo();
    entityInfo.setTableName("tbl_membership");
    FieldInfo id = FieldInfoBuilder.builder().name("id").isId(true).type(java.util.UUID.class).build();
    fields.add(id);
    FieldInfo dateCreated = FieldInfoBuilder.builder().name("dateCreated").columnName("date_created").updatable(false).type(java.time.LocalDate.class).temporal(true).build();
    fields.add(dateCreated);
    FieldInfo lastUpdated = FieldInfoBuilder.builder().name("lastUpdated").columnName("last_updated").type(java.time.LocalDateTime.class).temporal(true).build();
    fields.add(lastUpdated);
    FieldInfo member = FieldInfoBuilder.builder().name("member").relational(true).columnName("member_id").joinTable("tbl_member").type(works.hop.sample1.hydrate.entity.Member.class).build();
    fields.add(member);
    FieldInfo club = FieldInfoBuilder.builder().name("club").relational(true).columnName("club_id").joinTable("tbl_membership").type(works.hop.sample1.hydrate.entity.Club.class).collection(true).build();
    fields.add(club);
    FieldInfo alias = FieldInfoBuilder.builder().name("alias").columnName("member_alias").build();
    fields.add(alias);
    FieldInfo status = FieldInfoBuilder.builder().name("status").columnName("member_status").build();
    fields.add(status);
    entityInfo.setFields(fields);
    return entityInfo;
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  @Override
  public LocalDate getDateCreated() {
    return this.dateCreated;
  }

  @Override
  public LocalDateTime getLastUpdated() {
    return this.lastUpdated;
  }

  @Override
  public Member getMember() {
    return this.member;
  }

  @Override
  public Collection<Club> getClub() {
    return this.club;
  }

  @Override
  public String getAlias() {
    return this.alias;
  }

  @Override
  public String getStatus() {
    return this.status;
  }

  @Override
  public EntityInfo getEntityInfo() {
    return entityInfo;
  }

  public <O> O get(String property) {
    switch (property) {
      case "id": 
      return (O) this.id;
      case "dateCreated": 
      return (O) this.dateCreated;
      case "lastUpdated": 
      return (O) this.lastUpdated;
      case "member": 
      return (O) this.member;
      case "club": 
      return (O) this.club;
      case "alias": 
      return (O) this.alias;
      case "status": 
      return (O) this.status;
      default: 
      return null;
    }
  }

  public <O> void set(String property, O value) {
    switch (property) {
      case "id": 
      this.id = (java.util.UUID) value; 
      break;
      case "dateCreated": 
      this.dateCreated = (java.time.LocalDate) value; 
      break;
      case "lastUpdated": 
      this.lastUpdated = (java.time.LocalDateTime) value; 
      break;
      case "member": 
      this.member = (works.hop.sample1.hydrate.entity.Member) value; 
      break;
      case "club": 
      this.club = (java.util.Collection<works.hop.sample1.hydrate.entity.Club>) value; 
      break;
      case "alias": 
      this.alias = (java.lang.String) value; 
      break;
      case "status": 
      this.status = (java.lang.String) value; 
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
    entityInfo.getFields().stream().filter(fieldInfo -> fieldInfo.isRelational).forEach(fieldInfo -> {
        if (!fieldInfo.isCollection) {
            if (this.get(fieldInfo.name) != null) {
                set(fieldInfo.name, InsertTemplate.insertOne(get(fieldInfo.name), connection));
            }
        } else {
            if (this.get(fieldInfo.name) != null) {
                Collection<Hydrate> collection = get(fieldInfo.name);
                set(fieldInfo.name, collection.stream().map(entity -> InsertTemplate.insertOne(entity, connection)).collect(Collectors.toList()));
            }
        }
    });

    Map<String, Optional<Object>> parameters = new LinkedHashMap<>();
    extractEntityValues(parameters, this);

    String[] orderedColumns = parameters.entrySet().stream().filter(entry -> entry.getValue().isPresent())
            .map(Map.Entry::getKey).toArray(String[]::new);
    String query = EntityQuery.getInstance().insertOne(entityInfo.getTableName(), orderedColumns);

    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        for (int i = 0; i < orderedColumns.length; i++) { //maintains order of columns-to-values as they appear in the query
            if(parameters.get(orderedColumns[i]).isPresent()) {
                ps.setObject(i + 1, parameters.get(orderedColumns[i]).get());
            }
        }

        int rowsAffected = ps.executeUpdate();
        log.info("{} row(s) affected after insert operation", rowsAffected);

        try (ResultSet keys = ps.getGeneratedKeys()) {
           if (keys.next()) {
              this.id = UUID.fromString(keys.getString(1));
           }
        } catch (SQLException e) {
           e.printStackTrace();
           log.warn("Could not retrieve generated id value", e);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Problem executing insert query", e);
    }

    return (E) this;
  }

  @Override
  public <E extends Hydrate> E select(ResultSet rs, DbSelector resolver, Connection connection,
      LocalCache cache) {
    try {
        this.id = rs.getObject("id", UUID.class);
        String tableName =entityInfo.getTableName();
        if (cache.get(this.id, tableName).isPresent()) {
            return (E) cache.get(this.id, tableName).get();
        } else {
            cache.add(this.id, this, tableName);
            for(FieldInfo field : entityInfo.getFields()){
                if(field.isRelational){
                    if(!field.isCollection){
                        UUID fieldId = rs.getObject(field.columnName, UUID.class);
                        if (fieldId != null) {
                            set(field.name, resolver.selectByIdColumn((Hydrate)field.type.getConstructor().newInstance(), field.joinTable, "id", fieldId, connection));
                        }
                    }
                    else{
                       Supplier<Hydrate> newInstance = () -> {
                           try {
                               return (Hydrate)field.type.getConstructor().newInstance();
                           } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                               e.printStackTrace();
                               throw new RuntimeException(String.format("Failed to create a new instance of %s", field.type));
                           }
                       };
                       set(field.name, resolver.selectByJoinColumn(newInstance, entityInfo.getTableName(), "id", field.joinTable, field.columnName, field.columnName, this.id, connection));
                    }
                }
                else if(field.isEmbedded){
                    Hydrate embeddedField = get(field.name);
                    if(embeddedField != null){
                        embeddedField.select(rs, resolver, connection, cache);
                    }
                    else{
                        set(field.name, ((Hydrate)field.type.getConstructor().newInstance()).select(rs, resolver, connection, cache));
                    }
                }
                else{
                    if(field.isTemporal) {
                         set(field.name, rs.getObject(field.columnName, field.type));
                     }
                     else{
                         set(field.name, rs.getObject(field.columnName));
                     }
                }
            }
        }
    } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
        throw new RuntimeException("Cannot resolve a property", e);
    }
    return (E)this;
  }

  @Override
  public <E extends Hydrate> E update(Map<String, Object> columnValues, Connection connection) {
    applyEntityValues(columnValues, this); 

    Map<String, Optional<Object>> parameters = new LinkedHashMap<>();
    extractEntityValues(parameters, this);

    String[] idColumns = {"id"};
    String[] valueColumns = parameters.keySet().stream()
            .filter(o -> Arrays.stream(idColumns).noneMatch(i -> i.equals(o))).toArray(String[]::new);
    String query = EntityQuery.getInstance().updateOne(entityInfo.getTableName(), idColumns, valueColumns);

    String[] orderedColumns = Arrays.copyOf(valueColumns, valueColumns.length + idColumns.length);
    System.arraycopy(idColumns, 0, orderedColumns, valueColumns.length, idColumns.length);
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        for (int i = 0; i < orderedColumns.length; i++) { //maintains order of columns-to-values as they appear in the query
            ps.setObject(i + 1, parameters.get(orderedColumns[i]).orElse(null));
        }

        int rowsAffected = ps.executeUpdate();
        log.info("{} row(s) affected after update operation", rowsAffected);
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Problem executing update query", e);
    }

    return (E) this;
  }

  @Override
  public <E extends Hydrate> E delete(Connection connection) {
    String query = EntityQuery.getInstance().deleteOne(entityInfo.getTableName(), new String[]{"id"});
     try (PreparedStatement ps = connection.prepareStatement(query)) {
         ps.setObject(1, this.getId());

         int rowsAffected = ps.executeUpdate();
         log.info("{} row(s) affected after delete operation", rowsAffected);
     } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Problem executing delete query", e);
     }
     return (E)this;
  }
}
