package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cascade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "person")
public class Person {
    private static final Logger logger = LogManager.getLogger(Person.class.getName());

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 100, message = "Username size should be between 2 and 100")
    private String username;

    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, max = 200, message = "Password size should be between 8 and 100")
    @Column(name = "password")
    private String password;

    @Column(name = "birth_year")
    @Min(value = 1900, message = "Birth year should be biggest then 1900")
    private int birthYear;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "owner")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<ItemOrder> itemOders;


    public Person() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch person id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in person");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        logger.debug("catch person username: " + username);
        this.username = username;
        logger.info("set username: " + username + " in person");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        logger.debug("catch person password: " + password);
        this.password = password;
        logger.info("set password: " + password + " in person");
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        logger.debug("catch person birthYear: " + birthYear);
        this.birthYear = birthYear;
        logger.info("set birthYear: " + birthYear + " in person");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        logger.debug("catch person email: " + email);
        this.email = email;
        logger.info("set email: " + email + " in person");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        logger.debug("catch person phone: " + phone);
        this.phone = phone;
        logger.info("set phone: " + phone + " in person");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        logger.debug("catch person role: " + role);
        this.role = role;
        logger.info("set role: " + role + " in person");
    }

    public List<ItemOrder> getItemOders() {
        return itemOders;
    }

    public void setItemOders(List<ItemOrder> itemOders) {
        logger.debug("catch itemOrders with size: " + itemOders.size());
        this.itemOders = itemOders;
        logger.info("set itemOrders with size: " + itemOders.size() + " in person");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && birthYear == person.birthYear && Objects.equals(username, person.username) && Objects.equals(email, person.email) && Objects.equals(phone, person.phone) && Objects.equals(role, person.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, birthYear, email, phone, role);
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthYear=" + birthYear +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", itemOders=" + itemOders +
                '}';
    }
}
