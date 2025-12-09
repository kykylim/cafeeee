package catcafe.ui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Meowster Cafe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customers", new catcafe.ui.customers.CustomPanel());
        tabs.addTab("Menu", new catcafe.ui.menu.MenuPanel());
        tabs.addTab("Orders", new catcafe.ui.orders.OrderPanel());

        add(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
