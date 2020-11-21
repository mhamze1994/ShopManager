/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Action;
import javax.swing.Icon;

/**
 *
 * @author PersianDevStudio
 */
public class PressImageButton extends PressButton {

    public BufferedImage image;

    private boolean mouseDown;

    private boolean mouseHovering;
    
    private int p = 5;

    public PressImageButton() {

    }

    public PressImageButton(Icon icon) {
        super(icon);

    }

    public PressImageButton(String text) {
        super(text);

    }

    public PressImageButton(Action a) {
        super(a);

    }

    public PressImageButton(String text, Icon icon) {
        super(text, icon);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2D = (Graphics2D) g;

        g2D.setColor(getBackground());
        g2D.fillRect(0, 0, getWidth(), getHeight());
        
        if (image != null) {
            Insets insets = getInsets();
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.drawImage(image, p, p,
                    getWidth() - 2 * p,
                    getHeight() - 2 * p, null);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

}
