/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import application.DatabaseManager;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Item extends AbstractEntity {

    public static ArrayList<AnbarGardaniEntity> loadAnbarGardani() {
        ArrayList<AnbarGardaniEntity> all = new ArrayList<>();
        try {

            try (CallableStatement call = DatabaseManager.instance.prepareCall("call load_anbar_gardani()");
                    ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    all.add(new AnbarGardaniEntity(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getBigDecimal(3),
                            rs.getBigDecimal(4),
                            rs.getBigDecimal(5)));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    //columns of item table
    private long itemId;
    private long categoryId;
    private String description;
    private int unit1;
    private BigDecimal ratio1;
    private int unit2;
    //****

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnit1() {
        return unit1;
    }

    public void setUnit1(int unit1) {
        this.unit1 = unit1;
    }

    public BigDecimal getRatio1() {
        return ratio1;
    }

    public void setRatio1(BigDecimal ratio1) {
        this.ratio1 = ratio1;
    }

    public int getUnit2() {
        return unit2;
    }

    public void setUnit2(int unit2) {
        this.unit2 = unit2;
    }

    @Override
    public void readResultSet(ResultSet rs) throws SQLException {
        int pIndex = 1;
        setItemId(rs.getLong(pIndex++));
        setCategoryId(rs.getLong(pIndex++));
        setDescription(rs.getString(pIndex++));
        setUnit1(rs.getInt(pIndex++));
        setRatio1(rs.getBigDecimal(pIndex++));
        setUnit2(rs.getInt(pIndex++));

    }

    private BigDecimal lastKnownStock;

    public BigDecimal fetchItemStock() throws SQLException {
        CallableStatement call = DatabaseManager.instance.prepareCall("CALL get_item_stock(? , ?);");
        DatabaseManager.SetLong(call, 1, getItemId());
        call.registerOutParameter(2, Types.DECIMAL);
        call.execute();
        return lastKnownStock = call.getBigDecimal(2);
    }

    public BigDecimal getLastKnownStock() {
        return lastKnownStock;
    }

    @Override
    public String toString() {
        return description;
    }

    public static Item find(long itemId) {
        Item item = null;
        try {
            CallableStatement call = DatabaseManager.instance.prepareCall("CALL search_item_id(?)");
            DatabaseManager.SetLong(call, 1, itemId);
            ResultSet rs = call.executeQuery();
            if (rs.next()) {
                item = new Item();
                item.readResultSet(rs);
            }
            rs.close();
            call.close();
        } catch (SQLException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
        }
        return item;
    }

}
