/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report.ui;

import entity.Contact;
import entity.Item;
import invoice.ui.CustomFocusTraversalPolicy;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import report.ReportCustomerItemBill;
import ui.container.GroupPane;
import ui.container.TabbedContainer;
import ui.controls.PressButton;
import ui.controls.input.SearchBox;
import ui.controls.input.SearchBoxEditor;

/**
 *
 * @author PersianDevStudio
 */
public class ReportPanelCustomerItemBill extends GroupPane {

    private Item item;

    private Contact contact;
    private ReportCustomerItemBill reportCustomerItemBill;
    private TabbedContainer tabbedPane;

    /**
     * Creates new form NewJPanel
     */
    public ReportPanelCustomerItemBill() {
        initComponents();

        pressButtonReport.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButtonReport.setTextVerticalPosition(PressButton.POSITION_CENTER);
        pressButtonReport.drawTextUnderline(true);

        pressButtonClear.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButtonClear.setTextVerticalPosition(PressButton.POSITION_CENTER);
        pressButtonClear.drawTextUnderline(true);

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

        ArrayList<Component> uiComponentOrder = new ArrayList<>();
        uiComponentOrder.add(inputDateFrom);
        uiComponentOrder.add(inputDateTo);
        uiComponentOrder.add(((SearchBoxEditor) searchBoxContact.getEditor()).getInputField());
        uiComponentOrder.add(inputFieldItemId);
        uiComponentOrder.add(((SearchBoxEditor) searchBoxItem.getEditor()).getInputField());
        uiComponentOrder.add(pressButtonReport);
        groupPane1.setFocusCycleRoot(true);
        groupPane1.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(uiComponentOrder));

        reportTable.setReport((reportCustomerItemBill = new ReportCustomerItemBill()));

        int index = 0;
        reportTable.setColumnPreferredWidth(index++, 50);
        reportTable.setColumnPreferredWidth(index++, 70);
        reportTable.setColumnPreferredWidth(index++, 80);
        reportTable.setColumnPreferredWidth(index++, 100);
        reportTable.setColumnPreferredWidth(index++, 80);
        reportTable.setColumnPreferredWidth(index++, 60);
        reportTable.setColumnPreferredWidth(index++, 80);
        reportTable.setColumnPreferredWidth(index++, 200);
        reportTable.setColumnPreferredWidth(index++, 40);
        reportTable.setColumnPreferredWidth(index++, 50);

        java.awt.EventQueue.invokeLater(() -> {
            searchBoxContact.requestFocusInWindow();
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupPane1 = new ui.container.GroupPane();
        searchBoxItem = new ui.controls.input.SearchBox();
        searchBoxContact = new ui.controls.input.SearchBox();
        inputDateTo = new ui.controls.input.InputFieldDate();
        inputDateFrom = new ui.controls.input.InputFieldDate();
        jSeparator1 = new javax.swing.JSeparator();
        inputFieldItemId = new ui.controls.input.InputFieldNumber();
        pressButtonClear = new ui.controls.PressButton();
        pressButtonReport = new ui.controls.PressButton();
        jSeparator2 = new javax.swing.JSeparator();
        reportTable = new report.ui.ReportTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout(5, 5));

        groupPane1.setPreferredSize(new java.awt.Dimension(100, 60));

        searchBoxItem.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        searchBoxContact.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مخاطب", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputDateTo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تا تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputDateFrom.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "از تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputDateFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputDateFromActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        inputFieldItemId.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شناسه کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldItemId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputFieldItemIdActionPerformed(evt);
            }
        });

        pressButtonClear.setText("پاک کردن");
        pressButtonClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressButtonClearMousePressed(evt);
            }
        });
        pressButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButtonClearActionPerformed(evt);
            }
        });

        pressButtonReport.setText("گزارش");
        pressButtonReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressButtonReportMousePressed(evt);
            }
        });
        pressButtonReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButtonReportActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout groupPane1Layout = new javax.swing.GroupLayout(groupPane1);
        groupPane1.setLayout(groupPane1Layout);
        groupPane1Layout.setHorizontalGroup(
            groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane1Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(searchBoxItem, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputFieldItemId, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBoxContact, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pressButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pressButtonReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        groupPane1Layout.setVerticalGroup(
            groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchBoxContact, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchBoxItem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputFieldItemId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pressButtonReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pressButtonClear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(groupPane1, java.awt.BorderLayout.NORTH);
        add(reportTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void pressButtonReportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressButtonReportMousePressed
        apply();
    }//GEN-LAST:event_pressButtonReportMousePressed

    private void pressButtonReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButtonReportActionPerformed
        apply();
    }//GEN-LAST:event_pressButtonReportActionPerformed

    private void pressButtonClearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressButtonClearMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pressButtonClearMousePressed

    private void pressButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButtonClearActionPerformed
        clearInputs();
    }//GEN-LAST:event_pressButtonClearActionPerformed

    private void inputFieldItemIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputFieldItemIdActionPerformed
        findItemWithId();
    }//GEN-LAST:event_inputFieldItemIdActionPerformed

    private void inputDateFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputDateFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputDateFromActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.container.GroupPane groupPane1;
    private ui.controls.input.InputFieldDate inputDateFrom;
    private ui.controls.input.InputFieldDate inputDateTo;
    private ui.controls.input.InputFieldNumber inputFieldItemId;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private ui.controls.PressButton pressButtonClear;
    private ui.controls.PressButton pressButtonReport;
    private report.ui.ReportTable reportTable;
    private ui.controls.input.SearchBox searchBoxContact;
    private ui.controls.input.SearchBox searchBoxItem;
    // End of variables declaration//GEN-END:variables

    private void setSelectedContact(Contact contact) {
        this.contact = contact;
        refreshTitle(tabbedPane);
    }

    private void setSelectedItem(Item item) {
        this.item = item;
        inputFieldItemId.setText(item == null ? "" : item.getItemId() + "");
        refreshTitle(tabbedPane);
    }

    private void apply() {

        reportCustomerItemBill.setDateStart(inputDateFrom.isValidInput() ? Long.parseLong(inputDateFrom.getText().replace("/", "")) : 0L);
        reportCustomerItemBill.setDateEnd(inputDateTo.isValidInput() ? Long.parseLong(inputDateTo.getText().replace("/", "")) : 0L);

        reportCustomerItemBill.setCustomerId(contact == null ? 0L : contact.getContactId());
        reportCustomerItemBill.setItemId(item == null ? 0L : item.getItemId());

        reportTable.apply();

    }

    private void clearInputs() {
        inputDateFrom.setText("");
        inputDateTo.setText("");
        inputFieldItemId.setText("");

        searchBoxContact.setSelectedItem(null);
        searchBoxItem.setSelectedItem(null);
        contact = null;

        item = null;

    }

    private void findItemWithId() {
        try {

            Item item = Item.find(Long.parseLong(inputFieldItemId.getText()));
            if (item == null) {
                JOptionPane.showMessageDialog(this, "کالای مورد نظر یافت نشد!", "پیام", JOptionPane.WARNING_MESSAGE);
            } else {
                setSelectedItem(item);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ورودی نا معبر است!", "پیام", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshTitle(TabbedContainer parent) {
        this.tabbedPane = parent;

        String title = "صورتحساب ";

        if (contact != null) {
            title += "/" + (contact.getName() + " " + contact.getLastname()).trim();
        } else {
            title += " مشتری";
        }
        if (item != null) {
            title += "/" + item.getDescription();
        } else {
            title += "/کالا";
        }

        this.tabbedPane.setTitleAt(this.tabbedPane.indexOfComponent(this), title.trim());
    }
}