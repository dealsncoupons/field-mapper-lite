// This entity class is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity;

import java.lang.Boolean;
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

@Table("tbl_task")
public class Task implements ITask {
  private static final Logger log = LoggerFactory.getLogger(Task.class);

  @Id
  private UUID id;

  private String name;

  @Column("done")
  private Boolean completed;

  @Column(
      value = "date_created",
      updatable = false
  )
  @Temporal
  private LocalDate dateCreated;

  @JoinColumn(
      value = "next_task",
      fkTable = "tbl_task"
  )
  private Task nextTask;

  @JoinColumn(
      value = "parent_task",
      fkTable = "tbl_task"
  )
  private Task dependsOn;

  @JoinColumn(
      value = "parent_task",
      fkTable = "tbl_task",
      manyToOne = true
  )
  private Collection<Task> subTasks;

  @Metadata
  private final EntityInfo entityInfo;

  public Task() {
    this.entityInfo = initEntityInfo();
  }

  public Task(final UUID id, final String name, final Boolean completed,
      final LocalDate dateCreated, final Task nextTask, final Task dependsOn,
      final Collection<Task> subTasks) {
    this.id = id;
    this.name = name;
    this.completed = completed;
    this.dateCreated = dateCreated;
    this.nextTask = nextTask;
    this.dependsOn = dependsOn;
    this.subTasks = subTasks;
    this.entityInfo = initEntityInfo();
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Boolean getCompleted() {
    return this.completed;
  }

  @Override
  public LocalDate getDateCreated() {
    return this.dateCreated;
  }

  @Override
  public Task getNextTask() {
    return this.nextTask;
  }

  @Override
  public Task getDependsOn() {
    return this.dependsOn;
  }

  @Override
  public Collection<Task> getSubTasks() {
    return this.subTasks;
  }

  @Override
  public EntityInfo getEntityInfo() {
    return this.entityInfo;
  }

  @Override
  public EntityInfo initEntityInfo() {
    List<FieldInfo> fields = new ArrayList<>();
    EntityInfo entityInfo = new EntityInfo();
    entityInfo.setTableName("tbl_task");
    FieldInfo id = FieldInfoBuilder.builder().name("id").isId(true).type(java.util.UUID.class).build();
    fields.add(id);
    FieldInfo name = FieldInfoBuilder.builder().name("name").build();
    fields.add(name);
    FieldInfo completed = FieldInfoBuilder.builder().name("completed").columnName("done").type(boolean.class).build();
    fields.add(completed);
    FieldInfo dateCreated = FieldInfoBuilder.builder().name("dateCreated").columnName("date_created").updatable(false).type(java.time.LocalDate.class).temporal(true).build();
    fields.add(dateCreated);
    FieldInfo nextTask = FieldInfoBuilder.builder().name("nextTask").relational(true).columnName("next_task").joinTable("tbl_task").type(works.hop.upside.entity.Task.class).build();
    fields.add(nextTask);
    FieldInfo dependsOn = FieldInfoBuilder.builder().name("dependsOn").relational(true).columnName("parent_task").joinTable("tbl_task").type(works.hop.upside.entity.Task.class).build();
    fields.add(dependsOn);
    FieldInfo subTasks = FieldInfoBuilder.builder().name("subTasks").relational(true).columnName("parent_task").joinTable("tbl_task").type(works.hop.upside.entity.Task.class).collection(true).build();
    fields.add(subTasks);
    entityInfo.setFields(fields);
    return entityInfo;
  }

  public <O> O get(String property) {
    switch (property) {
      case "id": 
      return (O) this.id;
      case "name": 
      return (O) this.name;
      case "completed": 
      return (O) this.completed;
      case "dateCreated": 
      return (O) this.dateCreated;
      case "nextTask": 
      return (O) this.nextTask;
      case "dependsOn": 
      return (O) this.dependsOn;
      case "subTasks": 
      return (O) this.subTasks;
      default: 
      return null;
    }
  }

  public <O> void set(String property, O value) {
    switch (property) {
      case "id": 
      this.id = (java.util.UUID) value; 
      break;
      case "name": 
      this.name = (java.lang.String) value; 
      break;
      case "completed": 
      this.completed = (java.lang.Boolean) value; 
      break;
      case "dateCreated": 
      this.dateCreated = (java.time.LocalDate) value; 
      break;
      case "nextTask": 
      this.nextTask = (works.hop.upside.entity.Task) value; 
      break;
      case "dependsOn": 
      this.dependsOn = (works.hop.upside.entity.Task) value; 
      break;
      case "subTasks": 
      this.subTasks = (java.util.Collection<works.hop.upside.entity.Task>) value; 
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
