package com.natimeital.friendist;

/**
 * Created by nati on 5/20/2016.
 */
public class SearchableUserDetails {
    public int id;
    public String username, firstName,lastName,age,photoLink,eMail;

    //empty constructor


    public SearchableUserDetails() {
    }

    //full constructor
    public SearchableUserDetails(int id, String username, String firstName, String lastName, String age, String photoLink, String eMail) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.photoLink = photoLink;
        this.eMail = eMail;
    }
    //search user constructor


    public SearchableUserDetails(String username, String firstName, String lastName, String photoLink) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoLink = photoLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Override
    public String toString() {
        return "SearchableUserDetails{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age='" + age + '\'' +
                ", photoLink='" + photoLink + '\'' +
                ", eMail='" + eMail + '\'' +
                '}';
    }
}
