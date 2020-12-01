/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.invoice.Invoice;
import entity.invoice.InvoiceUpdateListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author PersianDevStudio
 */
public class Payment {

    public static final int STORE = 10;
    public static final int BANK_ACCOUNT = 20;
    public static final int CASH_BOX = 30;
    public static final int CHEQUE = 40;

    public static final long OBJECT_ID_CHEQUE_MANAGER = 1000;

    private long contact;
    private BigDecimal creditor = BigDecimal.ZERO;
    private BigDecimal deptor = BigDecimal.ZERO;
    private int objectType;
    private long objectId;

    private HashMap<String, Object> extraInfo = new HashMap<>();

    private InvoiceUpdateListener updateListener;

    public void putExtra(String key, Object value) {
        extraInfo.put(key.toLowerCase(), value);
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public BigDecimal getCreditor() {
        return creditor;
    }

    public void setCreditor(BigDecimal creditor) {
        this.creditor = creditor;
        fireUpdate();
    }

    public BigDecimal getDeptor() {
        return deptor;
    }

    public void setDeptor(BigDecimal deptor) {
        this.deptor = deptor;
        fireUpdate();
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        
        this.objectType = objectType;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public boolean isValid() {
        if (deptor.equals(BigDecimal.ZERO)) {
            return creditor.equals(BigDecimal.ZERO) == false && creditor.compareTo(BigDecimal.ZERO) == 1;
        }
        if (creditor.equals(BigDecimal.ZERO)) {
            return deptor.equals(BigDecimal.ZERO) == false && deptor.compareTo(BigDecimal.ZERO) == 1;
        }
        return false;
    }

    public void setInvoiceUpdateListener(InvoiceUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private void fireUpdate() {
        if (updateListener != null) {
            updateListener.dataUpdate();
        }
    }

    public void readResultSetAsCheque(Invoice invoice, ResultSet rsPayments) throws SQLException {
        int pIndex = 1;

        contact = invoice.getContact();
        objectId = 1000;
        objectType = Payment.CHEQUE;

        //Skip cheque id        
        rsPayments.getLong(pIndex++);

        putExtra("bankId", rsPayments.getLong(pIndex++));

        BigDecimal value = rsPayments.getBigDecimal(pIndex++);

        int type = rsPayments.getInt(pIndex++);

        putExtra("serial", rsPayments.getLong(pIndex++));

        //Skip invoice id
        rsPayments.getLong(pIndex++);

        putExtra("date", rsPayments.getLong(pIndex++));

        if (type == 1) {
            creditor = value;
            deptor = BigDecimal.ZERO;
        } else {
            creditor = BigDecimal.ZERO;
            deptor = value;
        }

    }

    public void readResultSet(ResultSet rsPayments) throws SQLException {
        int pIndex = 1;

        //TODO skip refrence id and type
        rsPayments.getInt(pIndex++);
        rsPayments.getLong(pIndex++);

        contact = rsPayments.getLong(pIndex++);
        creditor = rsPayments.getBigDecimal(pIndex++).stripTrailingZeros();
        deptor = rsPayments.getBigDecimal(pIndex++).stripTrailingZeros();
        objectId = rsPayments.getLong(pIndex++);
        objectType = rsPayments.getInt(pIndex++);

        //TODO skip date
        rsPayments.getLong(pIndex++);

    }

    public long getExtraLong(String key) {
        return Long.parseLong(extraInfo.get(key.toLowerCase()) + "");
    }

    public int getExtraInt(String key) {
        return Integer.parseInt(extraInfo.get(key.toLowerCase()) + "");
    }

}
