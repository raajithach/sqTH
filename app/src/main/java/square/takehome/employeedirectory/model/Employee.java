package square.takehome.employeedirectory.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    public enum EmployeeType {
        FULL_TIME,
        PART_TIME,
        CONTRACTOR
    }

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("full_name")
    private String full_name;

    @JsonProperty("phone_number")
    private String phone_number;
    @JsonProperty("email_address")
    private String email_address;

    @JsonProperty("biography")
    private String biography;

    @JsonProperty("photo_url_small")
    private String photo_url_small;

    @JsonProperty("photo_url_large")
    private String photo_url_large;
    @JsonProperty("team")
    private String team;
    @JsonProperty("employee_type")
    private square.takehome.employeedirectory.model.Employee.EmployeeType employeeType;


    @JsonCreator
    public Employee(@JsonProperty(value = "uuid", required = true) String uuid,
                    @JsonProperty(value = "full_name", required = true) String full_name,
                    @JsonProperty(value = "phone_number") String phone_number,
                    @JsonProperty(value = "email_address", required = true) String email_address,
                    @JsonProperty(value = "biography") String biography,
                    @JsonProperty(value = "photo_url_small") String photo_url_small,
                    @JsonProperty(value = "photo_url_large") String photo_url_large,
                    @JsonProperty(value = "team", required = true) String team,
                    @JsonProperty(value = "employee_type", required = true) EmployeeType employeeType) {
        this.uuid = uuid;
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email_address = email_address;
        this.biography = biography;
        this.photo_url_small = photo_url_small;
        this.photo_url_large = photo_url_large;
        this.team = team;
        this.employeeType = employeeType;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("full_name")
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getTeam() {
        return team;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getPhoto_url_small() {
        return photo_url_small;
    }
}
