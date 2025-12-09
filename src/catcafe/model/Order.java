/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catcafe.model;

import java.time.LocalDateTime;

public class Order {
    private Integer id;
    private Integer customerId;
    private LocalDateTime orderDate;
    private String status; // Pending, Completed, Cancelled
    private double total;

    public Order() {}

    public Order(Integer id, Integer customerId, LocalDateTime orderDate, String status, double total) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.total = total;
    }

    // ✅ Getters
    public Integer getId() {
        return id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }

    // ✅ Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
