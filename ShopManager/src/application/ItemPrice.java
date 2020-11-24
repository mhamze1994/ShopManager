/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author PersianDevStudio
 */
public class ItemPrice {

    public static ArrayList<ItemPrice> listPrices(long itemId) throws SQLException {
        ArrayList<ItemPrice> itemPrices = new ArrayList<>();
        String sql = "select * from item_price ip where item_id = ? order by `date` desc";
        try (PreparedStatement ps = DatabaseManager.instance.PrepareStatement(sql, Statement.NO_GENERATED_KEYS)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemPrice ip = new ItemPrice();
                    ip.readResultSet(rs);
                    if (itemPrices.contains(ip) == false) {
                        itemPrices.add(ip);
                    }
                }
            }
        }
        return itemPrices;
    }

    private long id;
    private int priceType;
    private long date;
    private long itemId;
    private BigDecimal price;

    public void readResultSet(ResultSet rs) throws SQLException {
        int pIndex = 1;
        id = rs.getLong(pIndex++);
        priceType = rs.getInt(pIndex++);
        date = rs.getLong(pIndex++);
        itemId = rs.getLong(pIndex++);
        price = rs.getBigDecimal(pIndex++);
    }

    public String description() {
        return "سطح " + priceType + " : " + toString();
    }

    @Override
    public String toString() {
        return price.stripTrailingZeros().toPlainString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemPrice other = (ItemPrice) obj;
        if (this.priceType != other.priceType) {
            return false;
        }
        return true;
    }

}
