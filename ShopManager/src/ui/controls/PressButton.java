/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import ui.AppTheme;

/**
 *
 * @author Studio
 */
public class PressButton extends JButton {

    private boolean mouseDown;

    private boolean mouseHovering;

    private int textVerticalPosition;

    private int textHorizontalPosition;

    private int padding = 5;

    private boolean drawTextUnderline;

    private boolean drawBottomSeparator;

    private boolean drawRightSeparator;

    public PressButton() {
        init();
    }

    public PressButton(Icon icon) {
        super(icon);
        init();
    }

    public PressButton(String text) {
        super(text);
        init();
    }

    public PressButton(Action a) {
        super(a);
        init();
    }

    public PressButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setOpaque(false);
        addMouseMotionListener(new MouseMotionListenerImpl());
        addMouseListener(new MouseListenerImpl());
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setTextHorizontalPosition(PressButton.POSITION_RIGHT);
        setTextVerticalPosition(PressButton.POSITION_CENTER);
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;

        if (mouseHovering) {
            if (mouseDown) {
                g2D.setColor(AppTheme.COLOR_SILVER_DARKER);
            } else {
                g2D.setColor(AppTheme.COLOR_SILVER);
            }
            g2D.fillRect(0, 0, getWidth(), getHeight());
        } else if (hasFocus()) {
            g2D.setColor(AppTheme.COLOR_MAIN);
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
            Stroke defaultStroke = g2D.getStroke();
            g2D.setStroke(dashed);
            g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2D.setStroke(defaultStroke);
        }

        FontMetrics fm = getFontMetrics(getFont());

        g2D.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int stringWidth = fm.stringWidth(getText());
        int x;
        switch (textHorizontalPosition) {
            case POSITION_RIGHT:
                x = getWidth() - stringWidth - padding;
                break;
            case POSITION_LEFT:
                x = padding;
                break;
            case POSITION_CENTER:
                x = (getWidth() - stringWidth) / 2;
                break;
            default:
                x = 0;
        }

        int y;
        switch (textVerticalPosition) {
            case POSITION_TOP:
                y = fm.getHeight() + padding;
                break;
            case POSITION_BOTTOM:
                y = getHeight() - fm.getHeight() - padding + fm.getAscent();
                break;
            case POSITION_CENTER:
                y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                break;
            default:
                y = 0;
        }

        if (mouseHovering || hasFocus()) {
            g2D.setColor(AppTheme.COLOR_MAIN);
        } else {
            g2D.setColor(AppTheme.COLOR_TEXT_DEFAULT);
        }
        g2D.drawString(getText(), x, y);

        if (drawTextUnderline) {
            g2D.drawLine(x, y + fm.getDescent(), x + stringWidth, y + fm.getDescent());
        }

        if (drawBottomSeparator) {
            if (mouseDown && mouseHovering) {
                g2D.setColor(AppTheme.COLOR_SILVER_DARKER.darker());
            } else {
                g2D.setColor(AppTheme.COLOR_SILVER_DARKER);
            }
            g2D.drawLine(padding, getHeight() - 1, getWidth() - padding, getHeight() - 1);
        }

        if (drawRightSeparator) {
            if (mouseDown && mouseHovering) {
                g2D.setColor(AppTheme.COLOR_SILVER_DARKER.darker());
            } else {
                g2D.setColor(AppTheme.COLOR_SILVER_DARKER);
            }
            g2D.drawLine(getWidth() - 1, padding, getWidth() - 1, getHeight() - padding);
        }
    }

    public int getTextVerticalPosition() {
        return textVerticalPosition;
    }

    public void setTextVerticalPosition(int textVerticalPosition) {
        this.textVerticalPosition = textVerticalPosition;
    }

    public int getTextHorizontalPosition() {
        return textHorizontalPosition;
    }

    public void setTextHorizontalPosition(int textHorizontalPosition) {
        this.textHorizontalPosition = textHorizontalPosition;
    }

    public void drawTextUnderline(boolean drawUnderline) {
        this.drawTextUnderline = drawUnderline;
    }

    public void drawBottomSeparator(boolean drawBottomSeparator) {
        this.drawBottomSeparator = drawBottomSeparator;
    }

    public void drawRightSeparator(boolean drawRightSeparator) {
        this.drawRightSeparator = drawRightSeparator;
    }

    private class MouseMotionListenerImpl implements MouseMotionListener {

        public MouseMotionListenerImpl() {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    private class MouseListenerImpl implements MouseListener {

        public MouseListenerImpl() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDown = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDown = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            mouseHovering = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseHovering = false;
        }
    }

    public static final int POSITION_CENTER/**/ = 1;
    public static final int POSITION_RIGHT/* */ = 2;
    public static final int POSITION_LEFT/*  */ = 3;
    public static final int POSITION_TOP/*   */ = 4;
    public static final int POSITION_BOTTOM/**/ = 5;
}
