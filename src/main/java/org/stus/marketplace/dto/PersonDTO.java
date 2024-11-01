package org.stus.marketplace.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonDTO {
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
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
