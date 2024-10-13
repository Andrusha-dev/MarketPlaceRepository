package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.PersonDTO;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.security.PersonDetails;

@RestController
@RequestMapping("/login")
public class AuthController {
             //for username&password authentication in rest api application
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;


    @Autowired
    public AuthController(AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }


    @PostMapping()
    public ResponseEntity<HttpStatus> login(@RequestBody LoginRequest loginRequest,
                                            HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationResponse);

        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
        securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);


        if (!authenticationResponse.isAuthenticated()) {
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/current")
    public PersonDTO getCurrentPerson() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Person person = personDetails.getPerson();

        return convertToPersonDTO(person);
    }


    public record LoginRequest(String username, String password) {
    }


    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
