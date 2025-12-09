package catcafe.ui.customers;

import catcafe.dao.CustomerDao;
import catcafe.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomPanel extends JPanel {   // ✅ Class name matches constructor
    private final CustomerDao dao = new CustomerDao();
    private DefaultTableModel model;
    private JTable tbl;
    private JTextField txtName, txtPhone, txtEmail, txtSearch;
    private JLabel lblStatus;

    public CustomPanel() {   // ✅ Constructor name matches class
        setLayout(new BorderLayout());
        initComponents();
        loadTable();
    }

    private void initComponents() {
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(btnSearch);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        lblStatus = new JLabel("Ready");
        right.add(new JLabel("Name")); right.add(txtName);
        right.add(new JLabel("Phone")); right.add(txtPhone);
        right.add(new JLabel("Email")); right.add(txtEmail);
        right.add(btnCreate); right.add(btnUpdate); right.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
        add(lblStatus, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> onSearch());
        btnCreate.addActionListener(e -> onCreate());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        tbl.getSelectionModel().addListSelectionListener(e -> onSelect());
    }

    private void loadTable() {
        try {
            List<Customer> list = dao.findAll();
            model.setRowCount(0);
            for (Customer c : list)
                model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail()});
            lblStatus.setText("Loaded " + list.size() + " customer(s).");
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void onCreate() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            Customer c = dao.insert(new Customer(null, name, txtPhone.getText().trim(), txtEmail.getText().trim()));
            loadTable();
            lblStatus.setText("Created customer ID " + c.getId());
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onUpdate() {
        int row = tbl.getSelectedRow();
        if (row < 0) { showError("Select a row to update."); return; }
        try {
            Integer id = (Integer) model.getValueAt(row, 0);
            String name = txtName.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            boolean ok = dao.update(new Customer(id, name, txtPhone.getText().trim(), txtEmail.getText().trim()));
            if (ok) { loadTable(); lblStatus.setText("Updated ID " + id); } else showError("Update failed.");
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onDelete() {
        int row = tbl.getSelectedRow();
        if (row < 0) { showError("Select a row to delete."); return; }
        Integer id = (Integer) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete customer ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
            != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(id)) { loadTable(); lblStatus.setText("Deleted ID " + id); } else showError("Delete failed.");
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onSearch() {
        try {
            String term = txtSearch.getText().trim();
            model.setRowCount(0);
            for (Customer c : dao.searchByName(term))
                model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail()});
            lblStatus.setText("Search: " + term);
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onSelect() {
        int row = tbl.getSelectedRow();
        if (row >= 0) {
            txtName.setText(String.valueOf(model.getValueAt(row, 1)));
            txtPhone.setText(String.valueOf(model.getValueAt(row, 2)));
            txtEmail.setText(String.valueOf(model.getValueAt(row, 3)));
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
