package org.stus.marketplace.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersonDTO {
    private static final Logger logger = LogManager.getLogger(PersonDTO.class.getName());
    private int id;

    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 100, message = "Username size should be between 2 and 100")
    private String username;

    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, max = 200, message = "Password size should be between 8 and 100")
    private String password;

    @Min(value = 1900, message = "Birth year should be biggest then 1900")
    private int birthYear;

    @Email
    private String email;

    private String phone;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch personDTO id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in personDTO");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        logger.debug("catch personDTO username: " + username);
        this.username = username;
        logger.info("set username: " + username + " in personDTO");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        logger.debug("catch personDTO password: " + password);
        this.password = password;
        logger.info("set password: " + password + " in personDTO");
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        logger.debug("catch personDTO bithYear: " + birthYear);
        this.birthYear = birthYear;
        logger.info("set birthYear: " + birthYear + " in personDTO");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        logger.debug("catch personDTO email: " + email);
        this.email = email;
        logger.info("set email: " + email + " in personDTO");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        logger.debug("catch personDTO phone: " + phone);
        this.phone = phone;
        logger.info("set phone: " + phone + " in personDTO");
    }
}
