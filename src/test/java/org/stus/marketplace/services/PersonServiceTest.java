package org.stus.marketplace.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    private PersonService personService;
    private PasswordEncoder passwordEncoder;
    private List<Person> persons;

    @Mock
    PersonRepository personRepository;

    @BeforeEach
    public void createPersoneService() {
        passwordEncoder = new BCryptPasswordEncoder();
        this.personService = new PersonService(personRepository, passwordEncoder);

        this.persons = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Person person = this.personFactory(i, "test_password");
            person.setId(i);
            person.setPassword(passwordEncoder.encode(person.getPassword()));
            person.setRole("ROLE_USER");

            persons.add(person);
        }
    }


    @Test
    public void personServiceShouldReturnAllPersons() {
        Mockito.doReturn(persons).when(personRepository).findAll();
        Assertions.assertEquals(persons, personService.findAllPersons());
        Mockito.verify(personRepository).findAll();
    }

    @Test
    public void personServiceShouldRetornPersonById() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);

            return Optional.ofNullable(this.findPersonById(id));
        }).when(personRepository).findById(Mockito.anyInt());

        Assertions.assertEquals(Optional.ofNullable(this.findPersonById(5)), personService.findPersonById(5));
        Mockito.verify(personRepository).findById(5);
    }

    @Test
    public void personServiceShouldReturnPersonByUsername() {
        Mockito.doAnswer(ans -> {
            String username = ans.getArgument(0);

            return Optional.ofNullable(this.findPersonByUsername(username));
        }).when(personRepository).findByUsername(Mockito.anyString());

        Assertions.assertEquals(Optional.ofNullable(this.findPersonByUsername("User5")), personService.findPersonByUserName("User5"));
        Mockito.verify(personRepository).findByUsername("User5");
    }

    @Test
    public void personServiceShouldSavePerson() {
        Mockito.doAnswer(ans -> {
            Person savedPerson = ans.getArgument(0);
            int id = 0;
            for (Person person : persons) {
                if (person.getId() > id) {
                    id = person.getId();
                }
            }

            id++;
            savedPerson.setId(id);
            persons.add(savedPerson);
            return savedPerson;
        }).when(personRepository).save(Mockito.any(Person.class));

        Person savedPerson = this.personFactory(6, "test_password");
        personService.savePerson(savedPerson);

        Person referencedPerson = this.personFactory(6, "test_password");
        referencedPerson.setId(6);
        referencedPerson.setPassword(passwordEncoder.encode(referencedPerson.getPassword()));
        referencedPerson.setRole("ROLE_USER");

        if (!passwordEncoder.matches("test_password", persons.getLast().getPassword()) &&
                !passwordEncoder.matches("test_password", referencedPerson.getPassword())) {
            Assertions.fail();
        }

        Assertions.assertEquals(referencedPerson, persons.getLast());
        Mockito.verify(personRepository).save(savedPerson);
    }

    @Test
    public void personServerShouldUpdatePerson() {
        Mockito.doAnswer(ans -> {
            Person updatedPerson = ans.getArgument(0);
            persons = persons.stream()
                    .map(person -> person.getId()==updatedPerson.getId() ? updatedPerson : person)
                    .collect(Collectors.toList());

            return updatedPerson;
        }).when(personRepository).save(Mockito.any(Person.class));

        Person updatedPerson = this.personFactory(10, "gaguga");
        updatedPerson.setId(5);
        personService.updatePerson(updatedPerson);

        Person referencedPerson = this.personFactory(10, "gaguga");
        referencedPerson.setId(5);
        referencedPerson.setPassword(passwordEncoder.encode(referencedPerson.getPassword()));
        referencedPerson.setRole("ROLE_USER");

        if (!passwordEncoder.matches("gaguga", persons.getLast().getPassword()) &&
            !passwordEncoder.matches("gaguga", referencedPerson.getPassword())) {
            Assertions.fail();
        }

        Assertions.assertEquals(referencedPerson, persons.getLast());
        Mockito.verify(personRepository).save(updatedPerson);
    }

    @Test
    public void personServiceShouldDeletePerson() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            persons = persons.stream()
                    .filter(person -> person.getId()!=id)
                    .collect(Collectors.toList());

            return true;
        }).when(personRepository).deleteById(Mockito.anyInt());

        personService.deletePerson(5);

        Assertions.assertFalse(this.findIfPresent(5));
        Mockito.verify(personRepository).deleteById(5);
    }


    private Person findPersonById(int id) {
        Person foundedPerson = null;
        for (Person person : persons) {
            if (person.getId() == id) {
                foundedPerson = person;
            }
        }

        return foundedPerson;
    }

    private Person findPersonByUsername(String username) {
        Person foundedPerson = null;
        for (Person person : persons) {
            if (person.getUsername().equals(username)) {
                foundedPerson = person;
            }
        }

        return foundedPerson;
    }

    private Person personFactory(int id, String password) {
        Person person = new Person();
        person.setUsername("User" + id);
        person.setPassword(password);

        return person;
    }

    private boolean findIfPresent(int id) {
        for (Person person : persons) {
            if (person.getId()==id)
                return true;
        }

        return false;
    }
}
