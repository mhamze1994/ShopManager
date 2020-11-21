/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import application.JalaliCalendar;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author PersianDevStudio
 */
public class ReportPaymentBill extends Report {

    private long contactId;
    private long startDate;
    private long endDate;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    @Override
    public String read(ResultSet rs, int index) throws SQLException {
        switch (index) {
            case 1:
                int t = rs.getInt(1);
                return t == 1 ? "فاکتور" : "سند";
            case 2:
                return rs.getLong(index) + "";
            case 3:
                return JalaliCalendar.format(rs.getLong(index));
            case 4:
                return rs.getString(index);
            case 5:
                return rs.getBigDecimal(index).stripTrailingZeros().toPlainString();
            case 6:
                return rs.getBigDecimal(index).stripTrailingZeros().toPlainString();
            case 7:
                int type = rs.getInt(1);
                if (type == 1) {
                    return rs.getBigDecimal(index).stripTrailingZeros().toPlainString();
                } else {
                    return "-";
                }
        }
        return "????";
    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

    @Override
    public void report() throws SQLException {
        prepareCallStatement("CALL report_payment_bill(? , ? , ?)");
        setLong(contactId, 1);
        setLong(startDate, 2);
        setLong(endDate, 3);

        execute();
    }

    @Override
    public String[] headers() {
        return new String[]{"نوع", "شماره", "اخرین پرداخت", "فروشنده/خریدار", "بستانکار", "بدهکار", "مانده"};
    }

}
