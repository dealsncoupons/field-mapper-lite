package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Table;

@Table("tbl_club")
public class Club extends BaseEntity {

    public String title;
    public String activity;
}
