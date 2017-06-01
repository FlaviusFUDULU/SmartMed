package users;

/**
 * Created by ffudulu on 06-Apr-17.
 */
public class UserPersonalData{
    private String firstName;
    private String lastName;
    private String email;
    private String CNP;
    private String ID;
    private String Age;
    //private Uri photoUrl;

    public UserPersonalData() {

    }

    public UserPersonalData(String firstName, String lastName, String email, String CNP, String ID, String age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CNP = CNP;
        this.ID = ID;
        Age = age;
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

    public String getCNP() {
        return CNP;
    }

    public String getID() {
        return ID;
    }

    public String getAge() {
        return Age;
    }
}
