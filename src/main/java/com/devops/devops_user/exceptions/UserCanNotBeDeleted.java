package com.devops.devops_user.exceptions;

public class UserCanNotBeDeleted extends RuntimeException {
    public UserCanNotBeDeleted(String message) {
        super(message);
    }
}
