/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import application.JalaliCalendar;
import entity.invoice.Invoice;
import java.math.BigDecimal;
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
                String str = t == 1 ? "فاکتور" : "سند";
                int operationType = rs.getInt(2);
                switch (operationType) {
                    case Invoice.TYPE_BUY:
                        str += " خرید";
                        break;
                    case Invoice.TYPE_REFUND_BUY:
                        str += " برگشت خرید";
                        break;
                    case Invoice.TYPE_SELL:
                        str += " فروش";
                        break;
                    case Invoice.TYPE_REFUND_SELL:
                        str += " برگشت فروش";
                        break;
                }
                return str;
            case 2:
                return rs.getLong(3) + "";
            case 3:
                return JalaliCalendar.format(rs.getLong(4));
            case 4:
                return rs.getString(5);
            case 5:
                return rs.getBigDecimal(7).stripTrailingZeros().toPlainString();
            case 6:
                return rs.getBigDecimal(6).stripTrailingZeros().toPlainString();
            case 7:
                int type = rs.getInt(1);
                // type 1 means its an invoice. type 2 means its a free document
                if (type == 1 && rs.getBigDecimal(8).compareTo(BigDecimal.ZERO) == -1) {
                    return rs.getBigDecimal(8).abs().stripTrailingZeros().toPlainString();
                } else {
                    return "-";
                }
            case 8:
                int type2 = rs.getInt(1);
                // type 1 means its an invoice. type 2 means its a free document
                if (type2 == 1 && rs.getBigDecimal(8).compareTo(BigDecimal.ZERO) == 1) {
                    return rs.getBigDecimal(8).abs().stripTrailingZeros().toPlainString();
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
        return new String[]{"نوع", "شماره", "اخرین پرداخت", "فروشنده/خریدار", "بدهکار", "بستانکار", "مانده بدهکار", "مانده بستانکار"};
    }

}
