/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.awt.AlphaComposite;
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
public class ImageButton extends PressButton {

    public BufferedImage image;

    private boolean mouseDown;

    private boolean mouseHovering;

    public ImageButton() {
        init();
    }

    public ImageButton(Icon icon) {
        super(icon);
        init();
    }

    public ImageButton(String text) {
        super(text);
        init();
    }

    public ImageButton(Action a) {
        super(a);
        init();
    }

    public ImageButton(String text, Icon icon) {
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

//        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
//        g2D.setComposite(ac);

//        g2D.setColor(getBackground());
//        g2D.fillRect(0, 0, getWidth(), getHeight());

        if (image != null) {
            Insets insets = getInsets();
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int img_width = getWidth() - 2 * 15;
            int img_height = getHeight() - 2 * 15;
            g2D.drawImage(image, (getWidth() - img_width)/2, 5, img_width, img_height, null);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}
