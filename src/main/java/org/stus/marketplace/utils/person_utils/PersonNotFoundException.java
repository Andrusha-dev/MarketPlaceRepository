package org.stus.marketplace.utils.person_utils;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String msg) {
        super(msg);
    }
}
