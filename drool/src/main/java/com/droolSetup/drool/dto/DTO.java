package com.droolSetup.drool.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DTO {
    String model;
    String type;
    String company;

    Integer avg;
    String price;
    Integer seats;

}