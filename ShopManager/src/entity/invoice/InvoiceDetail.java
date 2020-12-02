/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.invoice;

import entity.Item;
import application.Calculator;
import application.DatabaseManager;
import application.JalaliCalendar;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author PersianDevStudio
 */
public class InvoiceDetail {

    public static ArrayList<InvoiceDetail> getLatestTrades(long contactId, long itemId, int refundType) throws SQLException {
        ArrayList<InvoiceDetail> latestDetails = new ArrayList<>();

        CallableStatement pc = DatabaseManager.instance.prepareCall("CALL search_latest_contact_item_details(? , ? ,?)");
        DatabaseManager.SetLong(pc, 1, contactId);
        DatabaseManager.SetLong(pc, 2, itemId);
        DatabaseManager.SetInt(pc, 3, refundType);
        ResultSet rs = pc.executeQuery();
        while (rs.next()) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.readResultSet(rs);
            latestDetails.add(detail);
        }

        return latestDetails;
    }

// <editor-fold defaultstate="collapsed" desc="Database columns of invoicedetail table">   
//`detailId`, `date`, `contactId`, `operationType`, `itemId`, `suAmount`, `suPrice`, `refDetailId`, invoiceid
    private long detailId;
    private long date;
    private long contactId;
    private int operationType;
    private long itemId;
    private BigDecimal amount;
    private int unitId;
    private BigDecimal unitPrice;
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
        suAmount = Calculator.mul(getAmount(), getRatio());
        if (Invoice.isExporting(operationType)) {
            return suAmount.negate();
        }
        return suAmount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        fireUpdate();
    }

//    public void setSuAmount(BigDecimal suAmount) {
//        this.suAmount = suAmount;
//        fireUpdate();
//    }
    public BigDecimal getSuPrice() {
        suPrice = Calculator.divAndRem(getUnitPrice(), getRatio())[0];
        if (Invoice.isRefund(operationType)) {
            return suPrice.negate();
        }
        return suPrice;
    }

//    public void setSuPrice(BigDecimal suPrice) {
//        this.suPrice = suPrice;
//        fireUpdate();
//    }
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
        return Calculator.mul(getAmount(), getUnitPrice());
//        return Calculator.mul(getSuPrice(), getSuAmount());
    }

    public Item getItem() {
        if (item == null) {
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
        amount = rs.getBigDecimal(pIndex++).stripTrailingZeros();
        unitId = rs.getInt(pIndex++);

        BigDecimal ratio = rs.getBigDecimal(pIndex++).stripTrailingZeros();

        unitPrice = rs.getBigDecimal(pIndex++).stripTrailingZeros();

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

    public BigDecimal getAmount() {
        return amount;
    }

    public int getUnitId() {
        return unitId;
    }

    public BigDecimal getRatio() {
        //Unit 1 is the bigger
        if (unitId == getItem().getUnit1()) {
            if (getItem().getUnit2() == 0) {
                //If there is no unit2 return one
                return BigDecimal.ONE;
            }
            //ratio is valid only when there is a defined unit2
            return getItem().getRatio1();
        } else {
            return BigDecimal.ONE;
        }
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        fireUpdate();
    }

    public void setUnit(int unitId) {
        this.unitId = unitId;
    }
}
