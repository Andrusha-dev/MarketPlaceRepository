package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.dto.PersonDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.services.PersonService;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.person_utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final ItemService itemService;
    private final ModelMapper modelMapper;
    private final PersonDTOValidator personDTOValidator;
    private final PersonValidator personValidator;

    @Autowired
    public PersonController(PersonService personService, ItemService itemService, ModelMapper modelMapper, PersonDTOValidator personDTOValidator, PersonValidator personValidator) {
        this.personService = personService;
        this.itemService = itemService;
        this.modelMapper = modelMapper;
        this.personDTOValidator = personDTOValidator;
        this.personValidator = personValidator;
    }


    @GetMapping()
    public List<PersonDTO> getAllPersons() {
        return personService.findAllPersons().stream()
                        .map(p -> convertToPersonDTO(p))
                        .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPersonById(@PathVariable("id") int id) {
        Optional<Person> findedPerson = personService.findPersonById(id);

        if (findedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        return convertToPersonDTO(findedPerson.get());
    }

    @GetMapping("/username/{username}")
    public PersonDTO getPersonByUsername(@PathVariable("username") String username) {
        Optional<Person> findedPerson = personService.findPersonByUserName(username);
        if (findedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person no found");
        }

        return convertToPersonDTO(findedPerson.get());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createPerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        personDTOValidator.validate(personDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " - " + error.getDefaultMessage() + ";");
            }

            throw new PersonNotCreateException(builder.toString());
        }

        personService.savePerson(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updatePerson(@RequestBody @Valid PersonDTO personDTO,
                                                   BindingResult bindingResult, @PathVariable("id") int id) {

        Optional<Person> foundedPerson = personService.findPersonById(id);
        if (foundedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person no found");
        }

        Person updatedPerson = convertToPerson(personDTO);
        updatedPerson.setId(id);

        personValidator.validate(updatedPerson, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new PersonNotUpdateException(builder.toString());
        }

        personService.updatePerson(updatedPerson);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePerson(@PathVariable("id") int id) {
        Optional<Person> deletedPerson = personService.findPersonById(id);

        if (deletedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        personService.deletePerson(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreateException exc) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException exc) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotUpdateException exc) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
