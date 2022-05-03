package com.example.diplomasagaorderservice.models;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private Integer destinationToAddress;

    private Long userId;

    private Double price;

    private Long companyId;

    private OrderStatus orderStatus;

    public OrderDTO toOrderDTO() {
        OrderDTO order = new OrderDTO();
        order.setId(id);
        order.setAddress(address);
        order.setPrice(price);
        order.setCompanyId(companyId);
        order.setUserId(userId);
        order.setDestinationToAddress(destinationToAddress);
        order.setOrderStatus(orderStatus);
        return order;
    }
}
