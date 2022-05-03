package com.example.deliveryservice.model.DTOs;

import com.example.deliveryservice.model.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;

    private String address;

    private Integer destinationToAddress;

    private Long userId;

    private Long companyId;

    private Double price;

    public Orders toEntity() {
        Orders orders = new Orders();
        orders.setAddress(address);
        orders.setPrice(price);
        orders.setCompanyId(companyId);
        orders.setUserId(userId);
        orders.setDestinationToAddress(destinationToAddress);
        return orders;
    }

}
