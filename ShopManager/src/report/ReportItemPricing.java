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
public class ReportItemPricing extends Report {

    private long itemId;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    @Override
    public String read(ResultSet rs, int index) throws SQLException {
        switch (index) {
            case 1:
                return typeText(rs.getInt(2));
            case 2:
                return JalaliCalendar.format(rs.getLong(3));
            case 3:
                return rs.getBigDecimal(5).stripTrailingZeros().toPlainString();
        }
        return "???";
    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

    @Override
    public void report() throws SQLException {
        prepareCallStatement("CALL report_item_price(?)");
        setLong(itemId, 1);
        execute();
    }

    @Override
    public String[] headers() {
        return new String[]{"نوع", "تاریخ", "قیمت"};
    }

    private String typeText(int id) {
        return "سطح " + id;
    }

}
