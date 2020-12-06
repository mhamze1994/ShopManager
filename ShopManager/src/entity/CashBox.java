/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import application.DatabaseManager;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class CashBox extends AbstractEntity {

    public long cashboxId;
    public String title;

    private static final int OP_INSERT = 1;
    private static final int OP_UPDATE = 2;
    private static final int OP_DELETE = 3;

    @Override
    public void readResultSet(ResultSet rs) throws SQLException {
        setCashboxId(rs.getLong(1));
        setTitle(rs.getString(2));
    }

    public static ArrayList<CashBox> listAll() {
        ArrayList<CashBox> all = new ArrayList<>();
        try {
            CallableStatement call = DatabaseManager.instance.prepareCall("call select_cashbox(?)");
            DatabaseManager.SetLong(call, 1, 0);
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    CashBox cashBox = new CashBox();
                    cashBox.readResultSet(rs);
                    all.add(cashBox);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CashBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    public void insert() throws SQLException {
        doOperation(OP_INSERT);
    }

    public void update() throws SQLException {
        doOperation(OP_UPDATE);
    }

    public void delete() throws SQLException {
        doOperation(OP_DELETE);
    }

    private void doOperation(int operation) throws SQLException {
        try (CallableStatement call = DatabaseManager.instance.prepareCall("call ops_cashbox(? , ? , ?)")) {

            call.registerOutParameter(1, Types.BIGINT);

            DatabaseManager.SetLong(call, 1, cashboxId, false);

            DatabaseManager.SetString(call, 2, title);
            DatabaseManager.SetInt(call, 3, operation);

            call.execute();

            cashboxId = call.getLong(1);
        }
    }

    public long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getBalance() throws SQLException {
        CallableStatement call = DatabaseManager.instance.prepareCall("call get_balance(?,?,?);");
        DatabaseManager.SetLong(call, 1, Payment.CASH_BOX);
        DatabaseManager.SetLong(call, 2, cashboxId);
        call.registerOutParameter(3, Types.DECIMAL);
        call.execute();

        return call.getBigDecimal(3);
    }

    @Override
    public String toString() {
        return title ;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (this.cashboxId ^ (this.cashboxId >>> 32));
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
        final CashBox other = (CashBox) obj;
        if (this.cashboxId != other.cashboxId) {
            return false;
        }
        return true;
    }

    
    
}
