/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import ui.container.TabbedContainer;

/**
 *
 * @author PersianDevStudio
 */
public class CustomeTabbedPaneUI extends BasicTabbedPaneUI {

    private static BufferedImage IMAGE_CLOSE_HOVER;
    private static BufferedImage IMAGE_CLOSE_NORMAL;

    static {
        IMAGE_CLOSE_HOVER = AppResource.getImage(AppResource.ICON_CLOSE_SMALL, AppTheme.COLOR_WHITE);
        IMAGE_CLOSE_NORMAL = AppResource.getImage(AppResource.ICON_CLOSE_SMALL, AppTheme.COLOR_TEXT_DEFAULT);
    }

    private TabbedContainer tabbedContainer;

    private Color borderColor = new Color(200, 200, 200);

    private boolean tabsOverlapBorder = true;

    public CustomeTabbedPaneUI(TabbedContainer tabbedPane) {
        this.tabbedContainer = tabbedPane;
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new BasicTabbedPaneUI.TabbedPaneLayout() {
            @Override
            protected void calculateTabRects(int tabPlacement, int tabCount) {
                final int spacer = -7; // should be non-negative
                final int indent = -4;

                super.calculateTabRects(tabPlacement, tabCount);

                for (int i = 0; i < rects.length; i++) {
                    rects[i].x += i * spacer + indent;
                }
            }
        };
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        int originalWidth = metrics.stringWidth(tabbedContainer.getTitleAt(tabIndex)) + 20 + (tabbedContainer.isClosable() ? 15 : 0);
        int minWidth = 80;
        return Math.max(originalWidth, minWidth);

    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return 25;
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
//            g.setColor(lightHighlight);

        g.setColor(borderColor);
        
        int margin = isSelected ? 2 : 1;
        g.drawLine(x, y, x, y + h - margin); // left highlight

        g.drawLine(x, y, x + w, y); // top highlight

        g.drawLine(x + w, y, x + w, y + h - margin); // right shadow

        
        
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {

        Graphics2D g2D = (Graphics2D) g;

//        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect); //To change body of generated methods, choose Tools | Templates.
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabbedContainer.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;

        g2D.setColor(borderColor);
        g2D.drawLine(tabRect.x, tabRect.y, tabRect.x + tabRect.width, tabRect.y);
        int margin = isSelected ? 2 : 0;
        g2D.drawLine(tabRect.x, tabRect.y, tabRect.x, tabRect.y + tabRect.height - margin);
        g2D.drawLine(tabRect.x + tabRect.width, tabRect.y, tabRect.x + tabRect.width, tabRect.y + tabRect.height - margin);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(tabRect.x + 1, tabRect.y + 1, tabRect.width - 1, tabRect.height - 1);

        g2D.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        FontMetrics fm = getFontMetrics();

        String str = tabPane.getTitleAt(tabIndex);
        int x;

        if (tabbedContainer.isClosable()) {
            x = tabRect.x + 10;
        } else {
            x = tabRect.x + (tabRect.width - fm.stringWidth(str)) / 2;
        }

        int y = tabRect.y + ((tabRect.height - fm.getHeight()) / 2) + fm.getAscent();

        g2D.setColor(isSelected ? AppTheme.COLOR_MAIN : AppTheme.COLOR_TEXT_DEFAULT);
        g2D.drawString(str, x, y);

        if (tabbedContainer.isClosable()) {
            int w = 10;
            x = tabRect.x + tabRect.width - w - 5;
            y = tabRect.y + 5;
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            tabbedContainer.setCloseCoordinates(tabIndex, x, y, w, w);
            if (tabbedContainer.mousePositionIsInside(x, y, w, w)) {
                g2D.setColor(AppTheme.COLOR_WARNING);
                g2D.fillRect(x - 1, y - 1, w + 2, w + 2);
                g2D.drawImage(IMAGE_CLOSE_HOVER, x, y, w, w, null);
            } else {
                g2D.drawImage(IMAGE_CLOSE_NORMAL, x, y, w, w, null);
            }

        }

    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
//        super.paintContentBorder(g, tabPlacement, selectedIndex);
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();

        int x = 0;
        int y = 0;
        int w = width;
        int h = height;

        y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        if (tabsOverlapBorder) {
            y -= tabAreaInsets.bottom;
        }
        h -= (y);

        g.setColor(borderColor);
        g.drawLine(x, y, x + w, y);
        g.drawLine(x, y + h - 1, x + w, y + h - 1);

//        g.setColor(tabbedContainer.getBackground());
        g.setColor(Color.WHITE);
        g.fillRect(x + 1, y + 1, w - 2, h - 2);

    }

}
