package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cascade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Table(name = "person")
public class Person {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<ItemOrder> getItemOders() {
        return itemOders;
    }

    public void setItemOders(List<ItemOrder> itemOders) {
        this.itemOders = itemOders;
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
                '}';
    }
}
