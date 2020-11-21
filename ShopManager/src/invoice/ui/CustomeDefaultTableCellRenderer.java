/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import ui.AppTheme;
import ui.controls.TextView;

/**
 *
 * @author PersianDevStudio
 */
public class CustomeDefaultTableCellRenderer implements TableCellRenderer {

    private final TextView tv;

    public CustomeDefaultTableCellRenderer() {
        tv = new TextView();
        tv.setOpaque(true);
        tv.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            tv.setText("");
        } else {
            tv.setText(String.valueOf(value));
        }
        if (isSelected) {
            tv.setBackground(AppTheme.COLOR_SILVER_DARKER);
        } else {
            tv.setBackground(row % 2 == 0 ? AppTheme.COLOR_WHITE : AppTheme.COLOR_LIGHT_CYAN);

        }
        return tv;
    }
}
