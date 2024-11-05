package org.stus.marketplace.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(PersonService.class.getName());

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(int id) {
        logger.debug("catch person id: " + id);

        return personRepository.findById(id);
    }

    public Optional<Person> findPersonByUserName(String username) {
        logger.debug("catch username: " + username);

        return personRepository.findByUsername(username);
    }

    @Transactional
    public void savePerson(Person person) {
        logger.debug("catch person to save with username: " + person.getUsername());

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        logger.info("encoding password");
        person.setRole("ROLE_USER");
        logger.info("set role");
        personRepository.save(person);
        logger.info("saving person");
    }

    @Transactional
    public void updatePerson(Person person) {
        logger.debug("catch person to update with id: " + person.getId());

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        logger.info("encoding password");
        person.setRole("ROLE_USER");
        logger.info("set role");
        personRepository.save(person);
        logger.info("updating person with id: " + person.getId());
    }

    @Transactional
    public void deletePerson(int id) {
        logger.debug("catch person for delete with id: " + id);

        personRepository.deleteById(id);
        logger.info("deleting person with id: " + id);
    }
}
