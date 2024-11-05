package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(PersonController.class.getName());

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
        List<PersonDTO> personsDTO = personService.findAllPersons().stream()
                        .map(p -> convertToPersonDTO(p))
                        .collect(Collectors.toList());
        logger.info("find all person");

        return personsDTO;
    }

    @GetMapping("/{id}")
    public PersonDTO getPersonById(@PathVariable("id") int id) {
        logger.debug("catch person id: " + id);

        Optional<Person> findedPerson = personService.findPersonById(id);
        if (findedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }
        logger.info("find person with id: " + id);

        return convertToPersonDTO(findedPerson.get());
    }

    @GetMapping("/username/{username}")
    public PersonDTO getPersonByUsername(@PathVariable("username") String username) {
        logger.debug("catch username: " + username);
        Optional<Person> findedPerson = personService.findPersonByUserName(username);
        if (findedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person no found");
        }
        logger.info("find person with username: " + username);

        return convertToPersonDTO(findedPerson.get());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createPerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        logger.debug("catch personDTO with username: " + personDTO.getUsername());
        personDTOValidator.validate(personDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " - " + error.getDefaultMessage() + ";");
            }

            throw new PersonNotCreateException(builder.toString());
        }
        logger.info("validation of personDTO completed successfully");

        personService.savePerson(convertToPerson(personDTO));
        logger.info("saving person with username: " + personDTO.getUsername());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updatePerson(@RequestBody @Valid PersonDTO personDTO,
                                                   BindingResult bindingResult, @PathVariable("id") int id) {
        logger.debug("catch personDTO with username: " + personDTO.getUsername());

        Optional<Person> foundedPerson = personService.findPersonById(id);
        if (foundedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person no found");
        }
        logger.info("find person with id: " + id);

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
        logger.info("validation of person with id: " + id + " completed successfully");

        personService.updatePerson(updatedPerson);
        logger.info("updating person with id: " + id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePerson(@PathVariable("id") int id) {
        logger.debug("catch person id: " + id);

        Optional<Person> deletedPerson = personService.findPersonById(id);
        if (deletedPerson.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }
        logger.info("find person with id: " + id);

        personService.deletePerson(id);
        logger.info("deleting person with id: " + id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Person convertToPerson(PersonDTO personDTO) {
        logger.debug("catch personDTO with username: " + personDTO.getUsername());
        Person person =  modelMapper.map(personDTO, Person.class);
        logger.info("converting personDTO to person with username: " + person.getUsername());
        return person;
    }

    private PersonDTO convertToPersonDTO(Person person) {
        logger.debug("catch person with username: " + person.getUsername());
        PersonDTO personDTO =  modelMapper.map(person, PersonDTO.class);
        logger.info("converting person to personDTO with username: " + personDTO.getUsername());
        return personDTO;
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreateException exc) {
        logger.error("person not create", exc);
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException exc) {
        logger.error("person not found", exc);
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotUpdateException exc) {
        logger.error("person not update", exc);
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
