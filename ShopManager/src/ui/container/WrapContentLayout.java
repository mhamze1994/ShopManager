/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.container;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;

/**
 *
 * @author Studio
 */
public class WrapContentLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Insets i = parent.getInsets();
        return new Dimension(parent.getSize().width + i.left + i.right, parent.getSize().height + i.top + i.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Insets i = parent.getInsets();
        return new Dimension(i.left + i.right, parent.getSize().height + i.top + i.bottom);
    }

    int order = 1;
    int verticalOffset = 10;
    int horizontalOffset = 5;

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        Point location = parent.getLocation();
        int parentWidth = parent.getWidth();

        int availableWidth = parentWidth;
        int yPosition = verticalOffset + insets.top;
        int consumedWidth = 0;
        Component[] allComponents = parent.getComponents();
        int lastHeight = 0;
        for (int i = 0; i < allComponents.length; i++) {
            Component c = allComponents[i];

            if (availableWidth < c.getPreferredSize().width) {
                availableWidth = parentWidth;
                yPosition += c.getPreferredSize().height + verticalOffset;
                consumedWidth = 0;
            }
            consumedWidth += c.getPreferredSize().width + horizontalOffset;
            c.setLocation(parentWidth - consumedWidth, yPosition);
            availableWidth -= (c.getPreferredSize().width + horizontalOffset);
            c.setSize(c.getPreferredSize());

            lastHeight = c.getPreferredSize().height;
        }
        //parent.setLocation(200, 100);
        parent.setSize(parentWidth, yPosition + lastHeight + verticalOffset + insets.bottom);
    }

}
