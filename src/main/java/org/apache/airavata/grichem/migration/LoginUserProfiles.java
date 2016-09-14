package org.apache.airavata.grichem.migration;

/**
 * Created by abhandar on 9/14/16.
 */
public class LoginUserProfiles {

    private String firstName;
    private String lastName;
    private String middleInitials;
    private String userName;
    private String email;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = middleInitials;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleInitials() {
        return middleInitials;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }


}
