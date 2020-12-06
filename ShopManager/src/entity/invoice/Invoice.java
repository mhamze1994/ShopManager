/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.invoice;

import entity.Payment;
import application.Calculator;
import application.JalaliCalendar;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Invoice {

    public static final int TYPE_BUY = 10;
    public static final int TYPE_REFUND_BUY = 11;
    public static final int TYPE_SELL = 20;
    public static final int TYPE_REFUND_SELL = 21;

    public static final int TYPE_INIT_STOCK = 40;

    public static final int TYPE_ENTER_STOCK = 50;

    public static final int TYPE_EXIT_STOCK = 60;

    public static final int TYPE_OTHER = 70;

    public static String getTypeTitle(int aInt) {
        switch (aInt) {
            case TYPE_BUY:
                return "خرید";
            case TYPE_REFUND_BUY:
                return "برگشت از خرید";
            case TYPE_SELL:
                return "فروش";
            case TYPE_REFUND_SELL:
                return "برگشت از فروش";
            case TYPE_INIT_STOCK:
                return "موجودی اولیه";
            case TYPE_ENTER_STOCK:
                return "رسید";
            case TYPE_EXIT_STOCK:
                return "حواله";
            case TYPE_OTHER:
                return "سند آزاد";
        }
        return "???";
    }

    //invoiceid, invoicedate, contact, operationtype, `totalCost`
    private long invoiceId;
    private long date;
    private long contact;
    private int operationType;
    private BigDecimal totalCost;

    private ArrayList<InvoiceDetail> detailList = new ArrayList<>();

    private ArrayList<InvoiceDetail> detailListCopy = new ArrayList<>();

    /**
     * The fixed payment that every invoice will have. This is always about
     * stores and contacts
     */
    private Payment storePayment = new Payment();

    private ArrayList<Payment> contactPayment = new ArrayList<>();

    private InvoiceUpdateListener invoiceUpdateListener;

    public Invoice() {

    }

    public void setInvoiceUpdateListener(InvoiceUpdateListener invoiceUpdateListener) {
        this.invoiceUpdateListener = invoiceUpdateListener;
        for (Payment payment : contactPayment) {
            payment.setInvoiceUpdateListener(invoiceUpdateListener);
        }
        for (InvoiceDetail invoiceDetail : detailList) {
            invoiceDetail.setInvoiceUpdateListener(invoiceUpdateListener);
        }
    }

    public Invoice(int operationType) {
        this.operationType = operationType;
        setDate(JalaliCalendar.now());
    }

    public void add(InvoiceDetail detail) {
        updateDetail(detail);
        detailList.add(detail);

        fireInvoiceUpdated();
    }

    private void fireInvoiceUpdated() {
        if (invoiceUpdateListener != null) {
            invoiceUpdateListener.dataUpdate();
        }
    }

    public InvoiceDetail get(int index) {
        return detailList.get(index);
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
        updateAllDetails();
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
        updateAllDetails();
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
        updateAllDetails();
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
        updateAllDetails();
    }

    /**
     * For refund operations this total cost is negative.
     *
     * @return
     */
    public BigDecimal getTotalCost() {
        if (isRefund(operationType)) {
            return totalCost.negate();
        }
        return totalCost;
    }

    public BigDecimal displayTotalContactPayment() {
        BigDecimal tp = BigDecimal.ZERO;
        for (Payment payment : contactPayment) {
            tp = tp.add(payment.getCreditor()).add(payment.getDeptor());
        }
        return tp;
    }

    public void updateTotalCost() {
        this.totalCost = BigDecimal.ZERO;
        for (InvoiceDetail invoiceDetail : detailList) {
            totalCost = Calculator.add(totalCost, invoiceDetail.getTotalCost());
        }

        //when invoice is importing items in stores (buy or sell-refund)
        //The contact will become creditor. when items are removed from stores
        //(sell or buy-refund) the contact is receiving the items hence becoming deptor
        boolean importing = Invoice.isImporting(getOperationType());

        if (storePayment != null) {
            storePayment.setCreditor(importing ? totalCost : BigDecimal.ZERO);
            storePayment.setDeptor(importing ? BigDecimal.ZERO : totalCost);
        }

    }

    private void updateDetail(InvoiceDetail detail) {

        detail.setDate(date);
        detail.setContactId(contact);
        detail.setOperationType(operationType);
        detail.setInvoiceId(invoiceId);

        detail.setInvoiceUpdateListener(invoiceUpdateListener);
    }

    private void updateAllDetails() {
        if (storePayment != null) {
            storePayment.setContact(contact);
            storePayment.setObjectId(1);
            storePayment.setObjectType(Payment.STORE);
        }

        for (InvoiceDetail invoiceDetail : detailList) {
            updateDetail(invoiceDetail);
        }
    }

    public ArrayList<InvoiceDetail> getDetails() {
        return detailList;
    }

    public void addPayment(Payment payment) {
        contactPayment.add(payment);
        payment.setInvoiceUpdateListener(invoiceUpdateListener);
        fireInvoiceUpdated();
    }

    public void setStorePayment(Payment payment) {
        storePayment = payment;
    }

    public ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> all = new ArrayList<>();

        if (storePayment != null) {
            all.add(storePayment);
        }

        all.addAll(contactPayment);

        return all;
    }

    public ArrayList<Payment> getContactPayments() {
        return contactPayment;
    }

    public static boolean isRefund(int type) {
        return type == Invoice.TYPE_REFUND_BUY || type == Invoice.TYPE_REFUND_SELL;
    }

    public static boolean isImporting(int type) {
        return type == Invoice.TYPE_BUY || type == Invoice.TYPE_REFUND_SELL;
    }

    public static boolean isExporting(int type) {
        return type == Invoice.TYPE_SELL || type == Invoice.TYPE_REFUND_BUY;
    }

    public static boolean inoutBuy(int type) {
        return type == Invoice.TYPE_BUY || type == Invoice.TYPE_REFUND_BUY;
    }

    public static boolean inoutSell(int type) {
        return type == Invoice.TYPE_SELL || type == Invoice.TYPE_REFUND_SELL;
    }

    public void removePayment(Payment payment) {
        System.out.println(contactPayment.remove(payment));
        fireInvoiceUpdated();
    }

    public void readResultSet(ResultSet rs) throws SQLException {
        int pIndex = 1;
        invoiceId = rs.getLong(pIndex++);
        date = rs.getLong(pIndex++);
        contact = rs.getLong(pIndex++);
        operationType = rs.getInt(pIndex++);
        totalCost = rs.getBigDecimal(pIndex++);
    }

    public void remove(int selectedRow) {
        detailList.remove(selectedRow);
        fireInvoiceUpdated();
    }

    public void remove(InvoiceDetail selectedDetail) {
        detailList.remove(selectedDetail);
        fireInvoiceUpdated();
    }

    public void keepDetailsCopy() {
        detailListCopy.clear();
        for (InvoiceDetail invoiceDetail : detailList) {
            detailListCopy.add(invoiceDetail);
        }
    }

    public boolean stockCheckOk() {
        try {
            boolean isValid = true;
            for (InvoiceDetail invoiceDetail : detailList) {
                if (invoiceDetail.getItem().fetchItemStock().compareTo(BigDecimal.ZERO) == -1) {
                    isValid = false;
                }
            }
            for (InvoiceDetail invoiceDetail : detailListCopy) {
                if (invoiceDetail.getItem().fetchItemStock().compareTo(BigDecimal.ZERO) == -1) {
                    isValid = false;
                }
            }
            return isValid;
        } catch (SQLException ex) {
            Logger.getLogger(Invoice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
