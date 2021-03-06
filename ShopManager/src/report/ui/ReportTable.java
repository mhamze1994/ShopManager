/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report.ui;

import invoice.ui.CustomeDefaultTableCellRenderer;
import invoice.ui.CustomeTableHeaderRenderer;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import report.Report;
import report.ReportItemPricing;

/**
 *
 * @author PersianDevStudio
 */
public class ReportTable extends javax.swing.JPanel {

    private Report report;
    private TableModelReport reportTableModel;
    private boolean notificationEnabled;

    /**
     * Creates new form ReportTable
     */
    public ReportTable() {
        initComponents();
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }

    public void setReport(Report report) {
        this.report = report;
        try {
            reportTableModel = new TableModelReport(report);
        } catch (SQLException ex) {
            Logger.getLogger(ReportTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTable.setModel(reportTableModel);

        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        jTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        jScrollPane1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        jTable.getTableHeader().setDefaultRenderer(new CustomeTableHeaderRenderer());
        jTable.setRowHeight(30);
        for (int i = 0; i < jTable.getColumnModel().getColumnCount(); i++) {
            jTable.getColumnModel().getColumn(i).setCellRenderer(new CustomeDefaultTableCellRenderer());
        }

    }

    public TableModelReport getReportTableModel() {
        return reportTableModel;
    }

    public JTable getjTable() {
        return jTable;
    }

    public int getSelectedRow() {
        return jTable.getSelectedRow();
    }

    public Object getValue(int row, int col) {
        return jTable.getValueAt(row, col);
    }

    public void addMouseAdapter(MouseAdapter adapter) {
        jTable.addMouseListener(adapter);
    }

    public void setColumnPreferredWidth(int index, int width) {
        jTable.getColumnModel().getColumn(index).setPreferredWidth(width);
    }

    public void setColumnMaxWidth(int index, int width) {
        jTable.getColumnModel().getColumn(index).setMaxWidth(width);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables

    public void setReverse(boolean reverse) {
        reportTableModel.setIsReverse(reverse);
    }

    public void apply() {
        try {
            report.report();
            reportTableModel.refresh();
            jTable.revalidate();
            jTable.repaint();

            if (reportTableModel.getRowCount() == 0) {
                if (notificationEnabled) {
                    JOptionPane.showMessageDialog(this, "هیچ اطلاعاتی یافت نشد.", "پیام", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "خطا", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(ReportTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAutoResizeMode(int mode) {
        jTable.setAutoResizeMode(mode);
    }

    public void setNotificationEnabled(boolean enabled) {
        this.notificationEnabled = enabled;
    }

}
