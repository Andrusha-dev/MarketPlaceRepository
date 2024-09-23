package org.stus.marketplace.utils.person_utils;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.stus.marketplace.dto.PersonDTO;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.services.PersonService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class PersonDTOValidator implements Validator {
    private final PersonService personService;

    @Autowired
    public PersonDTOValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTO personDTO = (PersonDTO)target;

        String phone = personDTO.getPhone();
        String regex = "\\+38\\(0\\d{2}\\)\\d{3}-\\d{2}-\\d{2}";

        if (!Pattern.matches(regex, phone)) {
            errors.rejectValue("phone", "", "phone should meet the regular expression '+38(0XX)XXX-XX-XX'");
        }

        List<Person> persons = personService.findAllPersons();

        for (Person person : persons) {
            if (person.getUsername().equals(personDTO.getUsername())) {
                errors.rejectValue("username", "", "Such username are registered");
            }
            if (person.getEmail().equals(personDTO.getEmail())) {
                errors.rejectValue("email", "", "Such email are registered");
            }
            if (person.getPhone().equals(personDTO.getPhone())) {
                errors.rejectValue("phone", "", "Such phone are registered");
            }
        }
    }
}
