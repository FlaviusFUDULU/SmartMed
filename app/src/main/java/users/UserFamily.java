package users;

/**
 * Created by ffudulu on 02-Jun-17.
 */

public class UserFamily {

    private String email;
    private String firstName;
    private String lastName;
    private String pacientUID;


    public UserFamily(String email, String firstName, String lastName, String pacientUID) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pacientUID = pacientUID;
    }

    public UserFamily() {
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPacientUID() {
        return pacientUID;
    }
}
