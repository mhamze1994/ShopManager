/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import application.JalaliCalendar;
import entity.Bank;
import entity.CashBox;
import entity.invoice.Invoice;
import entity.Payment;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import ui.AppTheme;

/**
 *
 * @author PersianDevStudio
 */
public class PaymentUISimple extends javax.swing.JPanel {

    private Payment payment = new Payment();

    private int type;

    private Invoice invoice;

    private Object[] arrAllBanks;

    public PaymentUISimple(Invoice invoice, Payment payment) {
        this.payment = payment;
        this.invoice = invoice;
        this.type = payment.getObjectType();
        initComponents();
        setup(false);

        if (payment.getObjectType() == Payment.CHEQUE) {
            inputFieldChequeValue.setText(payment.getDeptor().add(payment.getCreditor()).stripTrailingZeros().toPlainString());
            inputFieldDate.setText(JalaliCalendar.format(payment.getExtraLong("date")));
            inputFieldChequeSerial.setText(payment.getExtraLong("serial") + "");
            comboList1.setSelectedItem(findBank(payment.getExtraLong("bankid")));

        } else {
            inputFieldValue.setText(payment.getDeptor().add(payment.getCreditor()).stripTrailingZeros().toPlainString());
        }

    }

    public PaymentUISimple(Invoice invoice, int type) {
        this.invoice = invoice;
        this.type = type;
        initComponents();
        setup(true);

        updatePaymentValue(type != Payment.CHEQUE ? inputFieldChequeValue : inputFieldValue);

        updateChequeDate();
        updateChequeSerial();
        updateBankId();
    }

    /**
     * Creates new form PaymentUISimple
     */
    public PaymentUISimple() {
        initComponents();
        setup(true);
    }

    private void setup(boolean addPayment) {
        inputFieldValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePaymentValue(inputFieldValue);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePaymentValue(inputFieldValue);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        arrAllBanks = Bank.all().toArray();
        comboList1.setModel(new DefaultComboBoxModel(arrAllBanks));
        comboList1.addActionListener((ActionEvent e) -> {
            updateBankId();
        });

        inputFieldChequeValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePaymentValue(inputFieldChequeValue);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePaymentValue(inputFieldChequeValue);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        inputFieldChequeSerial.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateChequeSerial();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChequeSerial();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });

        inputFieldDate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateChequeDate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChequeDate();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

        });

        switch (type) {
            case Payment.BANK_ACCOUNT:
                payment.setObjectType(Payment.BANK_ACCOUNT);
                comboListTargetObjects.setModel(getPosAccountModel());
                break;
            case Payment.CASH_BOX:
                payment.setObjectType(Payment.CASH_BOX);
                comboListTargetObjects.setModel(getCashBoxModel());
                break;
            case Payment.CHEQUE:
                payment.setObjectType(Payment.CHEQUE);
                payment.setObjectId(Payment.OBJECT_ID_CHEQUE_MANAGER);
                break;
        }

        if (type != Payment.CHEQUE) {
            remove(jPanel2);
            comboListTargetObjects.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    payment.setObjectId(((PaymentObject) comboListTargetObjects.getSelectedItem()).id);
                }
            });
            payment.setObjectId(((PaymentObject) comboListTargetObjects.getSelectedItem()).id);
        } else {
            remove(jPanel1);
        }

        if (addPayment) {
            invoice.addPayment(payment);
        }
    }

    private void updateBankId() {
        payment.putExtra("bankid", ((Bank) comboList1.getSelectedItem()).id);
    }

    private void updateChequeSerial() {

        try {
            long serial = Long.parseLong(inputFieldChequeSerial.getText());
            payment.putExtra("serial", serial);
        } catch (Exception e) {
            payment.putExtra("serial", 0L);
        }

    }

    private Bank findBank(long bankId) {
        for (Object bank : arrAllBanks) {
            if (((Bank) bank).id == bankId) {
                return (Bank) bank;
            }
        }
        return null;
    }

    private void updateChequeDate() {
        if (inputFieldDate.isValidInput()) {
            payment.putExtra("date", Long.parseLong(inputFieldDate.getText().replace("/", "")));
        } else {
            payment.putExtra("date", 0);
        }
    }

    public final DefaultComboBoxModel getPosAccountModel() {
        DefaultComboBoxModel<PaymentObject> dcbm = new DefaultComboBoxModel<>();

        dcbm.addElement(new PaymentObject("بانک", 1));

        return dcbm;
    }

    public final DefaultComboBoxModel getCashBoxModel() {

        DefaultComboBoxModel<PaymentObject> dcbm = new DefaultComboBoxModel<>();

        ArrayList<CashBox> cashBox = CashBox.listAll();
        for (CashBox cb : cashBox) {
            dcbm.addElement(new PaymentObject(cb.getTitle(), cb.getCashboxId()));
        }
        return dcbm;
    }

    public class PaymentObject {

        public String name;
        public long id;

        private PaymentObject(String name, long id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return String.valueOf(name);
        }

    }

    private void updatePaymentValue(JTextField textField) {
        try {
            if (Invoice.isExporting(invoice.getOperationType())) {
                payment.setCreditor(new BigDecimal(textField.getText()));
                payment.setDeptor(BigDecimal.ZERO);
            } else {
                payment.setCreditor(BigDecimal.ZERO);
                payment.setDeptor(new BigDecimal(textField.getText()));
            }
            textField.setForeground(AppTheme.COLOR_TEXT_DEFAULT);
        } catch (Exception e) {
            payment.setCreditor(BigDecimal.ZERO);
            payment.setDeptor(BigDecimal.ZERO);
            textField.setForeground(AppTheme.COLOR_WARNING);
        }
    }

    public Payment getPayment() {
        return payment;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        inputFieldValue = new ui.controls.input.InputFieldNumber();
        comboListTargetObjects = new ui.controls.ComboList();
        pressButton1 = new ui.controls.PressButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        pressButton2 = new ui.controls.PressButton();
        inputFieldChequeValue = new ui.controls.input.InputFieldNumber();
        inputFieldChequeSerial = new ui.controls.input.InputFieldNumber();
        comboList1 = new ui.controls.ComboList();
        inputFieldDate = new ui.controls.input.InputFieldDate();
        jSeparator2 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        inputFieldValue.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مبلغ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        comboListTargetObjects.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "پرداخت از/به", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        comboListTargetObjects.setPreferredSize(new java.awt.Dimension(150, 18));

        pressButton1.setText("حذف");
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(comboListTargetObjects, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputFieldValue, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jSeparator1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(inputFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboListTargetObjects, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jPanel1, "nomralPayment");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        pressButton2.setText("حذف");
        pressButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton2ActionPerformed(evt);
            }
        });

        inputFieldChequeValue.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مبلغ چک", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputFieldChequeSerial.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شماره سریال", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        comboList1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "بانک", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputFieldDate.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تاریخ چک", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(comboList1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputFieldChequeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(inputFieldChequeSerial, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputFieldChequeValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboList1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputFieldChequeSerial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jPanel2, "chequePayment");
    }// </editor-fold>//GEN-END:initComponents

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        removePayment();
    }//GEN-LAST:event_pressButton1ActionPerformed


    private void pressButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton2ActionPerformed
        removePayment();
    }//GEN-LAST:event_pressButton2ActionPerformed

    private void removePayment() {
        Container parent = getParent();
        invoice.removePayment(payment);
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.controls.ComboList comboList1;
    private ui.controls.ComboList comboListTargetObjects;
    private ui.controls.input.InputFieldNumber inputFieldChequeSerial;
    private ui.controls.input.InputFieldNumber inputFieldChequeValue;
    private ui.controls.input.InputFieldDate inputFieldDate;
    private ui.controls.input.InputFieldNumber inputFieldValue;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private ui.controls.PressButton pressButton1;
    private ui.controls.PressButton pressButton2;
    // End of variables declaration//GEN-END:variables
}
