package com.nodequest.ui;

import com.nodequest.game.GameController;
import com.nodequest.model.Item;
import com.nodequest.model.PartyMember;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class ShopPanel extends BackgroundPanel {
    private GameController controller;

    private final JLabel goldLabel = new JLabel("Oro: 0", JLabel.CENTER);
    private final JPanel shopGrid = new JPanel(new GridLayout(0, 2, 8, 8));
    private final JPanel inventoryGrid = new JPanel(new GridLayout(0, 1, 4, 4));
    private final JLabel feedbackLabel = new JLabel(" ", JLabel.CENTER);
    private final JButton leaveButton = new JButton("Salir");

    public ShopPanel() {
        super("shop");
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Tienda", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        goldLabel.setFont(goldLabel.getFont().deriveFont(Font.BOLD, 14f));
        goldLabel.setForeground(new Color(180, 130, 0));
        feedbackLabel.setForeground(new Color(0, 120, 0));

        JPanel header = new JPanel(new GridLayout(3, 1, 0, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(goldLabel);
        header.add(feedbackLabel);

        JPanel stockPanel = new JPanel(new BorderLayout(0, 6));
        stockPanel.setOpaque(false);
        stockPanel.setBorder(BorderFactory.createTitledBorder("For Sale"));
        shopGrid.setOpaque(false);
        stockPanel.add(shopGrid, BorderLayout.CENTER);

        JPanel invPanel = new JPanel(new BorderLayout(0, 6));
        invPanel.setOpaque(false);
        invPanel.setBorder(BorderFactory.createTitledBorder("Tu inventario"));
        inventoryGrid.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(inventoryGrid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        invPanel.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stockPanel, invPanel);
        split.setOpaque(false);
        split.setResizeWeight(0.6);
        split.setDividerSize(6);

        leaveButton.addActionListener(e -> {
            if (controller != null) { controller.continueFromEvent(); }
        });

        add(header, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(leaveButton, BorderLayout.SOUTH);
    }

    public void bind(GameController controller) {
        this.controller = controller;
    }

    public void refresh() {
        if (controller == null) return;

        goldLabel.setText("Oro: " + controller.getParty().getGold());

        shopGrid.removeAll();
        List<Item> stock = controller.getShopStock();
        if (stock.isEmpty()) {
            shopGrid.add(new JLabel("No hay nada a la venta por ahora.", JLabel.CENTER));
        } else {
            for (Item item : stock) {
                shopGrid.add(buildShopCard(item));
            }
        }



        inventoryGrid.removeAll();
        List<Item> inventory = controller.getParty().getInventory();
        if (inventory.isEmpty()) {
            inventoryGrid.add(new JLabel("Tu inventario está vacío.", JLabel.CENTER));
        } else {
            for (Item item : inventory) {
                inventoryGrid.add(buildInventoryCard(item));
            }
        }

        shopGrid.revalidate();
        shopGrid.repaint();
        inventoryGrid.revalidate();
        inventoryGrid.repaint();
    }
    private JPanel buildShopCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 80), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        card.setBackground(new Color(255, 250, 230));

        JLabel nameLabel = new JLabel("<html><b>" + item.getName() + "</b> - " + item.getCost() + "oro</html>");
        JLabel descLabel = new JLabel("<html><i>" + item.getDescription() + "</i></html>");
        descLabel.setFont(descLabel.getFont().deriveFont(11f));
        JButton buyBtn = new JButton("Comprar");
        buyBtn.setFont(buyBtn.getFont().deriveFont(11f));
        buyBtn.addActionListener(e -> {
            String msg = controller.buyItem(item);
            feedback(msg);
            refresh();
        });

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(buyBtn, BorderLayout.EAST);

        return card;
    }

    private JPanel buildInventoryCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 160, 100), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        card.setBackground(new Color(235, 255, 235));

        JLabel nameLabel = new JLabel("<html><b>" + item.getName() + "</b> - " + item.getDescription() + "</html>");
        nameLabel.setFont(nameLabel.getFont().deriveFont(11f));

        JComboBox<PartyMember> targetBox = new JComboBox<>();
        for (PartyMember member : controller.getParty().getAliveMembers()) {
            targetBox.addItem(member);
        }

        JButton useBtn = new JButton("Usar");
        useBtn.setFont(useBtn.getFont().deriveFont(11f));
        useBtn.addActionListener(e -> {
            PartyMember target = (PartyMember) targetBox.getSelectedItem();
            if (target != null) {
                String msg = controller.useItem(item, target);
                feedback(msg);
                refresh();
            }
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        controls.setOpaque(false);
        controls.add(targetBox);
        controls.add(useBtn);
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(controls, BorderLayout.EAST);

        return card;
    }

    private void feedback(String message) {
        feedbackLabel.setText(message != null ? message : " ");
        Timer timer = new Timer(3000, e -> feedbackLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
