/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.ui;

import application.DatabaseManager;
import application.JalaliCalendar;
import entity.Item;
import invoice.ui.CustomFocusTraversalPolicy;
import java.awt.Component;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import report.ReportItemPricing;
import ui.controls.PressButton;

/**
 *
 * @author PersianDevStudio
 */
public class PricingPanel extends javax.swing.JPanel {

    private PricingLevel[] defaultPricing = {
        new PricingLevel(1, "سطح 1"),
        new PricingLevel(2, "سطح 2"),
        new PricingLevel(3, "سطح 3"),
        new PricingLevel(4, "سطح 4"),
        new PricingLevel(5, "سطح 5")
    };

    private ReportItemPricing reportItemPricing;

    /**
     * Creates new form PricingPanel
     */
    public PricingPanel() {
        initComponents();

        ArrayList<Component> uiComponentOrder = new ArrayList<>();
        uiComponentOrder.add(itemPickPanel1.getSearchField());
        uiComponentOrder.add(inputFieldNumber1);
        uiComponentOrder.add(inputFieldDate1);
        uiComponentOrder.add(comboList1);
        uiComponentOrder.add(pressButton1);
        groupPane1.setFocusCycleRoot(true);
        groupPane1.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(uiComponentOrder));

        pressButton1.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton2.setTextHorizontalPosition(PressButton.POSITION_CENTER);

        comboList1.setModel(new DefaultComboBoxModel(defaultPricing));

        reportTable1.setReport((reportItemPricing = new ReportItemPricing()));

        reportTable1.setColumnMaxWidth(0, 50);

        reportTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        reportTable1.setNotificationEnabled(false);

        itemPickPanel1.setOnPick((Item item) -> {
            if (item != null) {
                reportItemPricing.setItemId(item.getItemId());
                reportTable1.apply();
            }
        });
        
        inputFieldDate1.setText(JalaliCalendar.now()+"");
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
        itemPickPanel1 = new application.ui.ItemPicker();
        groupPane2 = new ui.container.GroupPane();
        pressButton1 = new ui.controls.PressButton();
        comboList1 = new ui.controls.ComboList();
        inputFieldDate1 = new ui.controls.input.InputFieldDate();
        inputFieldNumber1 = new ui.controls.input.InputFieldNumber();
        pressButton2 = new ui.controls.PressButton();
        reportTable1 = new report.ui.ReportTable();

        setLayout(new java.awt.BorderLayout());

        groupPane1.setLayout(new java.awt.BorderLayout());

        itemPickPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        itemPickPanel1.setPreferredSize(new java.awt.Dimension(100, 45));
        groupPane1.add(itemPickPanel1, java.awt.BorderLayout.CENTER);

        pressButton1.setText("ثبت");
        pressButton1.setFont(new java.awt.Font("B Yekan", 0, 13)); // NOI18N
        pressButton1.setPreferredSize(new java.awt.Dimension(70, 28));
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });

        comboList1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "نوع قیمت", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        comboList1.setPreferredSize(new java.awt.Dimension(100, 39));

        inputFieldDate1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldDate1.setPreferredSize(new java.awt.Dimension(80, 39));

        inputFieldNumber1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "قیمت فروش", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldNumber1.setPreferredSize(new java.awt.Dimension(100, 39));

        pressButton2.setText("حذف");
        pressButton2.setFont(new java.awt.Font("B Yekan", 0, 13)); // NOI18N
        pressButton2.setPreferredSize(new java.awt.Dimension(70, 28));
        pressButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressButton2MousePressed(evt);
            }
        });

        javax.swing.GroupLayout groupPane2Layout = new javax.swing.GroupLayout(groupPane2);
        groupPane2.setLayout(groupPane2Layout);
        groupPane2Layout.setHorizontalGroup(
            groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(comboList1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputFieldDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        groupPane2Layout.setVerticalGroup(
            groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputFieldDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboList1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(groupPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        groupPane1.add(groupPane2, java.awt.BorderLayout.PAGE_END);

        add(groupPane1, java.awt.BorderLayout.NORTH);

        reportTable1.setBackground(new java.awt.Color(255, 255, 255));
        reportTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        reportTable1.setPreferredSize(new java.awt.Dimension(500, 442));
        add(reportTable1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        updatePrices(false);
    }//GEN-LAST:event_pressButton1ActionPerformed

    private void pressButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressButton2MousePressed
        updatePrices(true);
    }//GEN-LAST:event_pressButton2MousePressed

    private void updatePrices(boolean isDelete) {
        try {
            Item item = itemPickPanel1.getSelectedItem();
            long date = Long.parseLong(inputFieldDate1.getText().replace("/", ""));
            BigDecimal price = new BigDecimal(inputFieldNumber1.getText());
            int priceType = ((PricingLevel) comboList1.getSelectedItem()).id;

            CallableStatement call = DatabaseManager.instance.prepareCall("CALL item_price_update(?,?,?,?,?)");
            DatabaseManager.SetLong(call, 1, item.getItemId());
            DatabaseManager.SetInt(call, 2, priceType);
            DatabaseManager.SetBigDecimal(call, 3, price);
            DatabaseManager.SetLong(call, 4, date);
            DatabaseManager.SetBoolean(call, 5, isDelete);
            call.execute();
            call.close();

            reportTable1.apply();

        } catch (Exception ex) {
            NotificationManager.showError(this, ex.toString());
        }

    }

    public class PricingLevel {

        public int id;
        public String text;

        private PricingLevel(int id, String text) {
            this.id = id;
            this.text = text;

        }

        @Override
        public String toString() {
            return text;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.controls.ComboList comboList1;
    private ui.container.GroupPane groupPane1;
    private ui.container.GroupPane groupPane2;
    private ui.controls.input.InputFieldDate inputFieldDate1;
    private ui.controls.input.InputFieldNumber inputFieldNumber1;
    private application.ui.ItemPicker itemPickPanel1;
    private ui.controls.PressButton pressButton1;
    private ui.controls.PressButton pressButton2;
    private report.ui.ReportTable reportTable1;
    // End of variables declaration//GEN-END:variables
}
