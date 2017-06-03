package users;

import java.io.Serializable;

/**
 * Created by ffudulu on 06-Apr-17.
 */
public class UserPersonalData implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String cnp;
    private String id;
    private String age;
    //private Uri photoUrl;


    public UserPersonalData(String firstName, String lastName, String email, String cnp, String id, String age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cnp = cnp;
        this.id = id;
        this.age = age;
    }

    public UserPersonalData() {

    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCnp() {
        return cnp;
    }

    public String getId() {
        return id;
    }

    public String getAge() {
        return age;
    }
}
