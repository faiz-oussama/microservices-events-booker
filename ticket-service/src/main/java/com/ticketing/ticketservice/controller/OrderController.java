package com.ticketing.ticketservice.controller;

import com.ticketing.ticketservice.model.Order;
import com.ticketing.ticketservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id).orElse(null);
    }
    
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderService.updateOrder(id, orderDetails);
    }
    
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
    
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}