/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.container;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Studio
 */
public class GroupPane extends JPanel {

    public BufferedImage backgroundImage;

    public GroupPane(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        init();
    }

    public GroupPane(LayoutManager layout) {
        super(layout);
        init();
    }

    public GroupPane(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        init();
    }

    public GroupPane() {
        init();
    }

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        }
        super.paintComponent(g);
    }


    private void init() {
        setBackground(Color.WHITE);
    }
}
