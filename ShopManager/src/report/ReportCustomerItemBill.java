/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import application.JalaliCalendar;
import entity.invoice.Invoice;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author PersianDevStudio
 */
public class ReportCustomerItemBill extends Report {

    private int operationType;

    private long contactId;

    private long itemId;

    private long dateStart = 0;

    private long dateEnd = Long.MAX_VALUE;

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public long getCustomerId() {
        return contactId;
    }

    public void setCustomerId(long customerId) {
        this.contactId = customerId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String[] headers() {
        // `invoiceid`, date , `contactId` , `operationType` , `itemId` , suAmount , suPrice,  totalPrice
        return new String[]{"فاکتور", "تاریخ", "شناسه مخاطب", "نام مخاطب", "نوع", "شناسه کالا", "شرح کالا", "مقدار", "واحد", "فی", "مبلغ کل"};
    }

    @Override
    public String read(ResultSet rs, int index) throws SQLException {
        switch (index) {
            case 1:
                return String.valueOf(rs.getLong(index));
            case 2:
                return JalaliCalendar.format(rs.getLong(index));
            case 3:
                return String.valueOf(rs.getLong(index));
            case 4:
                return rs.getString(index);
            case 5:
                return Invoice.getTypeTitle(rs.getInt(index));
            case 6:
                return String.valueOf(rs.getLong(index));
            case 7:
                return rs.getString(index);
            case 8:
                return rs.getBigDecimal(index).abs().stripTrailingZeros().toPlainString();
            case 9:
                return rs.getString(index);
            case 10:
                return rs.getBigDecimal(index).abs().stripTrailingZeros().toPlainString();
            case 11:
                return rs.getBigDecimal(index).abs().stripTrailingZeros().toPlainString();
        }

        return "???";
    }

    @Override
    public void report() throws SQLException {
//      in in_date_start bigint,
//	in in_date_end bigint,
//	in in_contact_id bigint,
//	in in_item_id bigint,
//	in in_operation int
        prepareCallStatement("CALL report_customer_item_bill(?,?,?,?,?)");
        setLong(dateStart, 1);
        setLong(dateEnd, 2);
        setLong(contactId, 3);
        setLong(itemId, 4);
        setInt(operationType, 5);
        execute();

    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

}
