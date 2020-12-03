/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import application.Calculator;
import entity.Contact;
import application.DatabaseManager;
import application.ItemPrice;
import application.printer.InvoicePrint;
import entity.Item;
import entity.invoice.Invoice;
import entity.invoice.InvoiceDetail;
import entity.invoice.InvoiceManager;
import entity.Payment;
import entity.Unit;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import ui.AppTheme;
import ui.container.TabbedContainer;
import ui.controls.PressButton;
import ui.controls.TextView;
import ui.controls.input.InputField;

/**
 *
 * @author PersianDevStudio
 */
public final class InvoiceEditor extends javax.swing.JPanel {

    public static final int MODE_INSERT = 1;
    public static final int MODE_UPDATE = 2;

    private Invoice invoice;

    private InvoiceDetail selectedDetail;

    private String[] paymentTypes = {"POS", "صندوق", "چک"};
    private TabbedContainer parentTabbedPanel;

    private KeyStroke strokeCtrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK);
    private String actionMapSaveInvoice = "saveInvoice";

    private KeyStroke strokeCtrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK);
    private String actionMapGoToPayments = "goToPayments";

    /**
     * Creates new form InvoiceEditor
     */
    public InvoiceEditor() {

    }

    public InvoiceEditor(Invoice invoice) {
        init(invoice);
    }

    private void init(Invoice invoice) {
        initComponents();
        this.invoice = invoice;

        actionReload.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        actionReload.setTextVerticalPosition(PressButton.POSITION_CENTER);

        actionSave.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        actionSave.setTextVerticalPosition(PressButton.POSITION_CENTER);

        buttonInsert.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        buttonInsert.setTextVerticalPosition(PressButton.POSITION_CENTER);
        buttonInsert.drawTextUnderline(true);

        buttonCancel.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        buttonCancel.setTextVerticalPosition(PressButton.POSITION_CENTER);
        buttonCancel.drawTextUnderline(true);
        buttonCancel.setVisible(false);

        buttonDelete.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        buttonDelete.setTextVerticalPosition(PressButton.POSITION_CENTER);
        buttonDelete.drawTextUnderline(true);
        buttonDelete.setVisible(false);

        inputAreaLogEditor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        pressButtonAddPayment.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButtonAddPayment.setTextVerticalPosition(PressButton.POSITION_CENTER);

        comboListPaymentTypes.setModel(new DefaultComboBoxModel(paymentTypes));

        invoiceItemList.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting() == false) {
                int selectedRow = invoiceItemList.getSelectedRow();
                if (selectedRow >= 0) {
                    setSelectedDetail(invoice.get(selectedRow));
                }
            }
        });
        contactPicker.setOnPick((Contact contact) -> {
            if (parentTabbedPanel != null) {
                refreshTitle(parentTabbedPanel);
            }
            refreshLatestTrades();
        });
        itemPicker.setOnPick((Item item) -> {
            setSelectedItem(item);
//            refreshLatestTrades();
//            fillItemPrices(item);
        });

        java.awt.EventQueue.invokeLater(contactPicker.getNumberField()::requestFocusInWindow);

        setActionForAll(strokeCtrlEnter, actionMapSaveInvoice, invoiceSaveAction());
        setActionForAll(strokeCtrlP, actionMapGoToPayments, goToPaymentAction());

        refreshLatestTrades();
        ArrayList<Component> uiComponentOrder = new ArrayList<>();
        uiComponentOrder.add(inputDate);
        uiComponentOrder.add(contactPicker.getNumberField());
        uiComponentOrder.add(contactPicker.getSearchField());
        uiComponentOrder.add(itemPicker.getNumberField());
        uiComponentOrder.add(itemPicker.getSearchField());
        uiComponentOrder.add(inputAmount);
        uiComponentOrder.add(comboListUnit);
        uiComponentOrder.add(comboListPrice.getEditor().getEditorComponent());
        uiComponentOrder.add(buttonInsert);
        paneBody.setFocusCycleRoot(true);
        paneBody.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(uiComponentOrder));

        jScrollPane2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        invoiceItemList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        InvoiceTableModelSetup.setupTableNormal(invoice, invoiceItemList, this);

        contactPicker.updateTitles(invoice.getOperationType());

        if (Invoice.isRefund(invoice.getOperationType())) {
            inputAreaLog.setVisible(true);
        } else {
            inputAreaLog.setVisible(false);
        }

        TitledBorder priceTitle = (TitledBorder) comboListPrice.getBorder();
        if (Invoice.inoutBuy(invoice.getOperationType())) {
            priceTitle.setTitle("قیمت خرید");
        } else if (Invoice.inoutSell(invoice.getOperationType())) {
            priceTitle.setTitle("قیمت فروش");
        }

        invoice.setInvoiceUpdateListener(() -> {
            updateInvoiceSummery();
        });

        //Set initial invoice values
        if (invoice.getInvoiceId() != 0) {
            inputFieldNumber1.setText(invoice.getInvoiceId() + "");
        }

        inputDate.setText(invoice.getDate() + "");
        Contact contact = Contact.find(invoice.getContact());
        setSelectedContact(contact);
//        searchBoxContact.setItem(selectedContact);

        for (Payment payment : invoice.getContactPayments()) {
            addPaymentMethod(payment);
        }
        updateInvoiceSummery();
        //Note itemdetails should already be inside inovicedetail and table
        //will automatically update
    }

    private void setActionForAll(KeyStroke strokeCtrlEnter, String actionMapSaveInvoice, AbstractAction action) {
        setActionFor(splitGroupMain, strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(contactPicker.getNumberField(), strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(contactPicker.getSearchField(), strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(itemPicker.getNumberField(), strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(itemPicker.getSearchField(), strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(inputAmount, strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(comboListUnit, strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor((JComponent) comboListPrice.getEditor().getEditorComponent(), strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(buttonInsert, strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(comboListPaymentTypes, strokeCtrlEnter, actionMapSaveInvoice, action);
        setActionFor(pressButtonAddPayment, strokeCtrlEnter, actionMapSaveInvoice, action);
    }

    private AbstractAction invoiceSaveAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveInvoice();
            }
        };
    }

    private AbstractAction goToPaymentAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButtonAddPayment.requestFocusInWindow();
            }
        };
    }

    private void updateInvoiceSummery() {
        invoice.updateTotalCost();
        String str = "مبلغ فاکتور : " + invoice.getTotalCost().abs().stripTrailingZeros().toPlainString();
        str += "      ";
        str += "پرداخت : ";
        str += invoice.displayTotalContactPayment().abs().stripTrailingZeros().toPlainString();
        str += "      ";
        str += "مانده: ";
        str += Calculator.sub(invoice.getTotalCost().abs(), invoice.displayTotalContactPayment().abs()).stripTrailingZeros().toPlainString();
        textViewInvoiceSummary.setText(str);
    }

    private void setSelectedDetail(InvoiceDetail detail) {
        selectedDetail = detail;

        buttonDelete.setVisible(true);
        buttonCancel.setVisible(true);

        itemPicker.setSelectedItem(detail.getItem());

        setSelectedItem(detail.getItem());
//        itemPicker.setSelectedItem(detail.getItem());

        inputAmount.setText(detail.getAmount().abs().stripTrailingZeros().toPlainString());
        comboListPrice.setSelectedItem(detail.getUnitPrice().abs().stripTrailingZeros().toPlainString());

    }

    private boolean detailInputIsValid() {
        boolean isValid = true;
        StringBuilder errorLog = new StringBuilder();
        try {
            BigDecimal amount = new BigDecimal(inputAmount.getText());
            if (amount.compareTo(BigDecimal.ZERO) == -1 || amount.equals(BigDecimal.ZERO)) {
                errorLog.append("مقدار وارد شده باید بیشتر از صفر باشد. -").append(System.lineSeparator());
                isValid = false;
            }
        } catch (Exception ex) {
            errorLog.append("مقدار وارد شده صحیح نیست. -").append(System.lineSeparator());
            isValid = false;
        }

        if (itemPicker.getSelectedItem() == null) {
            errorLog.append("هیچ کالایی انتخاب نشده است. -").append(System.lineSeparator());
            isValid = false;
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(comboListPrice.getSelectedItem().toString());
            if (bigDecimal.compareTo(BigDecimal.ZERO) == -1) {
                errorLog.append("قیمت وارد شده نباید کمتر از صفر باشد.").append(System.lineSeparator());
                isValid = false;
            }
        } catch (Exception e) {
            isValid = false;
            errorLog.append("قیمت وارد نشده است. -").append(System.lineSeparator());
        }

        if (isValid == false) {
            JOptionPane.showMessageDialog(this, errorLog.toString(), "خطا", JOptionPane.ERROR_MESSAGE);
        }
        return isValid;
    }

    private boolean headerInputValid() {

        boolean isValid = true;

        if (paymentMethodListUI.getComponentCount() == 0) {
            int selection = JOptionPane.showOptionDialog(this, "لیست پرداخت ها خالی میباشد.", "هشدار", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"ادامه بده", "بازگشت"}, null);
            if (selection == 1) {
                return false;
            }
        }

        if (invoice.getTotalCost().abs().compareTo(invoice.displayTotalContactPayment().abs()) == -1) {
            int selection = JOptionPane.showOptionDialog(this, "مبلغ پرداختی بیشتر از مبلغ فاکتور است.", "هشدار", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"ثبت به حساب بستانکاری", "اصلاح"}, null);
            if (selection == 1) {
                return false;
            }
        }
        if (invoice.getTotalCost().abs().compareTo(invoice.displayTotalContactPayment().abs()) == 1) {
            int selection = JOptionPane.showOptionDialog(this, "مبلع پرداختی کمتر از مبلغ فاکتور است", "هشدار", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"ثبت به حساب بدهکاری", "اصلاح"}, null);
            if (selection == 1) {
                return false;
            }
        }

        StringBuilder errorLog = new StringBuilder();
        if (inputDate.isValidInput() == false) {
            isValid = false;
            errorLog.append("تاریخ اشتباه وارد شده است. -").append(System.lineSeparator());
        }
        if (contactPicker.getSelectedContact() == null) {
            isValid = false;
            errorLog.append("مخاطب فاکتور انتخاب نشده است. -").append(System.lineSeparator());
        }
        if (invoice.getDetails().isEmpty()) {
            isValid = false;
            errorLog.append("فاکتور خالی میباشد. -").append(System.lineSeparator());
        }

        for (Component component : paymentMethodListUI.getComponents()) {
            PaymentUISimple paymentUI = (PaymentUISimple) component;
            if (paymentUI.getPayment().isValid() == false) {
                errorLog.append("رقم تمام پرداخت ها باید بیشتر از صفر باشد. -").append(System.lineSeparator());
                isValid = false;
                break;
            }
        }

        if (isValid == false) {
            JOptionPane.showMessageDialog(this, errorLog.toString(), "خطا", JOptionPane.ERROR_MESSAGE);
        }

        return isValid;
    }

    private void saveInvoice() {
        try {
            if (headerInputValid() == false) {
                return;
            }

            invoice.setDate(Long.parseLong(inputDate.getText().replace("/", "")));
            invoice.setContact(contactPicker.getSelectedContact().getContactId());

            boolean success = false;
            if (invoice.getInvoiceId() == 0) {
                InvoiceManager.insert(invoice);
                refreshTitle(parentTabbedPanel);
                inputFieldNumber1.setText(invoice.getInvoiceId() + "");
                success = true;
            } else {
                if (InvoiceManager.update(invoice) == false) {
                    JOptionPane.showMessageDialog(this, "اعمال تغییرات امکان پذیر نمیباشد.", "خطا", JOptionPane.ERROR_MESSAGE);
                } else {
                    success = true;
                }
            }
            if (success) {
                int selection = JOptionPane.showOptionDialog(this, "فاکتور ثبت شد.", "هشدار", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"چاپ و بستن", "بستن", "ویرایش"}, "چاپ و بستن");
                if (selection == 0 || selection == 1) {

                    if (selection == 0) {
                        try {
                            PrinterJob myPrtJob = PrinterJob.getPrinterJob();
                            PageFormat pageFormat = myPrtJob.defaultPage();
                            myPrtJob.setPrintable(new InvoicePrint(invoice), pageFormat);
                            myPrtJob.print();
                        } catch (PrinterException ex) {
                            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    Container parent = getParent();
                    parent.remove(this);
                    parent.invalidate();
                    parent.repaint();
                }
            }
        } catch (SQLException ex) {
            DatabaseManager.instance.Rollback();
            String msg = ex.getMessage();
            if (msg.equalsIgnoreCase("date_out_of_range")) {
                msg = "تاریخ خارج از سال مالی میباشد.";
                JOptionPane.showMessageDialog(this, msg, "خطای تاریخ", JOptionPane.ERROR_MESSAGE);
            } else {
                Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshTitle(TabbedContainer parentTabbedPanel) {
        this.parentTabbedPanel = parentTabbedPanel;
        String title = "فاکتور";
        switch (invoice.getOperationType()) {
            case Invoice.TYPE_REFUND_BUY:
                title = "برگشت از خرید";
                break;
            case Invoice.TYPE_BUY:
                title = "رسید خرید";
                break;
            case Invoice.TYPE_REFUND_SELL:
                title = "برگشت از فروش";
                break;
            case Invoice.TYPE_SELL:
                title = "حواله فروش";
                break;
        }
        if (contactPicker.getSelectedContact() != null) {
            title += " - " + contactPicker.getSelectedContact().concatinatedInfo();
        }
        this.parentTabbedPanel.setTitleAt(this.parentTabbedPanel.indexOfComponent(this), title.trim());

        String titlePattern = "[1] فاکتور [2]";
        titlePattern = titlePattern.replace("[1]", invoice.getInvoiceId() == 0 ? "ثبت" : "ویرایش");
        switch (invoice.getOperationType()) {
            case Invoice.TYPE_REFUND_BUY:
                titlePattern = titlePattern.replace("[2]", "برگشت از خرید");
                break;
            case Invoice.TYPE_BUY:
                titlePattern = titlePattern.replace("[2]", "خرید");
                break;
            case Invoice.TYPE_REFUND_SELL:
                titlePattern = titlePattern.replace("[2]", "برگشت از فروش");
                break;
            case Invoice.TYPE_SELL:
                titlePattern = titlePattern.replace("[2]", "فروش");
                break;
        }
        textViewTitle.setText(titlePattern);
    }

    private void insertInput() {

        if (detailInputIsValid() == false) {
            return;
        }

        InvoiceDetail invoiceDetail;
        if (selectedDetail != null) {
            invoiceDetail = selectedDetail;
        } else {
            invoiceDetail = new InvoiceDetail();
        }
        invoiceDetail.setItem(itemPicker.getSelectedItem());

        BigDecimal amount = new BigDecimal(inputAmount.getText());
        invoiceDetail.setAmount(amount);
//        in case the operation is in editing mode leave stock check to the final step
        if (invoice.getInvoiceId() == 0) {
            if (Invoice.isExporting(invoice.getOperationType())
                    && itemPicker.getSelectedItem().getLastKnownStock().compareTo(invoiceDetail.getSuAmount()) == -1) {
                JOptionPane.showMessageDialog(this, "موجودی کافی نیست!", "خطا", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        invoiceDetail.setUnit(((Unit) comboListUnit.getSelectedItem()).unitId);
//        invoiceDetail.setSuAmount(amount);
        invoiceDetail.setUnitPrice(new BigDecimal(comboListPrice.getSelectedItem().toString()));
//        invoiceDetail.setSuPrice(new BigDecimal(comboListPrice.getSelectedItem().toString()));

        if (selectedDetail == null) {
            invoice.add(invoiceDetail);
        }

        clearInputs();
    }

    private void clearInputs() {
        EventQueue.invokeLater(() -> {
            buttonDelete.setVisible(false);
            buttonCancel.setVisible(false);

            itemPicker.setSelectedItem(null);
            selectedDetail = null;
            InputField inputFieldItem = itemPicker.getSearchField();
            inputFieldItem.requestFocus();
            inputFieldItem.setText("");
            inputAmount.setText("");
            comboListPrice.setSelectedItem("");
            itemPicker.getNumberField().setText("");
            invoiceItemList.clearSelection();
            invoiceItemList.revalidate();
            invoiceItemList.repaint();
        });
    }

    private void setSelectedItem(Item item) {
        try {
            if (item != null) {
                textViewStock.setText("موجودی : " + item.fetchItemStock().stripTrailingZeros().toPlainString());
//                itemPicker.setSelectedItem(item);
            } else {
                textViewStock.setText("موجودی : -");
//                itemPicker.setSelectedItem(null);
            }
            refreshLatestTrades();

            fillItemPrices(item);

            ArrayList<Unit> units = new ArrayList<>();
            if (item != null) {
                units.add(Unit.getUnit(item.getUnit1()));
                if (item.getUnit2() != 0) {
                    units.add(Unit.getUnit(item.getUnit2()));
                }
            }
            comboListUnit.setModel(new DefaultComboBoxModel(units.toArray()));

        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setSelectedContact(Contact contact) {
        contactPicker.setSelectedContact(contact);

    }

    private void refreshLatestTrades() {
        if (false == Invoice.isRefund(invoice.getOperationType())) {
            return;
        }
        if (contactPicker.getSelectedContact() == null || itemPicker.getSelectedItem() == null) {
            inputAreaLogEditor.setText("");
            return;
        }
        try {
            int refundType = -1;
            if (invoice.getOperationType() == Invoice.TYPE_REFUND_BUY) {
                refundType = Invoice.TYPE_BUY;
            } else if (invoice.getOperationType() == Invoice.TYPE_REFUND_SELL) {
                refundType = Invoice.TYPE_SELL;
            }

            ArrayList<InvoiceDetail> latestDetails = InvoiceDetail.getLatestTrades(
                    contactPicker.getSelectedContact().getContactId(),
                    itemPicker.getSelectedItem().getItemId(),
                    refundType
            );

            StringBuilder logText = new StringBuilder();
            logText.append(invoice.getOperationType() == Invoice.TYPE_REFUND_SELL ? "فروش های اخیر" : "خرید های اخیر")
                    .append(System.lineSeparator()).append(System.lineSeparator());

            for (InvoiceDetail invoiceDetail : latestDetails) {
                logText.append("- ").append(invoiceDetail.getSummary()).append(System.lineSeparator()).append(System.lineSeparator());
            }
            if (latestDetails.isEmpty()) {
                logText.append("هیچ فاکتوری موجود نیست!");
            }
            inputAreaLogEditor.setText(logText.toString());

        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitGroupMain = new ui.container.SplitGroup();
        groupPanePayment = new ui.container.GroupPane();
        textView1 = new ui.controls.TextView();
        scroll1 = new ui.container.Scroll();
        groupPane1 = new ui.container.GroupPane();
        groupPane2 = new ui.container.GroupPane();
        comboListPaymentTypes = new ui.controls.ComboList();
        pressButtonAddPayment = new ui.controls.PressButton();
        groupPane3 = new ui.container.GroupPane();
        paymentMethodListUI = new ui.container.GroupPane();
        groupPaneCenter = new ui.container.GroupPane();
        groupPaneHeader = new ui.container.GroupPane();
        paneHeader = new ui.container.GroupPane();
        textViewTitle = new ui.controls.TextView();
        actionPaneInvoice = new ui.container.GroupPane();
        actionSave = new ui.controls.ImageButton();
        actionReload = new ui.controls.ImageButton();
        paneBody = new ui.container.GroupPane();
        inputFieldNumber1 = new ui.controls.input.InputFieldNumber();
        inputDate = new ui.controls.input.InputFieldDate();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        buttonInsert = new ui.controls.ImageButton();
        inputAreaLog = new javax.swing.JScrollPane();
        inputAreaLogEditor = new ui.controls.input.InputArea();
        textViewStock = new ui.controls.TextView();
        buttonDelete = new ui.controls.ImageButton();
        buttonCancel = new ui.controls.ImageButton();
        contactPicker = new application.ui.ContactPicker();
        groupPane4 = new ui.container.GroupPane();
        comboListPrice = new ui.controls.ComboList();
        inputAmount = new ui.controls.input.InputFieldNumber();
        itemPicker = new application.ui.ItemPicker();
        comboListUnit = new ui.controls.ComboList();
        groupPaneInvoice = new ui.container.GroupPane();
        groupPaneTableHolder = new ui.container.GroupPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        invoiceItemList = new javax.swing.JTable();
        textViewInvoiceSummary = new ui.controls.TextView();

        splitGroupMain.setBackground(new java.awt.Color(255, 255, 255));
        splitGroupMain.setDividerLocation(350);
        splitGroupMain.setContinuousLayout(true);

        textView1.setForeground(new java.awt.Color(51, 51, 51));
        textView1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textView1.setText("دریافت و پرداخت");
        textView1.setFont(new java.awt.Font("B yekan+", 0, 18)); // NOI18N

        scroll1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        groupPane1.setLayout(new java.awt.BorderLayout());

        groupPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        groupPane2.setPreferredSize(new java.awt.Dimension(245, 40));
        groupPane2.setLayout(new java.awt.BorderLayout(5, 5));
        groupPane2.add(comboListPaymentTypes, java.awt.BorderLayout.CENTER);

        pressButtonAddPayment.setText("افزودن");
        pressButtonAddPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButtonAddPaymentActionPerformed(evt);
            }
        });
        groupPane2.add(pressButtonAddPayment, java.awt.BorderLayout.EAST);

        groupPane1.add(groupPane2, java.awt.BorderLayout.NORTH);

        groupPane3.setLayout(new java.awt.BorderLayout());

        paymentMethodListUI.setLayout(new javax.swing.BoxLayout(paymentMethodListUI, javax.swing.BoxLayout.PAGE_AXIS));
        groupPane3.add(paymentMethodListUI, java.awt.BorderLayout.NORTH);

        groupPane1.add(groupPane3, java.awt.BorderLayout.CENTER);

        scroll1.setViewportView(groupPane1);

        javax.swing.GroupLayout groupPanePaymentLayout = new javax.swing.GroupLayout(groupPanePayment);
        groupPanePayment.setLayout(groupPanePaymentLayout);
        groupPanePaymentLayout.setHorizontalGroup(
            groupPanePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPanePaymentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupPanePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scroll1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(textView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        groupPanePaymentLayout.setVerticalGroup(
            groupPanePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPanePaymentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textView1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scroll1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitGroupMain.setLeftComponent(groupPanePayment);

        groupPaneCenter.setLayout(new java.awt.BorderLayout());

        groupPaneHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        groupPaneHeader.setOpaque(false);
        groupPaneHeader.setLayout(new java.awt.BorderLayout());

        paneHeader.setOpaque(false);
        paneHeader.setLayout(new java.awt.BorderLayout());

        textViewTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textViewTitle.setForeground(new java.awt.Color(51, 51, 51));
        textViewTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        textViewTitle.setText("[ثبت/ویرایش] فاکتو [خرید/فروش و ...]");
        textViewTitle.setFont(new java.awt.Font("B yekan+", 0, 18)); // NOI18N
        paneHeader.add(textViewTitle, java.awt.BorderLayout.CENTER);

        actionPaneInvoice.setPreferredSize(new java.awt.Dimension(150, 0));
        actionPaneInvoice.setLayout(new java.awt.GridLayout(1, 0));

        actionSave.setText("ذخیره");
        actionSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionSaveActionPerformed(evt);
            }
        });
        actionPaneInvoice.add(actionSave);

        actionReload.setText("بازیابی");
        actionPaneInvoice.add(actionReload);

        paneHeader.add(actionPaneInvoice, java.awt.BorderLayout.WEST);

        groupPaneHeader.add(paneHeader, java.awt.BorderLayout.NORTH);

        inputFieldNumber1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شماره فاکتور", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputDate.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputDate.setText("");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        buttonInsert.setText("  ثبت  ");
        buttonInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertActionPerformed(evt);
            }
        });

        inputAreaLogEditor.setEditable(false);
        inputAreaLogEditor.setColumns(20);
        inputAreaLogEditor.setRows(5);
        inputAreaLogEditor.setFont(new java.awt.Font("B Yekan", 0, 13)); // NOI18N
        inputAreaLog.setViewportView(inputAreaLogEditor);

        textViewStock.setForeground(new java.awt.Color(0, 153, 0));
        textViewStock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        textViewStock.setText("موجودی : -");
        textViewStock.setFont(new java.awt.Font("B yekan+", 0, 12)); // NOI18N

        buttonDelete.setText("  حذف  ");
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonCancel.setText("لغو");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        comboListPrice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "قیمت", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        comboListPrice.setEditable(true);

        inputAmount.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مقدار", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputAmount.setPreferredSize(new java.awt.Dimension(100, 39));

        comboListUnit.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "واحد", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        javax.swing.GroupLayout groupPane4Layout = new javax.swing.GroupLayout(groupPane4);
        groupPane4.setLayout(groupPane4Layout);
        groupPane4Layout.setHorizontalGroup(
            groupPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane4Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(comboListPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboListUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        groupPane4Layout.setVerticalGroup(
            groupPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane4Layout.createSequentialGroup()
                .addGroup(groupPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboListUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboListPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout paneBodyLayout = new javax.swing.GroupLayout(paneBody);
        paneBody.setLayout(paneBodyLayout);
        paneBodyLayout.setHorizontalGroup(
            paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBodyLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(contactPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paneBodyLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(buttonInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textViewStock, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(inputAreaLog, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        paneBodyLayout.setVerticalGroup(
            paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(contactPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textViewStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonInsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inputAreaLog, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        groupPaneHeader.add(paneBody, java.awt.BorderLayout.CENTER);

        groupPaneCenter.add(groupPaneHeader, java.awt.BorderLayout.NORTH);

        groupPaneInvoice.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        groupPaneInvoice.setOpaque(false);
        groupPaneInvoice.setLayout(new java.awt.BorderLayout());

        groupPaneTableHolder.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        groupPaneTableHolder.setOpaque(false);
        groupPaneTableHolder.setLayout(new java.awt.CardLayout());

        invoiceItemList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(invoiceItemList);

        groupPaneTableHolder.add(jScrollPane2, "card2");

        groupPaneInvoice.add(groupPaneTableHolder, java.awt.BorderLayout.CENTER);

        textViewInvoiceSummary.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5), javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        textViewInvoiceSummary.setForeground(new java.awt.Color(51, 51, 51));
        textViewInvoiceSummary.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        textViewInvoiceSummary.setText(" ");
        groupPaneInvoice.add(textViewInvoiceSummary, java.awt.BorderLayout.PAGE_START);
        textViewInvoiceSummary.getAccessibleContext().setAccessibleName("");

        groupPaneCenter.add(groupPaneInvoice, java.awt.BorderLayout.CENTER);

        splitGroupMain.setRightComponent(groupPaneCenter);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitGroupMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitGroupMain, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInsertActionPerformed
        insertInput();
    }//GEN-LAST:event_buttonInsertActionPerformed

    private void actionSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionSaveActionPerformed
        saveInvoice();
    }//GEN-LAST:event_actionSaveActionPerformed

    private void pressButtonAddPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButtonAddPaymentActionPerformed
        addPaymentMethodEmpty();
    }//GEN-LAST:event_pressButtonAddPaymentActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        removeSelectedDetailFromInvoice();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        clearInputs();
    }//GEN-LAST:event_buttonCancelActionPerformed

    public void addPaymentMethod(Payment payment) {
        PaymentUISimple pUI = new PaymentUISimple(invoice, payment);
        paymentMethodListUI.add(pUI);
        paymentMethodListUI.revalidate();
        paymentMethodListUI.repaint();
    }

    public void addPaymentMethodEmpty() {
        int selectedIndex = comboListPaymentTypes.getSelectedIndex();
        PaymentUISimple c = null;
        switch (selectedIndex) {
            case 0:
                c = new PaymentUISimple(invoice, Payment.BANK_ACCOUNT);
                break;
            case 1:
                c = new PaymentUISimple(invoice, Payment.CASH_BOX);
                break;
            case 2:
                c = new PaymentUISimple(invoice, Payment.CHEQUE);
                break;
        }

        if (c != null) {
            for (int i = 0; i < c.getComponentCount(); i++) {
                setActionFor((JComponent) c.getComponent(i), strokeCtrlEnter, actionMapSaveInvoice, invoiceSaveAction());
            }
        }

        paymentMethodListUI.add(c);
        paymentMethodListUI.revalidate();
        paymentMethodListUI.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.container.GroupPane actionPaneInvoice;
    private ui.controls.ImageButton actionReload;
    private ui.controls.ImageButton actionSave;
    private ui.controls.ImageButton buttonCancel;
    private ui.controls.ImageButton buttonDelete;
    private ui.controls.ImageButton buttonInsert;
    private ui.controls.ComboList comboListPaymentTypes;
    private ui.controls.ComboList comboListPrice;
    private ui.controls.ComboList comboListUnit;
    private application.ui.ContactPicker contactPicker;
    private ui.container.GroupPane groupPane1;
    private ui.container.GroupPane groupPane2;
    private ui.container.GroupPane groupPane3;
    private ui.container.GroupPane groupPane4;
    private ui.container.GroupPane groupPaneCenter;
    private ui.container.GroupPane groupPaneHeader;
    private ui.container.GroupPane groupPaneInvoice;
    private ui.container.GroupPane groupPanePayment;
    private ui.container.GroupPane groupPaneTableHolder;
    private ui.controls.input.InputFieldNumber inputAmount;
    private javax.swing.JScrollPane inputAreaLog;
    private ui.controls.input.InputArea inputAreaLogEditor;
    private ui.controls.input.InputFieldDate inputDate;
    private ui.controls.input.InputFieldNumber inputFieldNumber1;
    private javax.swing.JTable invoiceItemList;
    private application.ui.ItemPicker itemPicker;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private ui.container.GroupPane paneBody;
    private ui.container.GroupPane paneHeader;
    private ui.container.GroupPane paymentMethodListUI;
    private ui.controls.PressButton pressButtonAddPayment;
    private ui.container.Scroll scroll1;
    private ui.container.SplitGroup splitGroupMain;
    private ui.controls.TextView textView1;
    private ui.controls.TextView textViewInvoiceSummary;
    private ui.controls.TextView textViewStock;
    private ui.controls.TextView textViewTitle;
    // End of variables declaration//GEN-END:variables

    private void removeSelectedDetailFromInvoice() {
        if (selectedDetail != null) {
            invoice.remove(selectedDetail);
            clearInputs();
        }
    }

    private void fillItemPrices(Item item) {
        if (item == null) {
            comboListPrice.setModel(new DefaultComboBoxModel<>());
            return;
        }
        try {
            ArrayList<ItemPrice> prices = ItemPrice.listPrices(item.getItemId());
            comboListPrice.setModel(new DefaultComboBoxModel<>(prices.toArray()));
            comboListPrice.setRenderer(new DefaultPriceListRenderer());
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setActionFor(JComponent component, KeyStroke keyStroke, String actionMapKey, AbstractAction action) {

        component.getInputMap().put(keyStroke, actionMapKey);
        component.getActionMap().put(actionMapKey, action);
    }

    private class DefaultPriceListRenderer implements ListCellRenderer<ItemPrice> {

        private TextView tv;

        public DefaultPriceListRenderer() {
            tv = new TextView();
            tv.setOpaque(true);
            tv.setPreferredSize(new Dimension(1, 16));
            tv.setHorizontalTextPosition(TextView.RIGHT);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ItemPrice> list, ItemPrice value, int index, boolean isSelected, boolean cellHasFocus) {
            tv.setText(value.description());
            tv.setBackground(isSelected ? AppTheme.COLOR_MAIN : AppTheme.COLOR_WHITE);
            tv.setForeground(isSelected ? AppTheme.COLOR_WHITE : AppTheme.COLOR_GRAY);
            return tv;
        }
    }

}
