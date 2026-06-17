package com.nodequest.ui;

import com.nodequest.game.GameController;
import com.nodequest.game.GameMap;
import com.nodequest.model.MapNode;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapPanel extends BackgroundPanel {
    private static final int NODE_RADIUS = 22;
    private static final int ROW_HEIGHT = 80;   
    private static final int H_PAD      = 40;   
    private static final Color BG        = new Color(28, 32, 48);
    private static final Color LINE      = new Color(90, 100, 130);
    private static final Color VISITED   = new Color(70, 75, 95);
    private static final Color CURRENT   = new Color(240, 190, 60);
    private static final Color AVAILABLE = new Color(90, 170, 255);

    private GameController controller;
    private final Map<Integer, Point2D.Double> nodeCenters = new HashMap<>();
    private Integer hoveredNodeId;

    public MapPanel() {
        super("map");
        setPreferredSize(new Dimension(640, 600));
        setBackground(BG);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller == null) return;
                Integer nodeId = nodeAt(e.getX(), e.getY());
                if (nodeId != null && controller.getMap().canMoveTo(nodeId)) {
                    controller.selectNode(nodeId);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Integer nodeId = nodeAt(e.getX(), e.getY());
                boolean canMove = nodeId != null && controller != null
                        && controller.getMap().canMoveTo(nodeId);
                setCursor(canMove
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
                if (nodeId != hoveredNodeId) {
                    hoveredNodeId = nodeId;
                    repaint();
                }
            }
        });
    }

    public void bind(GameController controller) {
        this.controller = controller;

        
        int rows = controller.getMap().getTotalRows();
        int logicalHeight = rows * ROW_HEIGHT + H_PAD * 2;
        setPreferredSize(new Dimension(640, logicalHeight));
        revalidate();

        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = getScrollPane();
            if (sp != null) {
                sp.getVerticalScrollBar().setValue(
                        sp.getVerticalScrollBar().getMaximum());
            }
        });

        repaint();
    }

    private JScrollPane getScrollPane() {
        java.awt.Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JScrollPane) return (JScrollPane) parent;
            if (parent.getParent() instanceof JScrollPane)
                return (JScrollPane) parent.getParent();
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (controller == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GameMap map = controller.getMap();
        nodeCenters.clear();
        layoutNodes(map);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(LINE);
        for (MapNode node : map.allNodes()) {
            Point2D.Double from = nodeCenters.get(node.getId());
            for (int targetId : node.getOutgoingIds()) {
                Point2D.Double to = nodeCenters.get(targetId);
                if (from != null && to != null) {
                    g2.draw(new Line2D.Double(from.x, from.y, to.x, to.y));
                }
            }
        }

        for (MapNode node : map.allNodes()) {
            drawNode(g2, node, map.getCurrentNode().getId() == node.getId());
        }

        g2.setColor(new Color(220, 220, 230));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        //g2.drawString("Escala la Torre - elige un piso para explorar.", 20, 28);

        g2.dispose();
    }


    private void layoutNodes(GameMap map) {
        int width      = getWidth();
        int totalRows  = map.getTotalRows();
        int canvasH    = getHeight();          

        List<List<MapNode>> rowList = map.getRows();
        for (int row = 0; row < rowList.size(); row++) {
            List<MapNode> nodes = rowList.get(row);

            double t = (double) row / Math.max(1, totalRows - 1); // 0..1
            double y = canvasH - H_PAD - t * (canvasH - H_PAD * 2);

            for (MapNode node : nodes) {
                double x;
                if (nodes.size() == 1) {
                    x = width / 2.0;
                } else {
                    x = width * (node.getColumn() + 1.0) / (nodes.size() + 1.0);
                }
                nodeCenters.put(node.getId(), new Point2D.Double(x, y));
            }
        }
    }

    private void drawNode(Graphics2D g2, MapNode node, boolean isCurrent) {
        Point2D.Double center = nodeCenters.get(node.getId());
        if (center == null) return;

        Color fill;
        if (isCurrent) {
            fill = CURRENT;
        } else if (node.isAvailable()) {
            fill = hoveredNodeId != null && hoveredNodeId == node.getId()
                    ? AVAILABLE.brighter() : AVAILABLE;
        } else if (node.isVisited()) {
            fill = VISITED;
        } else {
            fill = new Color(45, 50, 68);
        }

        Ellipse2D circle = new Ellipse2D.Double(
                center.x - NODE_RADIUS, center.y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);
        g2.setColor(fill);
        g2.fill(circle);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(isCurrent || node.isAvailable() ? 3f : 1.5f));
        g2.draw(circle);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g2.getFontMetrics();
        String icon = node.getType().getIcon();
        g2.drawString(icon,
                (float) (center.x - fm.stringWidth(icon) / 2.0),
                (float) (center.y + fm.getAscent() / 2.0 - 2));

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
        fm = g2.getFontMetrics();
        String label = node.getType().getLabel();
        g2.setColor(new Color(200, 205, 220));
        g2.drawString(label,
                (float) (center.x - fm.stringWidth(label) / 2.0),
                (float) (center.y + NODE_RADIUS + 14));
    }

    private Integer nodeAt(int x, int y) {
        for (Map.Entry<Integer, Point2D.Double> entry : nodeCenters.entrySet()) {
            Point2D.Double c = entry.getValue();
            double dx = x - c.x;
            double dy = y - c.y;
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                return entry.getKey();
            }
        }
        return null;
    }
}
