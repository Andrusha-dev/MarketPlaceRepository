package org.stus.marketplace.utils.person_utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.services.PersonService;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;

    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person updatedPerson = (Person)target;

        String phone = updatedPerson.getPhone();
        String regex = "\\+38\\(0\\d{2}\\)\\d{3}-\\d{2}-\\d{2}";

        if (!Pattern.matches(regex, phone)) {
            errors.rejectValue("phone", "", "phone should meet the regular expression '+38(0XX)XXX-XX-XX'");
        }

        List<Person> persons = personService.findAllPersons();

        for (Person person : persons) {
            if ((person.getUsername().equals(updatedPerson.getUsername())) && (person.getId()!=updatedPerson.getId())) {
                errors.rejectValue("username", "", "Such username are registered");
            }
            if ((person.getEmail().equals(updatedPerson.getEmail())) && (person.getId()!=updatedPerson.getId())) {
                errors.rejectValue("email", "", "Such email are registered");
            }
            if ((person.getPhone().equals(updatedPerson.getPhone())) && (person.getId()!=updatedPerson.getId())) {
                errors.rejectValue("phone", "", "Such phone are registered");
            }
        }
    }
}
