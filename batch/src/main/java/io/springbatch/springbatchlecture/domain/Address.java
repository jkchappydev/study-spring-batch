package io.springbatch.springbatchlecture.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer6 customer;

}
