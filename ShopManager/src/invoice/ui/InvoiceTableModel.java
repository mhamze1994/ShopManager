/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import entity.invoice.InvoiceDetail;
import entity.invoice.Invoice;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PersianDevStudio
 */
public class InvoiceTableModel extends DefaultTableModel {

    private static int sufixColumnCount = 1;
    private static int prefixColumnCount = 2;

    private ArrayList<String> columns;
    private ValueFinder valueFinder;

    private final Invoice invoice;

    public InvoiceTableModel(Invoice invoice, ArrayList<String> columns, ValueFinder valueFinder) {
        this.columns = columns;
        this.invoice = invoice;
        this.valueFinder = valueFinder;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return "";
        }
        if (column == 1) {
            return row + 1;
        }
        return valueFinder.find(invoice.get(row), column - prefixColumnCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0){
            return true;
        }
        return false;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        }
        if (column == 1) {
            return "ردیف";
        }
        if (column == getColumnCount() - 1) {
            return "";
        }
        return columns.get(column - prefixColumnCount);
    }

    @Override
    public int getColumnCount() {
        return columns.size() + prefixColumnCount + sufixColumnCount;
    }

    @Override
    public int getRowCount() {
        if (invoice == null) {
            return 0;
        }
        return invoice.getDetails().size();
    }

    public interface ValueFinder {

        public Object find(InvoiceDetail detail, int column);
    }

}
