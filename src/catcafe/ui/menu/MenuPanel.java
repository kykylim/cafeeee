package catcafe.ui.menu;

import catcafe.dao.MenuItemDao;
import catcafe.model.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MenuPanel extends JPanel {
    private final MenuItemDao dao = new MenuItemDao();
    private JTextField txtName, txtCategory, txtPrice;
    private JCheckBox chkAvailable;
    private JLabel lblStatus;
    private JPanel menuGrid;

    public MenuPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadMenuItems();
    }

    private void initComponents() {
        // Right-side form panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        txtName = new JTextField(20);
        txtCategory = new JTextField(20);
        txtPrice = new JTextField(20);
        chkAvailable = new JCheckBox("Available", true);
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        lblStatus = new JLabel("Ready");

        form.add(new JLabel("Name")); form.add(txtName);
        form.add(new JLabel("Category")); form.add(txtCategory);
        form.add(new JLabel("Price")); form.add(txtPrice);
        form.add(chkAvailable);
        form.add(btnCreate); form.add(btnUpdate); form.add(btnDelete);

        // Center menu grid
        menuGrid = new JPanel();
        menuGrid.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns
        JScrollPane scrollPane = new JScrollPane(menuGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);
        add(lblStatus, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> onCreate());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
    }

    private void loadMenuItems() {
        try {
            menuGrid.removeAll();
            List<MenuItem> items = dao.findAll();
            for (MenuItem item : items) {
                JPanel itemPanel = createItemPanel(item);
                menuGrid.add(itemPanel);
            }
            menuGrid.revalidate();
            menuGrid.repaint();
            lblStatus.setText("Loaded " + items.size() + " item(s).");
        } catch (SQLException ex) {
            showError("Load failed: " + ex.getMessage());
        }
    }

    private JPanel createItemPanel(MenuItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(200, 250));

        // Build image filename from item name (remove spaces/special chars)
        String imageFile = item.getName().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        String imagePath = "/catcafe/assets/images/" + imageFile;

        ImageIcon icon;
        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getResource(imagePath));
            Image scaled = rawIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } catch (Exception e) {
            // Fallback placeholder
            ImageIcon rawIcon = new ImageIcon(getClass().getResource("/catcafe/assets/images/chocoCake.jpg"));
            Image scaled = rawIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }

        JLabel imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name and price
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Price: $" + item.getPrice());
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantity spinner
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));
        qtySpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Purchase checkbox
        JCheckBox purchaseBox = new JCheckBox("Purchase");
        purchaseBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components
        panel.add(Box.createVerticalStrut(5));
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(nameLabel);
        panel.add(priceLabel);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtySpinner);
        panel.add(purchaseBox);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void onCreate() {
        try {
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            boolean available = chkAvailable.isSelected();
            MenuItem m = dao.insert(new MenuItem(null, name, category, price, available));
            loadMenuItems();
            lblStatus.setText("Created item ID " + m.getId());
        } catch (Exception ex) {
            showError("Create failed: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        showError("Update not supported in visual mode. Use table view if needed.");
    }

    private void onDelete() {
        showError("Delete not supported in visual mode. Use table view if needed.");
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
