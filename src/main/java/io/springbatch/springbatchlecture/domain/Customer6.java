package io.springbatch.springbatchlecture.domain;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customer6")
public class Customer6 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String age;

    @OneToOne(mappedBy = "customer")
    private Address address;

}
