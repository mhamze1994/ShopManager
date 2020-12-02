/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.math.BigDecimal;
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
        BigDecimal r = rs.getBigDecimal(4);
        switch (index) {
            case 1:
                return rs.getString(index);
            case 2:
                return rs.getBigDecimal(3).stripTrailingZeros().toPlainString();
            case 3:
                return rs.getBigDecimal(2).stripTrailingZeros().toPlainString();
            case 4:
                return r.compareTo(BigDecimal.ZERO) == -1 ? r.abs().stripTrailingZeros().toPlainString() : "";
            case 5:
                return r.compareTo(BigDecimal.ZERO) == +1 ? r.abs().stripTrailingZeros().toPlainString() : "";

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
        return new String[]{"مشخصات", "بدهکار" , "بستانکار","مانده بدهکار", "مانده بستانکار"};
    }

}
