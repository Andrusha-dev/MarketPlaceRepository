package org.stus.marketplace.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.stus.marketplace.models.Person;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PersonDetails implements UserDetails {
    private Person person;
    private static final Logger logger = LogManager.getLogger(PersonDetails.class.getName());

    public PersonDetails(Person person) {
        this.person = person;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        logger.debug("catch Person with id: " + person.getId());
        this.person = person;
    }
}
