package com.example.diplomasagaorderservice.models.DTOs;

import com.example.diplomasagaorderservice.configs.Constants;
import com.example.diplomasagaorderservice.models.Orders;
import com.example.diplomasagaorderservice.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO  {
    private Long id;

    private String address;

    private Integer destinationToAddress;

    private Long userId;

    private Long companyId;

    private Double price;

    private OrderStatus orderStatus;

    private Long startTime;

    public Orders toEntity() {
        Orders order = new Orders();
        order.setAddress(address);
        order.setPrice(calculatePrice(destinationToAddress));
        order.setCompanyId(companyId);
        order.setUserId(userId);
        order.setDestinationToAddress(destinationToAddress);
        order.setOrderStatus(orderStatus);
        return order;
    }

    private Double calculatePrice(Integer destinationToAddress) {
        Double totalPrice = 300.0;
        if (destinationToAddress > Constants.fixedDestination) {
            totalPrice += (destinationToAddress - Constants.fixedDestination) * 10.0;
        }
        return totalPrice;
    }
}
