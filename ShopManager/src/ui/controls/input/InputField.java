/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import java.awt.Color;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Studio
 */
public class InputField extends JFormattedTextField {

    private String placeholder;

    private int paddingX = 0;

    private boolean focus;

    private Color defaultBackgroundColor;
    private Color defaultForegroundColor;

    public InputField() {
        init();
    }

    public InputField(String text) {
        super(text);
        init();
    }

    private void init() {
        defaultBackgroundColor = getBackground();
    }

    public void displayError() {
        setBackground(new Color(255, 40, 40));
        setForeground(Color.WHITE);
    }

    public void hideError() {
        setBackground(defaultBackgroundColor);
        setForeground(defaultForegroundColor);
    }
//
//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//
//        Map<?, ?> desktopHints
//                = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
//
//        if (desktopHints != null) {
//            g2d.setRenderingHints(desktopHints);
//        } else {
//            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        }
//        drawTexts(g2d);
//
//        g2d.setColor(getForeground());
//        if (focus) {
//            g2d.setStroke(new BasicStroke(3));
//        } else {
//            g2d.setStroke(new BasicStroke(1));
//        }
//        g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
//        g2d.drawLine(0, getHeight() - 5, 0, getHeight());
//        g2d.drawLine(getWidth() - 1, getHeight() - 5, getWidth() - 1, getHeight());
//
//    }
//
//    private void drawTexts(Graphics2D g2d) {
//        FontMetrics fm = g2d.getFontMetrics(getFont());
//        int x, y;
//        y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
//        String txt;
//        if (getText().isEmpty()) {
//            g2d.setColor(Color.GRAY);
//            txt = placeholder;
//        } else {
//            txt = "";
//            //txt = getText();
//        }
//
//        if (getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
//            x = getWidth() - fm.stringWidth(txt) - paddingX;
//        } else {
//            x = paddingX;
//        }
//        g2d.drawString(txt, x, y);
//    }
//
//    public String getPlaceholder() {
//        return this.placeholder;
//    }
//
//    public void setPlaceholder(String placeholder) {
//        this.placeholder = placeholder;
//    }
}
