package edu.neu.madcourse.numadsp21finalproject.model;

import java.util.List;

public class User {
    private String email;
    private List<String> categories;

    public User(String email, List<String> categories) {
        this.email = email;
        this.categories = categories;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getCategories() {
        return categories;
    }
}
