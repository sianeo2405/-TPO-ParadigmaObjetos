package com.nodequest.ui;

import com.nodequest.game.GameController;
import com.nodequest.game.GameScreen;
import com.nodequest.game.SaveSystem;
import com.nodequest.model.Item;
import com.nodequest.model.PartyMember;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

public final class GameFrame extends JFrame implements GameController.Listener {
    private static final String CARD_MAP = "map";
    private static final String CARD_COMBAT = "combat";
    private static final String CARD_EVENT = "event";
    private static final String CARD_SHOP = "shop";

    private GameController controller;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerPanel = new JPanel(cardLayout);
    private final MapPanel mapPanel = new MapPanel();
    private final CombatPanel combatPanel = new CombatPanel();
    private final EventPanel eventPanel = new EventPanel();
    private final ShopPanel shopPanel = new ShopPanel();
    private final PartyPanel partyPanel = new PartyPanel();
    private final JLabel statusBar = new JLabel(" ");

    private JMenuItem loadItem;
    private JMenuItem useItem;

    public GameFrame() {
        super("Oathbreaker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1300, 920));
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Partida");
        JMenuItem saveItem = new JMenuItem("Guardar partida");
        loadItem = new JMenuItem("Cargar partida");
        JMenuItem exitItem = new JMenuItem("Salir");

        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);

        JMenu invMenu = new JMenu("Inventario");
        useItem = new JMenuItem("Usar objeto");
        invMenu.add(useItem);
        menuBar.add(invMenu);

        setJMenuBar(menuBar);

        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e -> System.exit(0));
        useItem.addActionListener(e -> useInventoryItem());

        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JScrollPane mapScroll = new JScrollPane(mapPanel);
        mapScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mapScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mapScroll.getVerticalScrollBar().setUnitIncrement(16);
        mapScroll.setBorder(null);
        centerPanel.add(mapScroll, CARD_MAP);
        centerPanel.add(combatPanel, CARD_COMBAT);
        centerPanel.add(eventPanel, CARD_EVENT);
        centerPanel.add(shopPanel, CARD_SHOP);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(partyPanel, BorderLayout.EAST);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        eventPanel.setRestartAction(this::restartGame);
        startNewGame();
    }

    private void startNewGame() {
        controller = new GameController();
        controller.addListener(this);

        mapPanel.bind(controller);
        combatPanel.bind(controller);
        eventPanel.bind(controller);
        shopPanel.bind(controller);

        refreshAll();
    }

    private void restartGame() {
        startNewGame();
    }

    @Override
    public void onStateChanged() {
        refreshAll();
    }

    private void saveGame() {
        if (controller == null) return;
        boolean success = SaveSystem.save(controller);
        if (success) {
            JOptionPane.showMessageDialog(this, "Partida guardada con éxito.", "Guardar Partida", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la partida.", "Guardar Partida", JOptionPane.ERROR_MESSAGE);
        }
        refreshAll();
    }

    private void loadGame() {
        GameController loaded = SaveSystem.load();
        if (loaded != null) {
            this.controller = loaded;
            controller.addListener(this);

            mapPanel.bind(controller);
            combatPanel.bind(controller);
            eventPanel.bind(controller);
            shopPanel.bind(controller);

            JOptionPane.showMessageDialog(this, "Partida cargada con éxito.", "Cargar Partida", JOptionPane.INFORMATION_MESSAGE);
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la partida o no existe.", "Cargar Partida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void useInventoryItem() {
        if (controller == null) return;
        List<Item> inventory = controller.getParty().getInventory();
        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tu inventario está vacío.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Item selectedItem = (Item) JOptionPane.showInputDialog(
                this,
                "Selecciona un objeto para usar:",
                "Usar Objeto",
                JOptionPane.QUESTION_MESSAGE,
                null,
                inventory.toArray(),
                inventory.get(0)
        );

        if (selectedItem == null) return;

        List<PartyMember> members = controller.getParty().getMembers();
        PartyMember selectedMember = (PartyMember) JOptionPane.showInputDialog(
                this,
                "Selecciona un miembro del grupo para usar " + selectedItem.getName() + " en él:",
                "Seleccionar Objetivo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                members.toArray(),
                members.get(0)
        );

        if (selectedMember == null) return;

        String msg = controller.useItem(selectedItem, selectedMember);
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        refreshAll();
    }

    private void refreshAll() {
        partyPanel.refresh(controller);
        statusBar.setText(controller.getStatusMessage());

        if (loadItem != null) {
            loadItem.setEnabled(SaveSystem.hasSave());
        }
        if (useItem != null) {
            useItem.setEnabled(controller != null && !controller.getParty().getInventory().isEmpty());
        }

        GameScreen screen = controller.getScreen();
        switch (screen) {
            case MAP -> {
                mapPanel.repaint();
                cardLayout.show(centerPanel, CARD_MAP);
            }
            case COMBAT -> {
                combatPanel.refresh();
                cardLayout.show(centerPanel, CARD_COMBAT);
            }
            case REST, TREASURE, VICTORY, DEFEAT -> {
                eventPanel.refresh();
                cardLayout.show(centerPanel, CARD_EVENT);
            }
            case SHOP -> {
                shopPanel.refresh();
                cardLayout.show(centerPanel, CARD_SHOP);
            }
        }
    }
}
