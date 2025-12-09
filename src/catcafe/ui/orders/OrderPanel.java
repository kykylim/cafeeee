/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catcafe.ui.orders;



import catcafe.dao.CustomerDao;
import catcafe.dao.MenuItemDao;
import catcafe.dao.OrderItemDao;
import catcafe.model.Customer;
import catcafe.model.MenuItem;
import catcafe.model.OrderItem;
import catcafe.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class OrderPanel extends JPanel {
    private final OrderService service = new OrderService();
    private final CustomerDao customerDao = new CustomerDao();
    private final MenuItemDao menuDao = new MenuItemDao();

    private JComboBox<CustomerOption> cmbCustomer;
    private JComboBox<MenuItemOption> cmbMenuItem;
    private JSpinner spnQty;
    private JLabel lblOrderId, lblTotal, lblStatus;
    private DefaultTableModel modelItems;
    private JTable tblItems;
    private Integer currentOrderId;

    public OrderPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Top: customer + actions
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbCustomer = new JComboBox<>();
        JButton btnNewOrder = new JButton("New Order");
        JButton btnComplete = new JButton("Complete");
        JButton btnCancel = new JButton("Cancel");
        lblOrderId = new JLabel("Order: none");
        lblTotal = new JLabel("Total: 0.00");
        lblStatus = new JLabel("Ready");

        top.add(new JLabel("Customer:"));
        top.add(cmbCustomer);
        top.add(btnNewOrder);
        top.add(btnComplete);
        top.add(btnCancel);
        top.add(lblOrderId);
        top.add(lblTotal);

        // Center: items table
        modelItems = new DefaultTableModel(new Object[]{"ItemID","Item","Qty","Price","LineTotal","OrderItemID"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblItems = new JTable(modelItems);
        tblItems.removeColumn(tblItems.getColumnModel().getColumn(5)); // hide OrderItemID

        // Right: add item
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        cmbMenuItem = new JComboBox<>();
        spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton btnAdd = new JButton("Add Item");
        JButton btnRemove = new JButton("Remove Selected");
        right.add(new JLabel("Menu Item")); right.add(cmbMenuItem);
        right.add(new JLabel("Quantity")); right.add(spnQty);
        right.add(btnAdd); right.add(btnRemove);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblItems), BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
        add(lblStatus, BorderLayout.SOUTH);

        // Load dropdown data
        reloadCustomers();
        reloadMenuItems();

        // Events
        btnNewOrder.addActionListener(e -> onNewOrder());
        btnComplete.addActionListener(e -> onComplete());
        btnCancel.addActionListener(e -> onCancel());
        btnAdd.addActionListener(e -> onAddItem());
        btnRemove.addActionListener(e -> onRemoveItem());
    }

    private void reloadCustomers() {
        try {
            cmbCustomer.removeAllItems();
            for (Customer c : customerDao.findAll()) {
                cmbCustomer.addItem(new CustomerOption(c.getId(), c.getName()));
            }
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void reloadMenuItems() {
        try {
            cmbMenuItem.removeAllItems();
            for (MenuItem m : menuDao.findAll()) {
                cmbMenuItem.addItem(new MenuItemOption(m.getId(), m.getName(), m.getPrice()));
            }
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onNewOrder() {
        CustomerOption sel = (CustomerOption) cmbCustomer.getSelectedItem();
        if (sel == null) { showError("Select a customer."); return; }
        try {
            currentOrderId = service.createOrder(sel.id).getId();
            modelItems.setRowCount(0);
            lblOrderId.setText("Order: " + currentOrderId);
            lblTotal.setText("Total: 0.00");
            lblStatus.setText("New order for " + sel.name);
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onAddItem() {
        if (currentOrderId == null) { showError("Create an order first."); return; }
        MenuItemOption mi = (MenuItemOption) cmbMenuItem.getSelectedItem();
        if (mi == null) { showError("Select a menu item."); return; }
        int qty = (Integer) spnQty.getValue();
        try {
            service.addItem(currentOrderId, mi.id, qty);
            refreshItemsTable();
            lblStatus.setText("Added " + qty + " x " + mi.name);
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onRemoveItem() {
        int row = tblItems.getSelectedRow();
        if (row < 0) { showError("Select an item row."); return; }
        Integer orderItemId = (Integer) modelItems.getValueAt(row, 5);
        try {
            service.removeItem(orderItemId, currentOrderId);
            refreshItemsTable();
            lblStatus.setText("Item removed.");
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onComplete() {
        if (currentOrderId == null) { showError("No active order."); return; }
        try {
            service.completeOrder(currentOrderId);
            lblStatus.setText("Order completed.");
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onCancel() {
        if (currentOrderId == null) { showError("No active order."); return; }
        try {
            service.cancelOrder(currentOrderId);
            lblStatus.setText("Order cancelled.");
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

   private void refreshItemsTable() {
    try {
        OrderItemDao itemDao = new OrderItemDao();
        List<OrderItem> items = itemDao.findByOrder(currentOrderId);
        modelItems.setRowCount(0);
        double total = 0.0;
        for (OrderItem it : items) {
            double line = it.getPrice() * it.getQuantity();
            total += line;
            MenuItem m = menuDao.findById(it.getMenuItemId());
            modelItems.addRow(new Object[]{
                it.getMenuItemId(),
                m != null ? m.getName() : "Unknown",
                it.getQuantity(),
                it.getPrice(),
                line,
                it.getId()
            });
        }
        lblTotal.setText(String.format("Total: %.2f", total));
    } catch (SQLException ex) {
        showError(ex.getMessage());
    }
}

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Helper option classes for combo boxes
    private static class CustomerOption {
        int id; String name;
        CustomerOption(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }
    private static class MenuItemOption {
        int id; String name; double price;
        MenuItemOption(int id, String name, double price) { this.id = id; this.name = name; this.price = price; }
        @Override public String toString() { return name + " (" + price + ")"; }
    }
}

