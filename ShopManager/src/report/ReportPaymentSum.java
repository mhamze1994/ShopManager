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
public class ReportPaymentSum extends Report {
    
    private long contactId;

    @Override
    public String read(ResultSet rs, int index) throws SQLException {
        switch (index) {
            case 1:
                return rs.getString(index);
            case 2:
            case 3:
            case 4:
                return rs.getBigDecimal(index).stripTrailingZeros().toPlainString();
        }
        return "???";
    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    
    @Override
    public void report() throws SQLException {
        prepareCallStatement("call report_payment_sum(?);");
        setLong(contactId, 1);
        execute();
    }

    @Override
    public String[] headers() {
        return new String[]{"مشخصات", "بستانکار", "بدهکار", "مانده"};
    }

}
