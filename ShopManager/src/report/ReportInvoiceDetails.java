/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author PersianDevStudio
 */
public class ReportInvoiceDetails extends Report {

    @Override
    public String read(ResultSet rs, int index) throws SQLException {
        return "";
    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

    @Override
    public void report() throws SQLException {

    }

    @Override
    public String[] headers() {
        return new String[]{"کد کالا", "شرح کالا", "مقدار", "فی", "جمع"};
    }

}
