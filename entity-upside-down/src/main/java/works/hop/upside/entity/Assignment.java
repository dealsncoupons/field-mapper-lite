// This entity class is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.annotation.Metadata;
import works.hop.javro.jdbc.annotation.Table;
import works.hop.javro.jdbc.annotation.Temporal;
import works.hop.upside.context.DbSelect;
import works.hop.upside.context.Hydrate;
import works.hop.upside.context.InsertTemplate;
import works.hop.upside.context.LocalCache;
import works.hop.upside.relations.EntityInfo;
import works.hop.upside.relations.EntityQuery;
import works.hop.upside.relations.FieldInfo;
import works.hop.upside.relations.FieldInfoBuilder;

@Table("tbl_assignment")
public class Assignment implements IAssignment {
  private static final Logger log = LoggerFactory.getLogger(Assignment.class);

  @Id
  private UUID id;

  @JoinColumn("task_id")
  private Task task;

  @JoinColumn("assignee_id")
  private User assignee;

  @Column("date_assigned")
  @Temporal
  private LocalDateTime dateAssigned;

  @Metadata
  private final EntityInfo entityInfo;

  public Assignment() {
    this.entityInfo = initEntityInfo();
  }

  public Assignment(final UUID id, final Task task, final User assignee,
      final LocalDateTime dateAssigned) {
    this.id = id;
    this.task = task;
    this.assignee = assignee;
    this.dateAssigned = dateAssigned;
    this.entityInfo = initEntityInfo();
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  @Override
  public Task getTask() {
    return this.task;
  }

  @Override
  public User getAssignee() {
    return this.assignee;
  }

  @Override
  public LocalDateTime getDateAssigned() {
    return this.dateAssigned;
  }

  @Override
  public EntityInfo getEntityInfo() {
    return this.entityInfo;
  }

  @Override
  public EntityInfo initEntityInfo() {
    List<FieldInfo> fields = new ArrayList<>();
    EntityInfo entityInfo = new EntityInfo();
    entityInfo.setTableName("tbl_assignment");
    FieldInfo id = FieldInfoBuilder.builder().name("id").isId(true).type(java.util.UUID.class).build();
    fields.add(id);
    FieldInfo task = FieldInfoBuilder.builder().name("task").relational(true).columnName("task_id").type(works.hop.upside.entity.Task.class).build();
    fields.add(task);
    FieldInfo assignee = FieldInfoBuilder.builder().name("assignee").relational(true).columnName("assignee_id").type(works.hop.upside.entity.User.class).build();
    fields.add(assignee);
    FieldInfo dateAssigned = FieldInfoBuilder.builder().name("dateAssigned").columnName("date_assigned").type(java.time.LocalDateTime.class).temporal(true).build();
    fields.add(dateAssigned);
    entityInfo.setFields(fields);
    return entityInfo;
  }

  public <O> O get(String property) {
    switch (property) {
      case "id": 
      return (O) this.id;
      case "task": 
      return (O) this.task;
      case "assignee": 
      return (O) this.assignee;
      case "dateAssigned": 
      return (O) this.dateAssigned;
      default: 
      return null;
    }
  }

  public <O> void set(String property, O value) {
    switch (property) {
      case "id": 
      this.id = (java.util.UUID) value; 
      break;
      case "task": 
      this.task = (works.hop.upside.entity.Task) value; 
      break;
      case "assignee": 
      this.assignee = (works.hop.upside.entity.User) value; 
      break;
      case "dateAssigned": 
      this.dateAssigned = (java.time.LocalDateTime) value; 
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
    getEntityInfo().getFields().stream().filter(fieldInfo -> fieldInfo.isRelational).forEach(fieldInfo -> {
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
    extractEntityValues(parameters, this, this.entityInfo);

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
  public <E extends Hydrate> E select(ResultSet rs, DbSelect resolver, Connection connection,
      LocalCache cache) {
    try {
        this.id = rs.getObject("id", UUID.class);
        String tableName =getEntityInfo().getTableName();
        if (cache.get(this.id, tableName).isPresent()) {
            return (E) cache.get(this.id, tableName).get();
        } else {
            cache.add(this.id, this, tableName);
            for(FieldInfo field : getEntityInfo().getFields()){
                if(field.isRelational){
                    if(!field.isCollection){
                        UUID fieldId = rs.getObject(field.columnName, UUID.class);
                        if (fieldId != null) {
                            set(field.name, resolver.selectByIdColumn((Hydrate)field.type.getConstructor().newInstance(), field.joinTable, "id", fieldId, connection));
                        }
                    }
                    else{
                        set(field.name, resolver.selectByJoinColumn(Task::new, entityInfo.getTableName(), "id", field.joinTable, field.columnName, field.columnName, this.id, connection));
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
    columnValues.forEach(this::set);

    Map<String, Optional<Object>> parameters = new LinkedHashMap<>();
    extractEntityValues(parameters, this, this.entityInfo);

    String[] idColumns = {"id"};
    String[] valueColumns = parameters.keySet().stream()
            .filter(o -> Arrays.stream(idColumns).noneMatch(i -> i.equals(o))).toArray(String[]::new);
    String query = EntityQuery.getInstance().updateOne(entityInfo.getTableName(), idColumns, valueColumns);

    String[] orderedColumns = Arrays.copyOf(valueColumns, valueColumns.length + idColumns.length);
    System.arraycopy(idColumns, 0, orderedColumns, valueColumns.length, idColumns.length);
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        for (int i = 0; i < orderedColumns.length; i++) { //maintains order of columns-to-values as they appear in the query
            ps.setObject(i + 1, parameters.get(orderedColumns[i]).get());
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
