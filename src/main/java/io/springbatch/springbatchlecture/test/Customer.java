package io.springbatch.springbatchlecture.test;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {

    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;

}
