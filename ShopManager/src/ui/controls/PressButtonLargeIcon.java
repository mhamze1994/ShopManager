/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * Draws the text at center bottom and icon on top of it
 *
 * @author PersianDevStudio
 */
public class PressButtonLargeIcon extends PressButton {

    public BufferedImage image;

    private boolean mouseDown;

    private boolean mouseHovering;

    public PressButtonLargeIcon() {
        init();

    }

    public PressButtonLargeIcon(Icon icon) {
        super(icon);
        init();

    }

    public PressButtonLargeIcon(String text) {
        super(text);
        init();

    }

    public PressButtonLargeIcon(Action a) {
        super(a);
        init();

    }

    public PressButtonLargeIcon(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setTextHorizontalPosition(PressButton.POSITION_CENTER);
        setTextVerticalPosition(PressButton.POSITION_BOTTOM);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2D = (Graphics2D) g;

        FontMetrics fm = getFontMetrics(getFont());
        if (image != null) {
            int m = fm.getHeight();
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.drawImage(image, m, 5, getWidth() - 2 * m, getWidth() - 2 * m, null);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

}
