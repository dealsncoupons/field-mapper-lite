package works.hop.field.jdbc.entity;

import works.hop.field.jdbc.annotation.Column;

public class Address {

    public String city;
    @Column("state_prov")
    public String state;
    @Column("zip_code")
    public String zipCode;

    public Address() {
        super();
    }

    public Address(String city, String state, String zipCode) {
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
