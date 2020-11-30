/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import ui.controls.PressButton;

/**
 *
 * @author PersianDevStudio
 */
public class CustomeTableHeaderRenderer implements TableCellRenderer {

    private final PressButton tv;

    public CustomeTableHeaderRenderer() {
        tv = new PressButton();
        tv.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        tv.drawRightSeparator(true);
        tv.setPreferredSize(new Dimension(0, 30));

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            tv.setText("");
        } else {
            tv.setText(String.valueOf(value));
        }
        return tv;
    }
}
