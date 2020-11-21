/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoice.ui;

import application.Calculator;
import entity.Contact;
import application.DatabaseManager;
import entity.Item;
import entity.invoice.Invoice;
import entity.invoice.InvoiceDetail;
import entity.invoice.InvoiceManager;
import entity.Payment;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import ui.container.TabbedContainer;
import ui.controls.PressButton;
import ui.controls.input.InputField;
import ui.controls.input.SearchBox;
import ui.controls.input.SearchBoxEditor;

/**
 *
 * @author PersianDevStudio
 */
public final class InvoiceEditor extends javax.swing.JPanel {

    public static final int MODE_INSERT = 1;
    public static final int MODE_UPDATE = 2;

    private Invoice invoice;

    private Item selectedItem;
    private Contact selectedContact;

    private InvoiceDetail selectedDetail;

    private String[] paymentTypes = {"POS", "صندوق"};
    private TabbedContainer parentTabbedPanel;

    /**
     * Creates new form InvoiceEditor
     */
    public InvoiceEditor() {
//        Invoice invoice = new Invoice();
//        invoice.setOperationType(Invoice.TYPE_BUY);
//        init(invoice);
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

        pressButton1.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton1.setTextVerticalPosition(PressButton.POSITION_CENTER);

        comboListPaymentTypes.setModel(new DefaultComboBoxModel(paymentTypes));

        SearchBox.setupSearchbox(Contact.class,
                searchBoxContact,
                "CALL search_contact(?)",
                (Object currentValue) -> {
                    setSelectedContact((Contact) currentValue);
                });
        SearchBox.setupSearchbox(Item.class,
                searchBoxItem,
                "CALL search_item(?)",
                (Object currentValue) -> {
                    setSelectedItem((Item) currentValue);
                });

        invoiceItemList.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting() == false) {
                int selectedRow = invoiceItemList.getSelectedRow();
                if (selectedRow >= 0) {
                    setSelectedDetail(invoice.get(selectedRow));
                }
            }
        });

        ArrayList<Component> uiComponentOrder = new ArrayList<>();
        uiComponentOrder.add(inputDate);
        uiComponentOrder.add(inputContact);
        uiComponentOrder.add(((SearchBoxEditor) searchBoxContact.getEditor()).getInputField());
        uiComponentOrder.add(((SearchBoxEditor) searchBoxItem.getEditor()).getInputField());
        uiComponentOrder.add(inputSuAmount);
        uiComponentOrder.add(inputPrice);
        uiComponentOrder.add(buttonInsert);
        paneBody.setFocusCycleRoot(true);
        paneBody.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(uiComponentOrder));

        jScrollPane2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        invoiceItemList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String contactTitle;
        InvoiceTableModelSetup.setupTableNormal(invoice, invoiceItemList, this);

        if (Invoice.inoutBuy(invoice.getOperationType())) {
            contactTitle = "فروشنده";
        } else {
            contactTitle = "خریدار";
        }
        ((TitledBorder) searchBoxContact.getBorder()).setTitle(contactTitle);
        ((TitledBorder) inputContact.getBorder()).setTitle("کد " + contactTitle);

        if (Invoice.isRefund(invoice.getOperationType())) {
            inputAreaLog.setVisible(true);
        } else {
            inputAreaLog.setVisible(false);
        }

        TitledBorder priceTitle = (TitledBorder) inputPrice.getBorder();
        if (Invoice.inoutBuy(invoice.getOperationType())) {
            priceTitle.setTitle("قیمت خرید");
        } else if (Invoice.inoutSell(invoice.getOperationType())) {
            priceTitle.setTitle("قیمت فروش");
        }

        invoice.setInvoiceUpdateListener(() -> {
            updateInvoiceSummery();
        });

        //Set initial invoice values
        inputDate.setText(invoice.getDate() + "");
        Contact contact = Contact.find(invoice.getContact());
        setSelectedContact(contact);
        searchBoxContact.setItem(selectedContact);

        for (Payment payment : invoice.getContactPayments()) {
            addPaymentMethod(payment);
        }
        updateInvoiceSummery();
        //Note itemdetails should already be inside inovicedetail and table
        //will automatically update
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

    public void setSelectedDetail(InvoiceDetail detail) {
        selectedDetail = detail;

        buttonDelete.setVisible(true);
        buttonCancel.setVisible(true);

        setSelectedItem(detail.getItem());
        searchBoxItem.setSelectedItem(selectedItem);

        inputSuAmount.setText(detail.getSuAmount().abs().stripTrailingZeros().toPlainString());
        inputPrice.setText(detail.getSuPrice().abs().stripTrailingZeros().toPlainString());

    }

    private boolean detailInputIsValid() {
        boolean isValid = true;
        StringBuilder errorLog = new StringBuilder();
        try {
            BigDecimal inputAmount = new BigDecimal(inputSuAmount.getText());
            if (inputAmount.compareTo(BigDecimal.ZERO) == -1 || inputAmount.equals(BigDecimal.ZERO)) {
                errorLog.append("مقدار وارد شده باید بیشتر از صفر باشد. -").append(System.lineSeparator());
                isValid = false;
            }
        } catch (Exception ex) {
            errorLog.append("مقدار وارد شده صحیح نیست. -").append(System.lineSeparator());
            isValid = false;
        }

        if (selectedItem == null) {
            errorLog.append("هیچ کالایی انتخاب نشده است. -").append(System.lineSeparator());
            isValid = false;
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(inputPrice.getText());
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
        if (selectedContact == null) {
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
            invoice.setContact(selectedContact.getContactId());

            if (invoice.getInvoiceId() == 0) {
                InvoiceManager.insert(invoice);
                refreshTitle(parentTabbedPanel);
            } else {
                if (InvoiceManager.update(invoice) == false) {
                    JOptionPane.showMessageDialog(this, "اعمال تغییرات امکان پذیر نمیباشد.", "خطا", JOptionPane.ERROR_MESSAGE);
                }

            }
        } catch (SQLException ex) {
            DatabaseManager.instance.Rollback();
            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
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
        if (selectedContact != null) {
            title += " - " + selectedContact.getName() + " " + selectedContact.getLastname();
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

        BigDecimal inputAmount = new BigDecimal(inputSuAmount.getText());
        //in case the operation is in editing mode leave stock check to the final step
        if (invoice.getInvoiceId() == 0) {
            if (Invoice.isExporting(invoice.getOperationType()) && selectedItem.getLastKnownStock().compareTo(inputAmount) == -1) {
                JOptionPane.showMessageDialog(this, "موجودی کافی نیست!", "خطا", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        InvoiceDetail invoiceDetail;
        if (selectedDetail != null) {
            invoiceDetail = selectedDetail;
        } else {
            invoiceDetail = new InvoiceDetail();
        }

        invoiceDetail.setItem(selectedItem);
        invoiceDetail.setSuAmount(inputAmount);
        invoiceDetail.setSuPrice(new BigDecimal(inputPrice.getText()));

        if (selectedDetail == null) {
            invoice.add(invoiceDetail);
        }

        clearInputs();
    }

    private void clearInputs() {
        EventQueue.invokeLater(() -> {
            buttonDelete.setVisible(false);
            buttonCancel.setVisible(false);

            selectedItem = null;
            selectedDetail = null;
            InputField inputFieldItem = ((SearchBoxEditor) searchBoxItem.getEditor()).getInputField();
            inputFieldItem.requestFocus();
            inputFieldItem.setText("");
            inputSuAmount.setText("");
            inputPrice.setText("");
            inputFieldStockId.setText("");
            invoiceItemList.clearSelection();
            invoiceItemList.revalidate();
            invoiceItemList.repaint();
        });
    }

    private void setSelectedItem(Item item) {
        try {
            this.selectedItem = item;
            if (this.selectedItem != null) {
                textViewStock.setText("موجودی : " + item.fetchItemStock().stripTrailingZeros().toPlainString());
                inputFieldStockId.setText(selectedItem.getItemId() + "");
                searchBoxItem.setItem(selectedItem);
            } else {
                textViewStock.setText("موجودی : -");
                inputFieldStockId.setText("");
                searchBoxItem.setItem(null);
            }
            refreshLatestTrades();
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setSelectedContact(Contact contact) {
        this.selectedContact = contact;
        if (contact != null) {
            inputContact.setText(contact.getContactId() + "");
        } else {
            inputContact.setText("");
        }
        if (parentTabbedPanel != null) {
            refreshTitle(parentTabbedPanel);
        }
        refreshLatestTrades();
    }

    private void refreshLatestTrades() {
        if (false == Invoice.isRefund(invoice.getOperationType())) {
            return;
        }
        if (selectedContact == null || selectedItem == null) {
            inputAreaLogEditor.setText("");
            return;
        }
        try {
            CallableStatement pc = DatabaseManager.instance.prepareCall("CALL search_contact_item(?,? ,?)");
            DatabaseManager.SetLong(pc, 1, selectedContact.getContactId());
            DatabaseManager.SetLong(pc, 2, selectedItem.getItemId());
            if (invoice.getOperationType() == Invoice.TYPE_REFUND_BUY) {
                DatabaseManager.SetInt(pc, 3, Invoice.TYPE_BUY);
            } else if (invoice.getOperationType() == Invoice.TYPE_REFUND_SELL) {
                DatabaseManager.SetInt(pc, 3, Invoice.TYPE_SELL);
            }
            ResultSet rs = pc.executeQuery();
            StringBuilder logText = new StringBuilder();
            logText.append(invoice.getOperationType() == Invoice.TYPE_REFUND_SELL ? "فروش های اخیر" : "خرید های اخیر")
                    .append(System.lineSeparator()).append(System.lineSeparator());
            int count = 0;
            while (rs.next()) {
                count++;
                InvoiceDetail detail = new InvoiceDetail();
                detail.readResultSet(rs);
                logText.append("- ").append(detail.getSummary()).append(System.lineSeparator()).append(System.lineSeparator());
            }
            if (count == 0) {
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
        pressButton1 = new ui.controls.PressButton();
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
        inputContact = new ui.controls.input.InputFieldNumber();
        searchBoxContact = new ui.controls.input.SearchBox();
        jSeparator2 = new javax.swing.JSeparator();
        searchBoxItem = new ui.controls.input.SearchBox();
        inputSuAmount = new ui.controls.input.InputFieldNumber();
        inputPrice = new ui.controls.input.InputFieldNumber();
        buttonInsert = new ui.controls.ImageButton();
        inputAreaLog = new javax.swing.JScrollPane();
        inputAreaLogEditor = new ui.controls.input.InputArea();
        textViewStock = new ui.controls.TextView();
        inputFieldStockId = new ui.controls.input.InputFieldNumber();
        buttonDelete = new ui.controls.ImageButton();
        buttonCancel = new ui.controls.ImageButton();
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
        textView1.setText("پرداخت ها");
        textView1.setFont(new java.awt.Font("B yekan+", 0, 18)); // NOI18N

        scroll1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        groupPane1.setLayout(new java.awt.BorderLayout());

        groupPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        groupPane2.setPreferredSize(new java.awt.Dimension(245, 40));
        groupPane2.setLayout(new java.awt.BorderLayout(5, 5));
        groupPane2.add(comboListPaymentTypes, java.awt.BorderLayout.CENTER);

        pressButton1.setText("افزودن");
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });
        groupPane2.add(pressButton1, java.awt.BorderLayout.EAST);

        groupPane1.add(groupPane2, java.awt.BorderLayout.NORTH);

        groupPane3.setLayout(new java.awt.BorderLayout());

        paymentMethodListUI.setLayout(new java.awt.GridLayout(0, 1));
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
                    .addComponent(scroll1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
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

        inputContact.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "کد مخاطب", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputContactActionPerformed(evt);
            }
        });

        searchBoxContact.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مشخصات مخاطب", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        searchBoxItem.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputSuAmount.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مقدار", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputPrice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "قیمت جزء (خرید/فروش)", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

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

        inputFieldStockId.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شناسه کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldStockId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldStockIdKeyPressed(evt);
            }
        });

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

        javax.swing.GroupLayout paneBodyLayout = new javax.swing.GroupLayout(paneBody);
        paneBody.setLayout(paneBodyLayout);
        paneBodyLayout.setHorizontalGroup(
            paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputAreaLog, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBodyLayout.createSequentialGroup()
                        .addGap(0, 50, Short.MAX_VALUE)
                        .addComponent(searchBoxContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputContact, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paneBodyLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paneBodyLayout.createSequentialGroup()
                                .addComponent(buttonInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textViewStock, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(paneBodyLayout.createSequentialGroup()
                                .addComponent(inputPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputSuAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBoxItem, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputFieldStockId, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        paneBodyLayout.setVerticalGroup(
            paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(inputContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(searchBoxContact, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputSuAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBoxItem, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputFieldStockId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addComponent(splitGroupMain, javax.swing.GroupLayout.DEFAULT_SIZE, 923, Short.MAX_VALUE)
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

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        addPaymentMethodEmpty();
    }//GEN-LAST:event_pressButton1ActionPerformed

    private void inputFieldStockIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldStockIdKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            findItemWithId();
        }

    }//GEN-LAST:event_inputFieldStockIdKeyPressed

    private void inputContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputContactActionPerformed
        setSelectedContactWithId();
    }//GEN-LAST:event_inputContactActionPerformed

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
        JComponent c = null;
        switch (selectedIndex) {
            case 0:
                c = new PaymentUISimple(invoice, Payment.BANK_ACCOUNT);
                break;
            case 1:
                c = new PaymentUISimple(invoice, Payment.CASH_BOX);
                break;
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
    private ui.container.GroupPane groupPane1;
    private ui.container.GroupPane groupPane2;
    private ui.container.GroupPane groupPane3;
    private ui.container.GroupPane groupPaneCenter;
    private ui.container.GroupPane groupPaneHeader;
    private ui.container.GroupPane groupPaneInvoice;
    private ui.container.GroupPane groupPanePayment;
    private ui.container.GroupPane groupPaneTableHolder;
    private javax.swing.JScrollPane inputAreaLog;
    private ui.controls.input.InputArea inputAreaLogEditor;
    private ui.controls.input.InputFieldNumber inputContact;
    private ui.controls.input.InputFieldDate inputDate;
    private ui.controls.input.InputFieldNumber inputFieldNumber1;
    private ui.controls.input.InputFieldNumber inputFieldStockId;
    private ui.controls.input.InputFieldNumber inputPrice;
    private ui.controls.input.InputFieldNumber inputSuAmount;
    private javax.swing.JTable invoiceItemList;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private ui.container.GroupPane paneBody;
    private ui.container.GroupPane paneHeader;
    private ui.container.GroupPane paymentMethodListUI;
    private ui.controls.PressButton pressButton1;
    private ui.container.Scroll scroll1;
    private ui.controls.input.SearchBox searchBoxContact;
    private ui.controls.input.SearchBox searchBoxItem;
    private ui.container.SplitGroup splitGroupMain;
    private ui.controls.TextView textView1;
    private ui.controls.TextView textViewInvoiceSummary;
    private ui.controls.TextView textViewStock;
    private ui.controls.TextView textViewTitle;
    // End of variables declaration//GEN-END:variables

    private void findItemWithId() {
        try {
            Item item = Item.find(Long.parseLong(inputFieldStockId.getText()));
            if (item == null) {
                JOptionPane.showMessageDialog(this, "هیچ کالایی با این شناسه پیدا نشد.", "پیغام", JOptionPane.WARNING_MESSAGE);
            } else {
                setSelectedItem(item);
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "کد کالا را وارد کنید.", "پیغام", JOptionPane.WARNING_MESSAGE);

        }
    }

    private void setSelectedContactWithId() {
        try {
            Contact find = Contact.find(Long.parseLong(inputContact.getText()));
            if (find == null) {
                JOptionPane.showMessageDialog(this, "هیچ مخاطبی با شناسه ی وارد شده یافت نشد.", "پیغام", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {

        }
    }

    private void removeSelectedDetailFromInvoice() {
        if (selectedDetail != null) {
            invoice.remove(selectedDetail);
            clearInputs();
        }
    }

}
