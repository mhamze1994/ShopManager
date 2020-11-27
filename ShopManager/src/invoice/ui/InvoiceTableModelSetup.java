/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import entity.Unit;
import entity.invoice.Invoice;
import entity.invoice.InvoiceDetail;
import java.util.ArrayList;
import javax.swing.JTable;

/**
 *
 * @author PersianDevStudio
 */
public class InvoiceTableModelSetup {

    public static void setupTableNormal(Invoice invoice, JTable jTable, InvoiceEditor invoiceEditor) {

        jTable.setModel(new InvoiceTableModel(invoice, getColumns(),
                (InvoiceDetail detail, int column) -> {
                    switch (column) {
                        case 0:
                            return detail.getItemId();
                        case 1:
                            return detail.getItem().getDescription();
                        case 2:
                            return detail.getAmount().abs().toPlainString();
                        case 3:
                            return Unit.toString(detail.getUnitId());
                        case 4:
                            return detail.getUnitPrice().abs().toPlainString();
                        case 5:
                            return detail.getSuPrice().abs().toPlainString();
                        case 6:
                            return detail.getTotalCost().abs().toPlainString();
                        default:
                            return null;
                    }
                }));

        applyCommonSettings(jTable, invoiceEditor);
    }

    private static ArrayList<String> getColumns() {
        ArrayList<String> columns = new ArrayList();

        columns.add("کد کالا");
        columns.add("شرح کالا");
        columns.add("مقدار");
        columns.add("واحد");
        columns.add("قیمت واحد");
        columns.add("قیمت جزء");
        columns.add("مبلغ کل");
        return columns;
    }

    private static void applyCommonSettings(JTable jTable, InvoiceEditor invoiceEditor) {
        jTable.getTableHeader().setReorderingAllowed(false);

        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        jTable.setRowHeight(30);

        int columnIndex = 0;
        jTable.getColumnModel().getColumn(columnIndex).setMinWidth(30);
        jTable.getColumnModel().getColumn(columnIndex).setMaxWidth(30);
        columnIndex++;
        jTable.getColumnModel().getColumn(columnIndex).setMinWidth(40);
        jTable.getColumnModel().getColumn(columnIndex).setMaxWidth(40);
        columnIndex++;

        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(80);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(200);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(80);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(60);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(100);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(100);
        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(100);

        jTable.getColumnModel().getColumn(columnIndex++).setMinWidth(300);

        jTable.getTableHeader().setDefaultRenderer(new CustomeTableHeaderRenderer());
        for (int i = 0; i < jTable.getColumnModel().getColumnCount(); i++) {
            jTable.getColumnModel().getColumn(i).setCellRenderer(new CustomeDefaultTableCellRenderer());
        }

    }


}
