package works.hop.upside.entity;

import org.apache.kafka.connect.data.Struct;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {

    private String city;
    private String state;
    private String zipCode;

    public Address() {
        super();
    }

    public Address(String city, String state, String zipCode) {
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Address select(ResultSet rs) {
        synchronized (this) {
            try {
                this.city = rs.getString("addr_city");
                this.state = rs.getString("addr_state_prov");
                this.zipCode = rs.getString("addr_zip_code");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot resolve a property", e);
            }
        }
        return this;
    }

    public Address refresh(Struct record) {
        this.city = record.getString("addr_city");
        this.state = record.getString("addr_state_prov");
        this.zipCode = record.getString("addr_zip_code");
        return this;
    }
}
