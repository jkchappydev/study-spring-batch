package io.springbatch.springbatchlecture.practice.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    private Long id;
    private String name;
    private int price;
    private String type;

}
