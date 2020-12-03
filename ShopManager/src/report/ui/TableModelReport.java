/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report.ui;

import application.CustomArrayList;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import report.Report;

/**
 *
 * @author PersianDevStudio
 */
public class TableModelReport extends DefaultTableModel {

    private Report report;

    private String[] headers;

    private final CustomArrayList<String[]> data;

    private boolean isReverse = false;

    public TableModelReport(Report report) throws SQLException {
        data = new CustomArrayList<>();

        this.report = report;
        this.headers = report.headers();
        headers = report.headers();

    }

    public boolean isIsReverse() {
        return isReverse;
    }

    public void setIsReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    
    public void refresh() throws SQLException {
        this.report.report();
        String[] row;
        data.clear();
        try {
            while ((row = this.report.next()) != null) {
                data.add(row);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return row + 1;
        }

        int indx = isReverse ? data.size() - row - 1 : row;

        return data.get(indx)[column - 1];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        if (headers == null) {
            return "-";
        }
        if (column == 0) {
            return "ردیف";
        }
        return headers[column - 1];
    }

    @Override
    public int getColumnCount() {
        if (report == null) {
            return 0;
        }
        return report.getColumnCount() + 1;
    }

    @Override
    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

}
