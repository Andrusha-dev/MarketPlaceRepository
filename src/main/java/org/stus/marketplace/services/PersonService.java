package org.stus.marketplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findPersonByUserName(String username) {
        return personRepository.findByUsername(username);
    }

    @Transactional
    public void savePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    @Transactional
    public void updatePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    @Transactional
    public void deletePerson(int id) {
        personRepository.deleteById(id);
    }
}
