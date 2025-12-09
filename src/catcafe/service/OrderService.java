/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catcafe.service;


import catcafe.dao.MenuItemDao; // use to get current price
import catcafe.dao.OrderDao;
import catcafe.dao.OrderItemDao;
import catcafe.model.MenuItem;
import catcafe.model.Order;
import catcafe.model.OrderItem;
import catcafe.util.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderDao orderDao = new OrderDao();
    private final OrderItemDao itemDao = new OrderItemDao();
    private final MenuItemDao menuDao = new MenuItemDao();

    public Order createOrder(int customerId) throws SQLException {
        return orderDao.insertPending(customerId);
    }

    public void addItem(int orderId, int menuItemId, int qty) throws SQLException {
        MenuItem mi = menuDao.findById(menuItemId); // add a findById in MenuItemDao
        double priceSnapshot = mi.getPrice();
        itemDao.insert(new OrderItem(null, orderId, menuItemId, qty, priceSnapshot));
        recomputeTotal(orderId);
    }

    public void removeItem(int orderItemId, int orderId) throws SQLException {
        itemDao.deleteById(orderItemId);
        recomputeTotal(orderId);
    }

    public void recomputeTotal(int orderId) throws SQLException {
        List<OrderItem> items = itemDao.findByOrder(orderId);
        double total = 0.0;
        for (OrderItem it : items) total += it.getPrice() * it.getQuantity();
        orderDao.updateTotal(orderId, total);
    }

    public void completeOrder(int orderId) throws SQLException {
        orderDao.updateStatus(orderId, "Completed");
    }

    public void cancelOrder(int orderId) throws SQLException {
        orderDao.updateStatus(orderId, "Cancelled");
    }
}

