package com.nodequest.ui;

import com.nodequest.game.CombatEngine;
import com.nodequest.game.GameController;
import com.nodequest.game.GameScreen;
import com.nodequest.model.CharacterClass;
import com.nodequest.model.Enemy;
import com.nodequest.model.PartyMember;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public final class CombatPanel extends BackgroundPanel {
    private GameController controller;
    private PartyMember selectedHero;

    private final JLabel statusLabel = new JLabel("Selecciona una acción.");
    private final JList<String> logList = new JList<>();
    private final DefaultListModel<String> logModel = new DefaultListModel<>();
    private final JPanel battlefieldPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    private final JPanel leftPanel = new JPanel(new GridLayout(1, 0, 10, 0));
    private final JPanel rightPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    private final JPanel heroButtons = new JPanel(new GridLayout(1, 4, 8, 0));
    private final JComboBox<Enemy> enemyTargetBox = new JComboBox<>();
    private final JComboBox<PartyMember> allyTargetBox = new JComboBox<>();
    private final JButton attackButton = new JButton("Ataque");
    private final JButton skillButton = new JButton("Habilidad");

    public CombatPanel() {
        super("combat");
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        battlefieldPanel.setOpaque(false);
        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        heroButtons.setOpaque(false);

        battlefieldPanel.add(leftPanel);
        battlefieldPanel.add(rightPanel);

        logList.setModel(logModel);
        logList.setEnabled(false);
        

        heroButtons.add(makeHeroButton("Guerrera"));
        heroButtons.add(makeHeroButton("Curandera"));
        heroButtons.add(makeHeroButton("Arquero"));
        heroButtons.add(makeHeroButton("Mago"));

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.add(new JLabel("Objetivo enemigo:"));
        actions.add(enemyTargetBox);
        actions.add(new JLabel("Objetivo aliado (Curar):"));
        actions.add(allyTargetBox);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(heroButtons, BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        attackButton.addActionListener(e -> performAttack());
        skillButton.addActionListener(e -> performSkill());
        buttons.add(attackButton);
        buttons.add(skillButton);
        bottom.add(buttons, BorderLayout.SOUTH);

        add(battlefieldPanel, BorderLayout.CENTER);
        
        JScrollPane logScroll = new JScrollPane(logList);
        logScroll.setOpaque(false);
        logScroll.getViewport().setOpaque(false);
        
        javax.swing.JSplitPane southSplit = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, bottom, logScroll);
        southSplit.setResizeWeight(0.75);
        southSplit.setOpaque(false);
        southSplit.setBorder(null);

        add(southSplit, BorderLayout.SOUTH);
    }

    public void bind(GameController controller) {
        this.controller = controller;
    }

    public void refresh() {
        if (controller == null) {
            return;
        }
        CombatEngine combat = controller.getCombatEngine();
        statusLabel.setText(controller.getStatusMessage());

        selectedHero = combat.getActiveHero();
        if (selectedHero != null) {
            CharacterClass cls = selectedHero.getCharacterClass();
            skillButton.setText(cls.getSkillName() + " (" + selectedHero.getSkillMpCost() + " MP)");
        } else {
            skillButton.setText("Habilidad");
        }

        leftPanel.removeAll();
        int numEnemies = combat.getEnemies().size();
        if (numEnemies <= 1) {
            leftPanel.setLayout(new GridLayout(1, 1, 10, 10));
        } else if (numEnemies == 2) {
            leftPanel.setLayout(new GridLayout(2, 1, 10, 10));
        } else {
            leftPanel.setLayout(new GridLayout(2, 2, 10, 10));
        }
        for (Enemy enemy : combat.getEnemies()) {
            leftPanel.add(buildEnemyCard(enemy));
        }

        rightPanel.removeAll();
        for (PartyMember member : controller.getParty().getMembers()) {
            rightPanel.add(buildHeroCard(member));
        }

        enemyTargetBox.removeAllItems();
        for (Enemy enemy : combat.getAliveEnemies()) {
            enemyTargetBox.addItem(enemy);
        }

        allyTargetBox.removeAllItems();
        for (PartyMember member : controller.getParty().getAliveMembers()) {
            allyTargetBox.addItem(member);
        }

        logModel.clear();
        for (String line : combat.getLog()) {
            logModel.addElement(line);
        }
        if (!logModel.isEmpty()) {
            logList.ensureIndexIsVisible(logModel.size() - 1);
        }

        boolean canAct = combat.isPlayerTurn() && !combat.isCombatOver() && !combat.isPartyWiped();
        attackButton.setEnabled(canAct && selectedHero != null && selectedHero.isAlive());
        skillButton.setEnabled(canAct && selectedHero != null && selectedHero.isAlive());
        updateHeroButtons();
        
        leftPanel.revalidate();
        leftPanel.repaint();
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private JButton makeHeroButton(String label) {
        JButton button = new JButton(label);
        button.addActionListener(e -> selectHero(label));
        return button;
    }
    
    private void selectHero(String label) {}

    private void updateHeroButtons() {
        List<PartyMember> members = controller.getParty().getMembers();
        for (int i = 0; i < heroButtons.getComponentCount(); i++) {
            JButton button = (JButton) heroButtons.getComponent(i);
            PartyMember member = members.get(i);
            button.setEnabled(member.isAlive() && controller.getCombatEngine().isPlayerTurn() && member == selectedHero);
            button.setBackground(selectedHero == member ? new Color(200, 230, 255) : null);
        }
    }

    private JPanel buildEnemyCard(Enemy enemy) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setOpaque(false);

        JLabel spriteLabel = new JLabel();
        String spriteKey = "enemy_" + enemy.getName();
        Image img = ImageManager.loadSprite(spriteKey);
        boolean isBoss = "Señor Dracónico".equalsIgnoreCase(enemy.getName());
        int numEnemies = controller.getCombatEngine().getEnemies().size();
        int width;
        int height;
        if (isBoss) {
            width = 600;
            height = 450;
        } else {
            width = switch (numEnemies) {
                case 1 -> 450;
                case 2 -> 320;
                default -> 220;
            };
            height = switch (numEnemies) {
                case 1 -> 337;
                case 2 -> 240;
                default -> 165;
            };
        }
        if (img != null) {
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaled));
            spriteLabel.setHorizontalAlignment(JLabel.CENTER);
        } else {
            spriteLabel.setText("[" + enemy.getName() + "]");
            spriteLabel.setFont(spriteLabel.getFont().deriveFont(Font.BOLD, 14f));
            spriteLabel.setForeground(Color.WHITE);
            spriteLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        spriteLabel.setEnabled(enemy.isAlive());
        card.add(spriteLabel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        statusPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(enemy.getName() + (enemy.isAlive() ? "" : " (KO)"), JLabel.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setEnabled(enemy.isAlive());
        statusPanel.add(nameLabel);

        JProgressBar bar = new JProgressBar(0, enemy.getMaxHp());
        bar.setValue(enemy.getCurrentHp());
        bar.setStringPainted(true);
        bar.setString(enemy.getCurrentHp() + " / " + enemy.getMaxHp() + " HP");
        bar.setForeground(enemy.isAlive() ? new Color(160, 40, 40) : Color.GRAY);
        statusPanel.add(bar);

        card.add(statusPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildHeroCard(PartyMember member) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setOpaque(false);

        JLabel spriteLabel = new JLabel();
        String spriteKey = "party_" + member.getCharacterClass().name().toLowerCase();
        Image img = ImageManager.loadSprite(spriteKey);
        if (img != null) {
            Image scaled = img.getScaledInstance(180, 240, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaled));
            spriteLabel.setHorizontalAlignment(JLabel.CENTER);
        } else {
            spriteLabel.setText("[" + member.getCharacterClass().getDisplayName() + "]");
            spriteLabel.setFont(spriteLabel.getFont().deriveFont(Font.BOLD, 14f));
            spriteLabel.setForeground(Color.WHITE);
            spriteLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        spriteLabel.setEnabled(member.isAlive());
        card.add(spriteLabel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        statusPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(member.getName() + (member.isAlive() ? "" : " (KO)"), JLabel.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setEnabled(member.isAlive());
        statusPanel.add(nameLabel);

        JProgressBar bar = new JProgressBar(0, member.getMaxHp());
        bar.setValue(member.getCurrentHp());
        bar.setStringPainted(true);
        bar.setString(member.getCurrentHp() + " / " + member.getMaxHp() + " HP");
        bar.setForeground(member.isAlive() ? new Color(180, 50, 50) : Color.GRAY);
        statusPanel.add(bar);

        card.add(statusPanel, BorderLayout.SOUTH);
        return card;
    }

    private void performAttack() {
        Enemy target = (Enemy) enemyTargetBox.getSelectedItem();
        if (selectedHero == null || target == null) {
            return;
        }
        controller.getCombatEngine().attack(selectedHero, target);
        controller.checkCombatEnd();
        if (controller.getScreen() == GameScreen.COMBAT) {
            controller.notifyListeners();
        }
    }

    private void performSkill() {
        if (selectedHero == null) {
            return;
        }
        Enemy enemyTarget = (Enemy) enemyTargetBox.getSelectedItem();
        PartyMember allyTarget = (PartyMember) allyTargetBox.getSelectedItem();
        controller.getCombatEngine().useSkill(selectedHero, allyTarget, enemyTarget);
        controller.checkCombatEnd();
        if (controller.getScreen() == GameScreen.COMBAT) {
            controller.notifyListeners();
        }
    }
}
