package com.example.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    private Double balance;

    private BalancesType balancesType;

}
