package view;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;

// Panel que muestra una imagen de fondo, escalada según el modo de escala seleccionado.

public class BackgroundPanel extends JPanel {
    public enum ScaleMode {
        STRETCH,
        COVER,
        FIT
    }

    private Image backgroundImage;
    private ScaleMode scaleMode = ScaleMode.COVER;

    public BackgroundPanel() {
        
    }

    public BackgroundPanel(String imageKey) {
        this();
        setBackgroundImage(imageKey);
    }

    public void setBackgroundImage(String imageKey) {
        if (imageKey == null || imageKey.trim().isEmpty()) {
            this.backgroundImage = null;
        } else {
            this.backgroundImage = ImageManager.loadImage(imageKey);
        }
        repaint();
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        if (backgroundImage != null) {
            int width = getWidth();
            int height = getHeight();

            if (scaleMode == ScaleMode.STRETCH) {
                g.drawImage(backgroundImage, 0, 0, width, height, this);
            } else if (scaleMode == ScaleMode.COVER) {
                int imgW = backgroundImage.getWidth(this);
                int imgH = backgroundImage.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    double scale = Math.max((double) width / imgW, (double) height / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (width - drawW) / 2;
                    int y = (height - drawH) / 2;
                    g.drawImage(backgroundImage, x, y, drawW, drawH, this);
                } else {
                    g.drawImage(backgroundImage, 0, 0, width, height, this);
                }
            } else if (scaleMode == ScaleMode.FIT) {
                int imgW = backgroundImage.getWidth(this);
                int imgH = backgroundImage.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    double scale = Math.min((double) width / imgW, (double) height / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (width - drawW) / 2;
                    int y = (height - drawH) / 2;
                    g.drawImage(backgroundImage, x, y, drawW, drawH, this);
                } else {
                    g.drawImage(backgroundImage, 0, 0, width, height, this);
                }
            }
        }
    }
}
