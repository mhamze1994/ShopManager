package entity.invoice;

import application.Calculator;
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
                        if (p.getObjectType() != Payment.CHEQUE) {
                            invoice.addPayment(p);
                        }
                    }
                }

                findInvoice.getMoreResults();
                ResultSet rsCheques = findInvoice.getResultSet();
                while (rsCheques.next()) {
                    Payment p = new Payment();
                    p.readResultSetAsCheque(invoice , rsCheques);
                    invoice.addPayment(p);
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
            CallableStatement detailInsert = DatabaseManager.instance.prepareCall("CALL invoicedetail_insert(?,?,?,?,?,?,?,?,?,?,?)");
            DatabaseManager.SetLong/*  */(detailInsert, 1, detail.getDate());
            DatabaseManager.SetLong/*  */(detailInsert, 2, detail.getContactId());
            DatabaseManager.SetInt/*   */(detailInsert, 3, detail.getOperationType());
            DatabaseManager.SetLong/*  */(detailInsert, 4, detail.getItemId());

            DatabaseManager.SetBigDecimal(detailInsert, 5, detail.getAmount());
            DatabaseManager.SetInt(detailInsert, 6, detail.getUnitId());
            DatabaseManager.SetBigDecimal(detailInsert, 7, detail.getRatio());
//            DatabaseManager.SetBigDecimal(detailInsert, 5, detail.getSuAmount());

            DatabaseManager.SetBigDecimal(detailInsert, 8, detail.getUnitPrice());
            DatabaseManager.SetBigDecimal(detailInsert, 9, detail.getSuPrice());

            DatabaseManager.SetLong/*  */(detailInsert, 10, detail.getRefDetailId());
            DatabaseManager.SetLong/*  */(detailInsert, 11, detail.getInvoiceId());
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
            if (payment.getObjectType() == Payment.CHEQUE) {
                try (CallableStatement ps = DatabaseManager.instance.prepareCall("CALL cheque_insert(?,?,?,?,?,?)")) {
                    DatabaseManager.SetLong(ps, 1, payment.getExtraLong("bankId"));
                    DatabaseManager.SetBigDecimal(ps, 2, Calculator.add(payment.getDeptor(), payment.getCreditor()));
                    DatabaseManager.SetInt(ps, 3, Invoice.isExporting(invoice.getOperationType()) ? 1 : 2);//1 received cheque , 2 payed cheque
                    DatabaseManager.SetLong(ps, 4, payment.getExtraLong("serial"));
                    DatabaseManager.SetLong(ps, 5, invoice.getInvoiceId());
                    DatabaseManager.SetLong(ps, 6, payment.getExtraLong("date"));
                    ps.execute();
                }
            }
        }
    }

}
