package com.example.deliveryservice.service;

import com.example.deliveryservice.model.Couriers;
import com.example.deliveryservice.model.DTOs.OrderDTO;
import com.example.deliveryservice.model.Orders;
import com.example.deliveryservice.model.OrdersToCuriers;
import com.example.deliveryservice.repository.CouriersRepository;
import com.example.deliveryservice.repository.OrdersRepository;
import com.example.deliveryservice.repository.OrdersToCouriersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DeliveryService {

    CouriersRepository couriersRepository;

    private Random random = new Random();
    OrdersRepository ordersRepository;

    OrdersToCouriersRepository ordersToCouriersRepository;

    @Autowired
    public DeliveryService(CouriersRepository couriersRepository, OrdersRepository ordersRepository, OrdersToCouriersRepository ordersToCouriersRepository) {
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.ordersToCouriersRepository = ordersToCouriersRepository;
    }

    public Couriers assignCourrier(OrderDTO orderDTO) throws InterruptedException {
        Orders orders = orderDTO.toEntity();
        //List<Couriers> allByCouriersStatus_free = couriersRepository.findAllByCouriersStatus_Free();
        List<Couriers> all = couriersRepository.findAll();

        Random random = new Random();
        Couriers courier = all.get(random.nextInt(all.size() ));
        Thread.sleep(300);
        ordersRepository.save(orders);

        OrdersToCuriers ordersToCuriers = new OrdersToCuriers();
        ordersToCuriers.setCourier(courier);
        ordersToCuriers.setOrders(orders);
        ordersToCouriersRepository.save(ordersToCuriers);

        return courier;
    }

    public void revertCourrier(OrderDTO orderDTO) {
        Orders orders = orderDTO.toEntity();
        //List<Couriers> allByCouriersStatus_free = couriersRepository.findAllByCouriersStatus_Free();

        Orders byId = ordersRepository.findById(orders.getId()).orElseThrow(EntityNotFoundException::new);

        OrdersToCuriers ordersToCuriers = ordersToCouriersRepository.findOrdersToCuriersByOrders(byId).orElseThrow(EntityNotFoundException::new);

        ordersToCouriersRepository.delete(ordersToCuriers);
        ordersRepository.delete(byId);
    }
}
