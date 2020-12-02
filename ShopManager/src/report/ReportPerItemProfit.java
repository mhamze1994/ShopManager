/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author PersianDevStudio
 */
public class ReportPerItemProfit extends Report {

    private long dateStart;
    private long dateEnd;
    private long itemId;

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
                return rs.getLong(index) + "";
            case 2:
                return rs.getString(index);
            case 3:
            case 4:
            case 5:
                return rs.getBigDecimal(index).setScale(0, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString();
            case 6:
                return rs.getBigDecimal(3).multiply(rs.getBigDecimal(4).subtract(rs.getBigDecimal(5)))
                        .setScale(0, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString();

        }

        return "";
    }

    @Override
    public int getColumnCount() {
        return headers().length;
    }

    @Override
    public void report() throws SQLException {
        prepareCallStatement("call report_per_item_profit(?,?,?)");
        setLong(itemId, 1);
        setLong(dateStart, 2);
        setLong(dateEnd, 3);
        execute();
    }

    @Override
    public String[] headers() {
        return new String[]{"شناسه کالا", "شرح کالا", "جمع فروش", "میانگین فروش", "میانگین خرید", "سود"};
    }

}
