package users;

import java.io.Serializable;

/**
 * Created by ffudulu on 22-Apr-17.
 */

public class UserMedic implements Serializable{

    private String email;
    private String firstName;
    private String hospitalName;
    private String lastName;
    private String rank;
    private String sectionName;


    public UserMedic(String email, String firstName, String hospitalName, String lastName, String rank, String sectionName) {
        this.email = email;
        this.firstName = firstName;
        this.hospitalName = hospitalName;
        this.lastName = lastName;
        this.rank = rank;
        this.sectionName = sectionName;
    }

    public UserMedic() {

    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRank() {
        return rank;
    }

    public String getSectionName() {
        return sectionName;
    }
}
