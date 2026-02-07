package io.springbatch.springbatchlecture.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Customer2 {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;

}
