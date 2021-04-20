package edu.neu.madcourse.numadsp21finalproject.model;

import java.util.List;

public class User {
    private String email;
    private List<String> categories;

    public User(String email, List<String> categories) {
        this.email = email;
        this.categories = categories;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public List<String> getCategories() {
        return categories;
    }
}
