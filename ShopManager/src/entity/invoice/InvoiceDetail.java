/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.invoice;

import entity.Item;
import application.Calculator;
import application.JalaliCalendar;
import entity.AbstractEntity;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author PersianDevStudio
 */
public class InvoiceDetail {

    // <editor-fold defaultstate="collapsed" desc="Database columns of invoicedetail table">   
    //`detailId`, `date`, `contactId`, `operationType`, `itemId`, `suAmount`, `suPrice`, `refDetailId`, invoiceid
    private long detailId;
    private long date;
    private long contactId;
    private int operationType;
    private long itemId;
    private BigDecimal suAmount;
    private BigDecimal suPrice;
    private long refDetailId;
    private long invoiceId;
    //</editor-fold>

    private InvoiceUpdateListener invoiceUpdateListener;

    private Item item;

    public void setInvoiceUpdateListener(InvoiceUpdateListener invoiceUpdateListener) {
        this.invoiceUpdateListener = invoiceUpdateListener;
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public long getItemId() {
        return itemId;
    }

//    public void setItemId(long itemId) {
//        this.itemId = itemId;
//    }
    public BigDecimal getSuAmount() {
        if (Invoice.isExporting(operationType)) {
            return suAmount.negate();
        }
        return suAmount;
    }

    public void setSuAmount(BigDecimal suAmount) {
        this.suAmount = suAmount;
        fireUpdate();
    }

    public BigDecimal getSuPrice() {
        if (Invoice.isRefund(operationType)) {
            return suPrice.negate();
        }
        return suPrice;
    }

    public void setSuPrice(BigDecimal suPrice) {
        this.suPrice = suPrice;
        fireUpdate();
    }

    public long getRefDetailId() {
        return refDetailId;
    }

    public void setRefDetailId(long refDetailId) {
        this.refDetailId = refDetailId;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getTotalCost() {
        return Calculator.mul(suPrice, suAmount);
    }

    public Item getItem() {
        if (item == null){
            item = Item.find(itemId);
        }
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        itemId = item == null ? 0 : item.getItemId();
    }

    public void readResultSet(ResultSet rs) throws SQLException {
        int pIndex = 1;
        detailId = rs.getLong(pIndex++);
        date = rs.getLong(pIndex++);
        contactId = rs.getLong(pIndex++);
        operationType = rs.getInt(pIndex++);
        itemId = rs.getLong(pIndex++);
        suAmount = rs.getBigDecimal(pIndex++).stripTrailingZeros();
        suPrice = rs.getBigDecimal(pIndex++).stripTrailingZeros();
        refDetailId = rs.getLong(pIndex++);
        invoiceId = rs.getLong(pIndex++);
    }

    public String getSummary() {
        return "فاکتور @1    تاریخ : @4   مقدار : @2     قیمت : @3"
                .replace("@1", invoiceId + "")
                .replace("@2", suAmount.stripTrailingZeros().toPlainString())
                .replace("@3", suPrice.stripTrailingZeros().toPlainString())
                .replace("@4", JalaliCalendar.format(date));
    }

    private void fireUpdate() {
        if (invoiceUpdateListener != null) {
            invoiceUpdateListener.dataUpdate();
        }
    }
}
