package org.stus.marketplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.PersonRepository;
import org.stus.marketplace.security.PersonDetails;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> findedPerson = personRepository.findByUsername(username);
        if (findedPerson.isEmpty()) {
            throw new UsernameNotFoundException("Person not found");
        }

        return new PersonDetails(findedPerson.get());
    }
}
