package com.academicwork.blog;

public class User {

    public final long id;
    public final String userName;
    public final String firstName;
    public final String lastName;
    public final String email;

    public User(long id, String userName, String firstName, String lastName, String email) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

}
