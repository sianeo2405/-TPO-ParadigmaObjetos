package view;

import controller.GameController;
import controller.GameScreen;
import model.PartyMember;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;

// Panel que muestra la información del equipo del jugador del lado derecho,
// incluyendo los miembros de la party y el oro disponible.

public final class PartyPanel extends JPanel {
    private final HeroCard warrior = new HeroCard();
    private final HeroCard mage = new HeroCard();
    private final HeroCard archer = new HeroCard();
    private final HeroCard healer = new HeroCard();
    private final JLabel goldLabel = new JLabel("Oro disponible: 0g", SwingConstants.CENTER);

    public PartyPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createTitledBorder("Equipo"));
        setPreferredSize(new Dimension(300, 0));

        JPanel goldPanel = new JPanel(new BorderLayout());
        goldPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        goldLabel.setFont(goldLabel.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        goldLabel.setForeground(new Color(218, 165, 32)); 
        goldPanel.add(goldLabel, BorderLayout.CENTER);
        add(goldPanel, BorderLayout.NORTH);

        JPanel heroesPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        heroesPanel.add(warrior);
        heroesPanel.add(mage);
        heroesPanel.add(archer);
        heroesPanel.add(healer);
        add(heroesPanel, BorderLayout.CENTER);
    }

    public void refresh(GameController controller) {
        var members = controller.getParty().getMembers();
        PartyMember activeHero = null;
        if (controller.getScreen() == GameScreen.COMBAT) {
            activeHero = controller.getCombatEngine().getActiveHero();
        }

        warrior.refresh(members.get(0), members.get(0) == activeHero);
        mage.refresh(members.get(1), members.get(1) == activeHero);
        archer.refresh(members.get(2), members.get(2) == activeHero);
        healer.refresh(members.get(3), members.get(3) == activeHero);

        int gold = controller.getParty().getGold();
        goldLabel.setText("Oro disponible: " + gold + "g");
    }

    private static final class HeroCard extends JPanel {
        private final JLabel spriteLabel = new JLabel();
        private final JLabel nameLabel = new JLabel();
        private final JLabel classLabel = new JLabel();
        private final JProgressBar hpBar = new JProgressBar(0, 100);
        private final JProgressBar mpBar = new JProgressBar(0, 100);
        private final JLabel statsLabel = new JLabel();

        HeroCard() {
            setLayout(new BorderLayout(8, 4));
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

            JPanel infoPanel = new JPanel(new GridLayout(5, 1, 2, 2));
            infoPanel.setOpaque(false);
            nameLabel.setFont(nameLabel.getFont().deriveFont(14f));
            hpBar.setStringPainted(true);
            mpBar.setStringPainted(true);
            hpBar.setForeground(new Color(180, 50, 50));
            mpBar.setForeground(new Color(50, 90, 180));
            infoPanel.add(nameLabel);
            infoPanel.add(classLabel);
            infoPanel.add(hpBar);
            infoPanel.add(mpBar);
            infoPanel.add(statsLabel);

            add(spriteLabel, BorderLayout.WEST);
            add(infoPanel, BorderLayout.CENTER);
        }

void refresh(PartyMember member, boolean isActive) {
            nameLabel.setText(member.getName() + " (Nv " + member.getLevel() + ")");
            
            classLabel.setText(member.getRoleName() + " — " + member.getSkillName());
            
            hpBar.setMaximum(member.getMaxHp());
            hpBar.setValue(member.getCurrentHp());
            hpBar.setString(member.getCurrentHp() + " / " + member.getMaxHp() + " HP");
            mpBar.setMaximum(member.getMaxMp());
            mpBar.setValue(member.getCurrentMp());
            mpBar.setString(member.getCurrentMp() + " / " + member.getMaxMp() + " MP");
            statsLabel.setText("ATK " + member.getAttack() + "  DEF " + member.getDefense() + "  SPD " + member.getSpeed());
            
            String spriteKey = "party_" + member.getClass().getSimpleName().toLowerCase();
            Image img = ImageManager.loadSidebarSprite(spriteKey);
            if (img != null) {
                Image scaled = img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                spriteLabel.setIcon(new ImageIcon(scaled));
                spriteLabel.setVisible(true);
            } else {
                spriteLabel.setIcon(null);
                spriteLabel.setVisible(false);
            }

            if (isActive) {
                setBackground(new Color(255, 223, 128)); 
            } else {
                setBackground(member.isAlive() ? new Color(245, 245, 245) : new Color(220, 220, 220));
            }
            setEnabled(member.isAlive());
            nameLabel.setEnabled(member.isAlive());
            classLabel.setEnabled(member.isAlive());
            hpBar.setEnabled(member.isAlive());
            mpBar.setEnabled(member.isAlive());
            statsLabel.setEnabled(member.isAlive());
            spriteLabel.setEnabled(member.isAlive());
        }
    }   
}