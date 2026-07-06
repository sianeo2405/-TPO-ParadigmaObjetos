package view;

import controller.GameController;
import model.Item;


import javax.swing.*;
import java.awt.*;
import java.util.List;

// Panel que muestra la tienda, con el oro del jugador y los objetos disponibles para comprar.

public final class ShopPanel extends BackgroundPanel {
    private GameController controller;

    private final JLabel goldLabel = new JLabel("Oro: 0", JLabel.CENTER);
    private final JPanel shopGrid = new JPanel(new GridLayout(0, 2, 8, 8));
    
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

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stockPanel, null);
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

        shopGrid.revalidate();
        shopGrid.repaint();
        
        
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

    

    private void feedback(String message) {
        feedbackLabel.setText(message != null ? message : " ");
        Timer timer = new Timer(3000, e -> feedbackLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
