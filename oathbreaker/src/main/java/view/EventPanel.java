package view;

import controller.GameController;
import controller.GameScreen;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// Panel que muestra los eventos del juego, como descansos, tesoros, victorias y derrotas,
// con un título, un mensaje y un botón de acción contextual según el evento actual.

public final class EventPanel extends BackgroundPanel {
    private final JLabel titleLabel = new JLabel("", JLabel.CENTER);
    private final JLabel messageLabel = new JLabel("", JLabel.CENTER);
    private final JButton actionButton = new JButton("Continuar");
    private final JPanel messageBoxPanel = new JPanel();
    private GameController controller;
    private Runnable restartAction;

    public EventPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 28f));
        messageLabel.setFont(messageLabel.getFont().deriveFont(16f));

        messageBoxPanel.setOpaque(false);
        messageBoxPanel.setLayout(new GridBagLayout());

        GridBagConstraints innerC = new GridBagConstraints();
        innerC.gridx = 0;
        innerC.gridy = 0;
        innerC.insets = new Insets(0, 0, 15, 0);
        innerC.anchor = GridBagConstraints.CENTER;
        messageBoxPanel.add(titleLabel, innerC);

        innerC.gridy = 1;
        innerC.insets = new Insets(0, 0, 0, 0);
        messageBoxPanel.add(messageLabel, innerC);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        add(messageBoxPanel, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(30, 10, 10, 10);
        actionButton.addActionListener(e -> onAction());
        add(actionButton, c);
    }

    public void setRestartAction(Runnable restartAction) {
        this.restartAction = restartAction;
    }

    public void bind(GameController controller) {
        this.controller = controller;
    }

    public void refresh() {
        if (controller == null) {
            return;
        }

        GameScreen screen = controller.getScreen();
        messageLabel.setText("<html><div style='text-align:center;width:420px'>"
                + controller.getStatusMessage() + "</div></html>");

        messageBoxPanel.setOpaque(true);
        messageBoxPanel.setBackground(java.awt.Color.WHITE);
        messageBoxPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        titleLabel.setForeground(new java.awt.Color(30, 30, 30));
        messageLabel.setForeground(new java.awt.Color(60, 60, 60));
        
        switch (screen) {
            case REST -> {
                setBackgroundImage("rest");
                titleLabel.setText("Descanso");
                actionButton.setText("Continuar viaje");
            }
            case TREASURE -> {
                setBackgroundImage("treasure");
                titleLabel.setText("Tesoro Encontrado");
                actionButton.setText("Continuar viaje");
            }
            case VICTORY -> {
                setBackgroundImage("victory");
                titleLabel.setText("Victoria!");
                actionButton.setText("Jugar Otra Vez");
            }
            case DEFEAT -> {
                setBackgroundImage("defeat");
                titleLabel.setText("Derrota");
                actionButton.setText("Intentar de Nuevo");
            }
            default -> {
            }
        }
    }

    private void onAction() {
        if (controller == null) {
            return;
        }
        GameScreen screen = controller.getScreen();
        if (screen == GameScreen.VICTORY || screen == GameScreen.DEFEAT) {
            if (restartAction != null) {
                restartAction.run();
            }
        } else {
            controller.continueFromEvent();
        }
    }
}
