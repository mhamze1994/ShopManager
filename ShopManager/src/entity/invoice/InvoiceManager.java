package entity.invoice;

import entity.Payment;
import application.DatabaseManager;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvoiceManager {

    public static Invoice find(long invoiceId) {

        try {
            CallableStatement findInvoice = DatabaseManager.instance.prepareCall("CALL search_invoice(?)");
            DatabaseManager.SetLong(findInvoice, 1, invoiceId);

            Invoice invoice = null;
            ResultSet rsInvoice = findInvoice.executeQuery();

            if (rsInvoice.next()) {
                invoice = new Invoice();
                invoice.readResultSet(rsInvoice);
                findInvoice.getMoreResults();

                ResultSet rsDetail = findInvoice.getResultSet();
                while (rsDetail.next()) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.readResultSet(rsDetail);
                    invoice.add(detail);
                }
                invoice.keepDetailsCopy();

                findInvoice.getMoreResults();
                ResultSet rsPayments = findInvoice.getResultSet();
                while (rsPayments.next()) {
                    Payment p = new Payment();
                    p.readResultSet(rsPayments);
                    if (p.getObjectType() == Payment.STORE) {
                        invoice.setStorePayment(p);
                    } else {
                        invoice.addPayment(p);
                    }
                }

            }

            findInvoice.close();
            return invoice;
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean update(Invoice invoice) throws SQLException {

        DatabaseManager.instance.BeginTransaction();

        invoice.updateTotalCost();
        //Takes 5 intput
        CallableStatement invoiceUpdate = DatabaseManager.instance.prepareCall("CALL invoice_update(?,?,?,?,?)");
        DatabaseManager.SetLong(invoiceUpdate, 1, invoice.getInvoiceId());
        DatabaseManager.SetLong(invoiceUpdate, 2, invoice.getDate());
        DatabaseManager.SetLong(invoiceUpdate, 3, invoice.getContact());
        DatabaseManager.SetInt(invoiceUpdate, 4, invoice.getOperationType());
        DatabaseManager.SetBigDecimal(invoiceUpdate, 5, invoice.getTotalCost());
        invoiceUpdate.execute();
        invoiceUpdate.close();

        insertOtherData(invoice);

        if (invoice.stockCheckOk() == false) {
            DatabaseManager.instance.Rollback();
            return false;
        } else {
            DatabaseManager.instance.CommitTransaction();
            return true;
        }

    }

    public static void insert(Invoice invoice) throws SQLException {

        DatabaseManager.instance.BeginTransaction();

        invoice.updateTotalCost();
        //CALL shopmanager.invoice_insert(:in_date,:in_contact,:in_type,:in_total_cost,?)
        //Takes 4 intput and 1 output
        CallableStatement call = DatabaseManager.instance.prepareCall("CALL invoice_insert(?,?,?,?,?)");
        DatabaseManager.SetLong(call, 1, invoice.getDate());
        DatabaseManager.SetLong(call, 2, invoice.getContact());
        DatabaseManager.SetInt(call, 3, invoice.getOperationType());
        DatabaseManager.SetBigDecimal(call, 4, invoice.getTotalCost());
        call.registerOutParameter(5, Types.BIGINT);
        call.execute();
        call.close();

        invoice.setInvoiceId(call.getLong(5));//This will update invoice details inside

        insertOtherData(invoice);

        DatabaseManager.instance.CommitTransaction();
    }

    private static void insertOtherData(Invoice invoice) throws SQLException {
        for (InvoiceDetail detail : invoice.getDetails()) {
            //CALL shopmanager.invoicedetail_insert(:in_date,:in_contact,:in_operation,:in_item,:in_su_amount,:in_su_price,:in_ref_id,:in_invoice)
            //Takes 8 inputs
            CallableStatement detailInsert = DatabaseManager.instance.prepareCall("CALL invoicedetail_insert(?,?,?,?,?,?,?,?)");
            DatabaseManager.SetLong/*  */(detailInsert, 1, detail.getDate());
            DatabaseManager.SetLong/*  */(detailInsert, 2, detail.getContactId());
            DatabaseManager.SetInt/*   */(detailInsert, 3, detail.getOperationType());
            DatabaseManager.SetLong/*  */(detailInsert, 4, detail.getItemId());

            DatabaseManager.SetBigDecimal(detailInsert, 5, detail.getSuAmount());
            DatabaseManager.SetBigDecimal(detailInsert, 6, detail.getSuPrice());

            DatabaseManager.SetLong/*  */(detailInsert, 7, detail.getRefDetailId());
            DatabaseManager.SetLong/*  */(detailInsert, 8, detail.getInvoiceId());
            detailInsert.execute();
            detailInsert.close();
        }

        //shopmanager.payment_insert(:document,:invoice,:contact,:creditor,:debtor,:object_id,:object_type)
        for (Payment payment : invoice.getAllPayments()) {
            try (CallableStatement ps = DatabaseManager.instance.prepareCall("CALL payment_insert(1,?,?,?,?,?,?,?)")) {
                DatabaseManager.SetLong(ps, 1, invoice.getInvoiceId());
                DatabaseManager.SetLong(ps, 2, invoice.getContact());
                DatabaseManager.SetBigDecimal(ps, 3, payment.getCreditor());
                DatabaseManager.SetBigDecimal(ps, 4, payment.getDeptor());
                DatabaseManager.SetLong(ps, 5, payment.getObjectId());
                DatabaseManager.SetInt(ps, 6, payment.getObjectType());
                DatabaseManager.SetLong(ps, 7, invoice.getDate());
                ps.execute();
            }
        }
    }

}
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package invoice;
//
//import entity.DatabaseManager;
//import java.sql.CallableStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author PersianDevStudio
// */
//public class InvoiceManager {
//
//    public static Invoice find(long invoiceId, boolean fullLoad) {
//        Invoice invoice = new Invoice();
//
//        try {
//            try (Statement stmt = DatabaseManager.instance.createStatement();
//                    ResultSet rs = stmt.executeQuery("select * from invoice where invoiceid = " + invoiceId)) {
//                if (rs.next()) {
//                    invoice.setInvoiceId(rs.getLong("invoiceid"));
//                    invoice.setInvoiceDate(rs.getLong("invoicedate"));
//                    invoice.setContactId(rs.getLong("contact"));
//                    invoice.setOpertionType(rs.getInt("operationtype"));
//                    invoice.setTotalCost(rs.getBigDecimal("totalcost"));
//
//                } else {
//                    return null;
//                }
//            }
//
//            if (fullLoad) {
//                try (Statement stmt = DatabaseManager.instance.createStatement()) {
//                    ResultSet rs = stmt.executeQuery("select * from invoicedetail where invoiceid = " + invoiceId);
//                    while (rs.next()) {
//                        InvoiceDetail invoiceDetail = new InvoiceDetail();
//                        invoiceDetail.setDetailId(rs.getLong("detailId"));
//                        invoiceDetail.setDate(rs.getLong("date"));
//                        invoiceDetail.setContactId(rs.getLong("contactId"));
//                        invoiceDetail.setOpertationType(rs.getInt("operationType"));
//                        invoiceDetail.setItemId(rs.getLong("itemId"));
//                        invoiceDetail.setSuAmount(rs.getBigDecimal("suAmount"));
//                        invoiceDetail.setSuBuyPrice(rs.getBigDecimal("suBuyPrice"));
//                        invoiceDetail.setRefDetailId(rs.getLong("refDetailId"));
//                        invoiceDetail.setSuSellPrice(rs.getBigDecimal("suSellPrice"));
//                        invoiceDetail.setInvoiceId(rs.getLong("invoiceid"));
//
//                        invoice.addOriginal(invoiceDetail);
//                    }
//                    rs.close();
//                }
//
//            }
//
//            return invoice;
//        } catch (SQLException ex) {
//            Logger.getLogger(InvoiceManager.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//    }
//
//    public static void insert(Invoice invoice) {
//        invoice.calculatePayments();
//        try {
//            DatabaseManager.instance.BeginTransaction();
//
//            CallableStatement call;
//
//            //Insert header
//            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoice_insert(?,?,?,?,?)");
//            DatabaseManager.SetLong(call, 1, invoice.getInvoiceDate());
//            DatabaseManager.SetLong(call, 2, invoice.getContactId());
//            DatabaseManager.SetLong(call, 3, invoice.getOpertionType());
//            DatabaseManager.SetBigDecimal(call, 4, invoice.getTotalCost());
//            call.registerOutParameter(5, Types.BIGINT);
//            call.execute();
//            long generatedId = call.getLong(5);
//            invoice.setInvoiceId(generatedId);
//            call.close();
//
//            //insert details
//            for (InvoiceDetail invoiceDetail : invoice.getDetailList()) {
//                insertDetail(invoiceDetail, generatedId);
//            }
//
//            // insert payments
//            //CALL shopmanager.payment_insert(:document,:invoice,:contact,:creditor,:debtor,:object_id,:object_type);
//            for (Payment payment : invoice.getAllPayments()) {
//                savePayment(payment, invoice, "CALL shopmanager.payment_insert(NULL,?,?,?,?,?,?)");
//            }
//
//            DatabaseManager.instance.CommitTransaction();
//
//        } catch (SQLException ex) {
//            DatabaseManager.instance.Rollback();
//            Logger.getLogger(InvoiceManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//
//    public static void update(Invoice invoice) {
//        invoice.calculatePayments();
//        try {
//            DatabaseManager.instance.BeginTransaction();
//            CallableStatement call = null;
//
//            ArrayList<InvoiceDetail> deletedItems = invoice.getDeletedItems();
//            for (InvoiceDetail deletedItem : deletedItems) {
//                //CALL shopmanager.invoicedetail_buy_delete(:in_invoiceDetailBuyId,?)
//                String callStatement = null;
//
//                switch (deletedItem.getOpertationType()) {
//                    case Invoice.TYPE_BUY:
//                        callStatement = "CALL shopmanager.invoicedetail_buy_delete(?,?)";
//                        break;
//                    case Invoice.TYPE_REFUND_BUY:
//                        callStatement = "CALL shopmanager.invoicedetail_refund_buy_delete(?,?)";
//                        break;
//                    case Invoice.TYPE_SELL:
//                        callStatement = "CALL shopmanager.invoicedetail_sell_delete(?,?)";
//                        break;
//                    case Invoice.TYPE_REFUND_SELL:
//                        callStatement = "CALL shopmanager.invoicedetail_refund_sell_delete(?,?)";
//                        break;
//                }
//
//                call = DatabaseManager.instance.prepareCall(callStatement);
//                DatabaseManager.SetLong(call, 1, deletedItem.getDetailId());
//                call.registerOutParameter(2, Types.INTEGER);
//                call.execute();
//                call.close();
//
//            }
//
//            for (InvoiceDetail invoiceDetail : invoice.getDetailList()) {
//                if (invoiceDetail.getDetailId() == 0) {
//                    insertDetail(invoiceDetail, invoice.getInvoiceId());
//                } else {
//                    switch (invoice.getOpertionType()) {
//                        case Invoice.TYPE_BUY:
//                            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_buy_update(?,?,?,?,?,?)");
//                            DatabaseManager.SetLong(call, 1, invoiceDetail.getDetailId());
//                            DatabaseManager.SetLong(call, 2, invoiceDetail.getDate());
//                            DatabaseManager.SetLong(call, 3, invoiceDetail.getContactId());
//                            DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                            DatabaseManager.SetBigDecimal(call, 5, invoiceDetail.getSuBuyPrice());
//                            call.registerOutParameter(6, Types.INTEGER);
//                            break;
//                        case Invoice.TYPE_REFUND_BUY:
//                            //CALL shopmanager.invoicedetail_refund_buy_update(:in_buyRefundId,:in_date,:in_contactId,:in_suAmount,?)
//                            //TAKES 4 input - 1 output
//                            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_refund_buy_update(?,?,?,?,?)");
//                            DatabaseManager.SetLong(call, 1, invoiceDetail.getDetailId());
//                            DatabaseManager.SetLong(call, 2, invoiceDetail.getDate());
//                            DatabaseManager.SetLong(call, 3, invoiceDetail.getContactId());
//                            DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                            call.registerOutParameter(5, Types.INTEGER);
//                            break;
//                        case Invoice.TYPE_SELL:
//                            //CALL shopmanager.invoicedetail_sell_update(:in_invoiceDetailSellId,:in_date,:in_contact,:in_suAmount,:in_suSellPrice,?)
//                            //TAKES 5 input - 1 output
//                            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_sell_update(?,?,?,?,?,?)");
//                            DatabaseManager.SetLong(call, 1, invoiceDetail.getDetailId());
//                            DatabaseManager.SetLong(call, 2, invoiceDetail.getDate());
//                            DatabaseManager.SetLong(call, 3, invoiceDetail.getContactId());
//                            DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                            DatabaseManager.SetBigDecimal(call, 5, invoiceDetail.getSuSellPrice());
//                            call.registerOutParameter(6, Types.INTEGER);
//                            break;
//                        case Invoice.TYPE_REFUND_SELL:
//                            //CALL shopmanager.invoicedetail_refund_sell_update(:in_sell_refund_id,:in_date,:in_contact,:in_su_amount,?)
//                            //TAKES 4 input - 1 output
//                            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_refund_sell_update(?,?,?,?,?)");
//                            DatabaseManager.SetLong(call, 1, invoiceDetail.getDetailId());
//                            DatabaseManager.SetLong(call, 2, invoiceDetail.getDate());
//                            DatabaseManager.SetLong(call, 3, invoiceDetail.getContactId());
//                            DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                            call.registerOutParameter(5, Types.INTEGER);
//                            break;
//                        default:
//                            break;
//                    }
//
//                    call.execute();
//                    call.close();
//                }
//            }
//
//            //Update header
//            //CALL shopmanager.invoice_update(:in_invoice_id,:in_date,:in_contact,:in_type,:in_total_cost)
//            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoice_update(?,?,?,?,?)");
//            DatabaseManager.SetLong(call, 1, invoice.getInvoiceId());
//            DatabaseManager.SetLong(call, 2, invoice.getInvoiceDate());
//            DatabaseManager.SetLong(call, 3, invoice.getContactId());
//            DatabaseManager.SetInt(call, 4, invoice.getOpertionType());
//            DatabaseManager.SetBigDecimal(call, 5, invoice.getTotalCost());
//            call.execute();
//            call.close();
//
//            // update payments
//            //CALL shopmanager.payment_insert(:document,:invoice,:contact,:creditor,:debtor,:object_id,:object_type);
//            for (Payment payment : invoice.getAllPayments()) {
//                savePayment(payment, invoice, "CALL shopmanager.payment_update(NULL,?,?,?,?,?,?)");
//
//            }
//
//            DatabaseManager.instance.CommitTransaction();
//        } catch (SQLException ex) {
//            Logger.getLogger(InvoiceManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public static void delete(Invoice invoice) {
//        try {
//            DatabaseManager.instance.BeginTransaction();
//            CallableStatement call;
//            //The procedure it self will manager operation types
//            call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoice_delete(?)");
//            DatabaseManager.SetLong(call, 1, invoice.getInvoiceId());
//            call.execute();
//            call.close();
//            DatabaseManager.instance.CommitTransaction();
//        } catch (SQLException ex) {
//            DatabaseManager.instance.Rollback();
//            Logger.getLogger(InvoiceManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private static void insertDetail(InvoiceDetail invoiceDetail, long invoiceId) throws SQLException {
//        CallableStatement call;
//        switch (invoiceDetail.getOpertationType()) {
//            case Invoice.TYPE_BUY:
//                //CALL shopmanager.invoicedetail_buy_insert(:in_date,:in_contact,:in_item,:in_su_buy_amount,:in_su_buy_price,:in_invoiceid)
//                //Takes 5 parameters
//                call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_buy_insert(?,?,?,?,?,?);");
//                DatabaseManager.SetLong(call, 1, invoiceDetail.getDate());
//                DatabaseManager.SetLong(call, 2, invoiceDetail.getContactId());
//                DatabaseManager.SetLong(call, 3, invoiceDetail.getItemId());
//                DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                DatabaseManager.SetBigDecimal(call, 5, invoiceDetail.getSuBuyPrice());
//                DatabaseManager.SetLong(call, 6, invoiceId);
//                call.execute();
//                call.close();
//                break;
//            case Invoice.TYPE_REFUND_BUY:
//                //CALL shopmanager.invoicedetail_refund_buy_insert(:in_date,:in_contactId,:in_itemId,:in_suAmount,:in_refDetailId,:in_invoiceid,?);
//                //Takes 7 parameters last one is OUTPUT
//                call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_refund_buy_insert(?,?,?,?,?,?,?);");
//                DatabaseManager.SetLong/*  */(call, 1, invoiceDetail.getDate());
//                DatabaseManager.SetLong/*  */(call, 2, invoiceDetail.getContactId());
//                DatabaseManager.SetLong/*  */(call, 3, invoiceDetail.getItemId());
//                DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                DatabaseManager.SetLong/*  */(call, 5, invoiceDetail.getRefDetailId());
//                DatabaseManager.SetLong/*  */(call, 6, invoiceId);
//                call.registerOutParameter/*      */(7, Types.INTEGER);
//                call.execute();
//                call.close();
//                break;
//
//            case Invoice.TYPE_SELL:
//                //CALL shopmanager.invoicedetail_sell_insert(:in_date,:in_contact,:in_item,:in_su_amount,:in_su_sell_price,:in_ref_detail_id,:in_invoiceid,?)
//                //Takes 8 params last one is OUTPUT
//                call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_sell_insert(?,?,?,?,?,?,?,?);");
//                DatabaseManager.SetLong/*  */(call, 1, invoiceDetail.getDate());
//                DatabaseManager.SetLong/*  */(call, 2, invoiceDetail.getContactId());
//                DatabaseManager.SetLong/*  */(call, 3, invoiceDetail.getItemId());
//                DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                DatabaseManager.SetBigDecimal(call, 5, invoiceDetail.getSuSellPrice());
//                DatabaseManager.SetLong/*  */(call, 6, invoiceDetail.getRefDetailId());
//                DatabaseManager.SetLong/*  */(call, 7, invoiceId);
//                call.registerOutParameter/*      */(8, Types.INTEGER);
//                call.execute();
//                call.close();
//                break;
//            case Invoice.TYPE_REFUND_SELL:
//                //CALL shopmanager.invoicedetail_refund_sell_insert(:in_date,:in_contact,:in_item,:in_su_amount,:in_ref_sell,:in_invoiceid,?)
//                //Takes 7 last one is OUTPUT
//                call = DatabaseManager.instance.prepareCall("CALL shopmanager.invoicedetail_refund_sell_insert(?,?,?,?,?,?,?);");
//                DatabaseManager.SetLong/*  */(call, 1, invoiceDetail.getDate());
//                DatabaseManager.SetLong/*  */(call, 2, invoiceDetail.getContactId());
//                DatabaseManager.SetLong/*  */(call, 3, invoiceDetail.getItemId());
//                DatabaseManager.SetBigDecimal(call, 4, invoiceDetail.getSuAmount());
//                DatabaseManager.SetLong/*  */(call, 5, invoiceDetail.getRefDetailId());
//                DatabaseManager.SetLong/*  */(call, 6, invoiceId);
//                call.registerOutParameter/*      */(7, Types.INTEGER);
//                call.execute();
//                call.close();
//                break;
//
//        }
//    }
//
//    private static void savePayment(Payment payment, Invoice invoice, String command) throws SQLException {
//        try (CallableStatement call = DatabaseManager.instance.prepareCall(command)) {
//            DatabaseManager.SetLong(call, 1, invoice.getInvoiceId());
//            DatabaseManager.SetLong(call, 2, invoice.getContactId());
//            DatabaseManager.SetBigDecimal(call, 3, payment.getCreditor());
//            DatabaseManager.SetBigDecimal(call, 4, payment.getDeptor());
//            DatabaseManager.SetLong(call, 5, payment.getObjectId());
//            DatabaseManager.SetInt(call, 6, payment.getObjectType());
//            call.execute();
//        }
//    }
//
////    private static void testUpdate() {
////        Invoice invoice = find(3, true);
////
////        invoice.setContactId(1103);
////        invoice.setInvoiceDate(202005061520L);
////
////        invoice.get(1).setSuAmount(new BigDecimal(90));
////        invoice.get(1).setSuBuyPrice(new BigDecimal(480));
////
////        invoice.remove(0);
////
////        long KALA_6 = 13020301;
////        InvoiceDetail invoiceDetail = new InvoiceDetail();
////        invoiceDetail.setItemId(KALA_6);
////        invoiceDetail.setSuAmount(new BigDecimal(650));
////        invoiceDetail.setSuBuyPrice(new BigDecimal(200));
////        invoice.add(invoiceDetail);
////
////        update(invoice);
////
////    }
//
//    //---------------------------------------------------------
////    public static void main(String[] args) {
////
////        testInsert();
////        testDelete();
////        testUpdate();
////
////    }
////    
////    private static void testDelete() {
////        Invoice invoice = new Invoice();
////        invoice.setOpertionType(Invoice.TYPE_BUY);
////        invoice.setInvoiceId(3);
////        delete(invoice);
////    }
////
////    private static void testInsert() {
////        long KALA_1 = 13010101;
////        long KALA_5 = 13010302;
////
////        Invoice invoice = new Invoice();
////        invoice.setContactId(1101);
////        invoice.setInvoiceDate(139901012135L);
////        invoice.setOpertionType(Invoice.TYPE_BUY);
////
////        InvoiceDetail invoiceDetail;
////
////        invoiceDetail = new InvoiceDetail();
////        invoiceDetail.setItemId(KALA_1);
////        invoiceDetail.setSuAmount(new BigDecimal(50));
////        invoiceDetail.setSuBuyPrice(new BigDecimal(650));
////        invoice.add(invoiceDetail);
////
////        invoiceDetail = new InvoiceDetail();
////        invoiceDetail.setItemId(KALA_5);
////        invoiceDetail.setSuAmount(new BigDecimal(35));
////        invoiceDetail.setSuBuyPrice(new BigDecimal(800));
////        invoice.add(invoiceDetail);
////
////        insert(invoice);
////    }
////
////
//}
