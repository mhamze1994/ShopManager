/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.container;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;
import ui.CustomeTabbedPaneUI;

/**
 *
 * @author PersianDevStudio
 */
public class TabbedContainer extends JTabbedPane {

    private boolean isClosable;

    private Cursor cursorHand = new Cursor(Cursor.HAND_CURSOR);
    private Cursor cursorDefault = new Cursor(Cursor.DEFAULT_CURSOR);

    private Point mousePosition;

    public TabbedContainer() {
        init();
    }

    public TabbedContainer(int tabPlacement) {
        super(tabPlacement);
        init();
    }

    public TabbedContainer(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        init();
    }

    private void init() {
        setUI(new CustomeTabbedPaneUI(this));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                closeTabIfClickedClose(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mousePosition = e.getPoint();
                repaint();
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
                repaint();
            }

        });

    }

    public void setClosable(boolean isClosable) {
        this.isClosable = isClosable;
        revalidate();
        repaint();
    }

    public boolean isClosable() {
        return isClosable;
    }

    private HashMap<Integer, Rectangle> closeCoordinates = new HashMap<>();

    public void setCloseCoordinates(int tabIndex, int x, int y, int width, int height) {
        Rectangle r;
        if (closeCoordinates.containsKey(tabIndex)) {
            r = closeCoordinates.get(tabIndex);
        } else {
            r = new Rectangle();
        }
        r.x = x;
        r.y = y;
        r.width = width;
        r.height = height;
        closeCoordinates.put(tabIndex, r);

    }

    private void closeTabIfClickedClose(Point point) {
        for (Map.Entry<Integer, Rectangle> entry : closeCoordinates.entrySet()) {

            Integer index = entry.getKey();
            Rectangle rect = entry.getValue();
            if (isInside(point, rect)) {
                remove(index);
                revalidate();
                repaint();
                return;
            }
        }
    }

    private boolean isInsideAny(Point point) {
        for (Map.Entry<Integer, Rectangle> entry : closeCoordinates.entrySet()) {
            Integer index = entry.getKey();
            Rectangle rect = entry.getValue();
            if (isInside(point, rect)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInside(Point point, Rectangle rect) {
        return isInside(point, rect.x, rect.y, rect.width, rect.height);
    }

    public boolean isInside(Point point, int x, int y, int w, int h) {
        if (point == null) {
            return false;
        }
        return point.x >= x && point.x <= x + w && point.y >= y && point.y <= y + h;
    }

    public boolean mousePositionIsInside(int x, int y, int w, int h) {
        return isInside(mousePosition, x, y, w, h);
    }

}
