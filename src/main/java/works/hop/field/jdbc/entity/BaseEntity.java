package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class BaseEntity {

    @Id
    public UUID id;
    @Column("date_created")
    public LocalDate dateCreated;
    @Column("last_updated")
    public LocalDateTime lastUpdated;

    public BaseEntity() {
        super();
    }

    public BaseEntity(UUID id, LocalDate dateCreated, LocalDateTime lastUpdated) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }
}
