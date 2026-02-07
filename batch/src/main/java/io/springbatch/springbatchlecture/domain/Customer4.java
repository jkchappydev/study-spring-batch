package io.springbatch.springbatchlecture.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customer")
public class Customer4 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment랑 맞춤
    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;

}
