package view;

import controller.CombatEngine;
import model.Combatant;
import model.PartyMember;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.List;

// Panel que muestra el orden de turno en combate, con los sprites de los combatientes y resaltando al activo.

public final class TurnOrderPanel extends JPanel {
    private CombatEngine combatEngine;

    private static final int D_NORMAL = 48;
    private static final int D_ACTIVE = 62;
    private static final int GAP = 20;
    private static final int PANEL_HEIGHT = 95;

    public TurnOrderPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(800, PANEL_HEIGHT));
    }

    public void refresh(CombatEngine combatEngine) {
        this.combatEngine = combatEngine;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (combatEngine == null) {
            return;
        }

        List<Combatant> turnOrder = combatEngine.getTurnOrder();
        if (turnOrder == null || turnOrder.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int cy = h / 2 - 8;

        int currentIndex = combatEngine.getCurrentCombatantIndex();
        int n = turnOrder.size();

        int totalWidth = 0;
        for (int i = 0; i < n; i++) {
            boolean isActive = (i == currentIndex);
            totalWidth += isActive ? D_ACTIVE : D_NORMAL;
            if (i < n - 1) {
                totalWidth += GAP;
            }
        }

        int startX = (w - totalWidth) / 2;
        int currentX = startX;

        int lineY = cy;
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(startX + D_NORMAL / 2, lineY, startX + totalWidth - D_NORMAL / 2, lineY);

        for (int i = 0; i < n; i++) {
            Combatant c = turnOrder.get(i);
            boolean isActive = (i == currentIndex);
            int d = isActive ? D_ACTIVE : D_NORMAL;
            int cx = currentX + d / 2;

            Composite origComp = g2.getComposite();
            if (!c.isAlive()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            }

            g2.setColor(new Color(15, 15, 20, 200));
            g2.fill(new Ellipse2D.Double(cx - d / 2.0, cy - d / 2.0, d, d));

            Image img = getCombatantSprite(c);
            Shape oldClip = g2.getClip();
            double clipD = d - 4.0;
            g2.setClip(new Ellipse2D.Double(cx - clipD / 2.0, cy - clipD / 2.0, clipD, clipD));

            if (img != null) {
                int imgW = img.getWidth(null);
                int imgH = img.getHeight(null);
                if (imgW > 0 && imgH > 0) {
                    double scale;
                    int sw, sh;
                    int ox, oy;
                    if (imgW > imgH) {
                        scale = clipD / imgH;
                        sw = (int) (imgW * scale);
                        sh = (int) clipD;
                        ox = (int) (cx - sw / 2.0);
                        oy = (int) (cy - clipD / 2.0);
                    } else {
                        scale = clipD / imgW;
                        sw = (int) clipD;
                        sh = (int) (imgH * scale);
                        ox = (int) (cx - clipD / 2.0);
                        oy = (int) (cy - clipD / 2.0);
                    }
                    g2.drawImage(img, ox, oy, sw, sh, null);
                }
            } else {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, isActive ? 18 : 14));
                String initial = c.getName().isEmpty() ? "?" : c.getName().substring(0, 1);
                FontMetrics fm = g2.getFontMetrics();
                int tx = cx - fm.stringWidth(initial) / 2;
                int ty = cy + (fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, tx, ty);
            }

            g2.setClip(oldClip);

            Color borderColor;
            if (isActive) {
                borderColor = new Color(251, 191, 36); 
            } else if (c instanceof PartyMember) {
                borderColor = new Color(56, 189, 248); 
            } else {
                borderColor = new Color(239, 68, 68); 
            }

            float borderW = isActive ? 4.0f : 2.5f;
            g2.setStroke(new BasicStroke(borderW));
            g2.setColor(borderColor);
            g2.draw(new Ellipse2D.Double(cx - d / 2.0 + borderW / 2.0, cy - d / 2.0 + borderW / 2.0, d - borderW, d - borderW));

            
            if (isActive) {
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(251, 191, 36, 120));
                double outerD = d + 5.0;
                g2.draw(new Ellipse2D.Double(cx - outerD / 2.0, cy - outerD / 2.0, outerD, outerD));
            }

            
            if (!c.isAlive()) {
                g2.setStroke(new BasicStroke(3.0f));
                g2.setColor(new Color(239, 68, 68, 220));
                int r = d / 4;
                g2.drawLine(cx - r, cy - r, cx + r, cy + r);
                g2.drawLine(cx + r, cy - r, cx - r, cy + r);
            }

            g2.setComposite(origComp);

            if (isActive) {
                g2.setColor(new Color(251, 191, 36)); 
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            } else {
                g2.setColor(new Color(220, 220, 220));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            }
            int name = c.getSpeed();
            
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx - fm.stringWidth(String.valueOf(name)) / 2;
            int ty = cy + d / 2 + 13;
            g2.drawString(String.valueOf(name), tx, ty);

            currentX += d + GAP;
        }

        g2.dispose();
    }

private Image getCombatantSprite(Combatant c) {
        // Usamos nuestro método polimórfico en lugar de instanceof
        if (c.isPlayerControlled()) {
            // CORRECCIÓN: Usamos Reflection igual que en PartyPanel y CombatPanel
            String key = "party_" + c.getClass().getSimpleName().toLowerCase();
            
            Image img = ImageManager.loadTurnOrderSprite(key);
            if (img == null) {
                img = ImageManager.loadSidebarSprite(key);
            }
            if (img == null) {
                img = ImageManager.loadSprite(key);
            }
            return img;
            
        } else {
            // Si no es controlado por el jugador, es un enemigo.
            // Como getName() ya existe en la clase abstracta Combatant, no necesitamos hacer cast a (Enemy)
            String key = "enemy_" + c.getName();
            
            Image img = ImageManager.loadTurnOrderSprite(key);
            if (img == null) {
                img = ImageManager.loadSprite(key);
            }
            return img;
        }
    }
}
