/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report.ui;

import application.ui.ItemPicker;
import entity.Item;
import report.ReportPerItemProfit;

/**
 *
 * @author PersianDevStudio
 */
public class ReportPanelPerItemProfit extends javax.swing.JPanel {

    private ReportPerItemProfit reportPerItemProfit;

    /**
     * Creates new form ReportPanelPerItemProfit
     */
    public ReportPanelPerItemProfit() {
        initComponents();

        reportPerItemProfit = new ReportPerItemProfit();
        reportTable1.setReport(reportPerItemProfit);

        reportTable1.setColumnPreferredWidth(0, 50);
        reportTable1.setColumnPreferredWidth(1, 150);
        reportTable1.setColumnPreferredWidth(2, 300);
        reportTable1.setColumnPreferredWidth(3, 100);
        reportTable1.setColumnPreferredWidth(4, 100);
        reportTable1.setColumnPreferredWidth(5, 100);
        reportTable1.setColumnPreferredWidth(6, 100);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        reportTable1 = new report.ui.ReportTable();
        groupPane1 = new ui.container.GroupPane();
        inputFieldDateEnd = new ui.controls.input.InputFieldDate();
        inputFieldDateStart = new ui.controls.input.InputFieldDate();
        jSeparator1 = new javax.swing.JSeparator();
        itemPicker1 = new application.ui.ItemPicker();
        pressButton1 = new ui.controls.PressButton();
        pressButton2 = new ui.controls.PressButton();

        setLayout(new java.awt.BorderLayout());
        add(reportTable1, java.awt.BorderLayout.CENTER);

        groupPane1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        inputFieldDateEnd.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تا تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        groupPane1.add(inputFieldDateEnd);

        inputFieldDateStart.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "از تاریخ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        groupPane1.add(inputFieldDateStart);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, 30));
        groupPane1.add(jSeparator1);
        groupPane1.add(itemPicker1);

        pressButton1.setText("اعمال");
        pressButton1.setPreferredSize(new java.awt.Dimension(59, 30));
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });
        groupPane1.add(pressButton1);

        pressButton2.setText("پاک کردن");
        pressButton2.setPreferredSize(new java.awt.Dimension(59, 30));
        pressButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton2ActionPerformed(evt);
            }
        });
        groupPane1.add(pressButton2);

        add(groupPane1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        loadReport();
    }//GEN-LAST:event_pressButton1ActionPerformed

    private void pressButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton2ActionPerformed
        clearInput();
    }//GEN-LAST:event_pressButton2ActionPerformed
    
    private void clearInput() {
        inputFieldDateStart.setText("");
        inputFieldDateEnd.setText("");
        itemPicker1.setSelectedItem(null);
    }

    private void loadReport() {

        reportPerItemProfit.setItemId(itemPicker1.getSelectedItem() == null ? 0 : itemPicker1.getSelectedItem().getItemId());

        if (inputFieldDateStart.isValidInput()) {
            reportPerItemProfit.setDateStart(Long.parseLong(inputFieldDateStart.getText().replace("/", "")));
        } else {
            reportPerItemProfit.setDateStart(0);
        }

        if (inputFieldDateEnd.isValidInput()) {
            reportPerItemProfit.setDateStart(Long.parseLong(inputFieldDateEnd.getText().replace("/", "")));
        } else {
            reportPerItemProfit.setDateStart(0);
        }

        reportTable1.apply();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.container.GroupPane groupPane1;
    private ui.controls.input.InputFieldDate inputFieldDateEnd;
    private ui.controls.input.InputFieldDate inputFieldDateStart;
    private application.ui.ItemPicker itemPicker1;
    private javax.swing.JSeparator jSeparator1;
    private ui.controls.PressButton pressButton1;
    private ui.controls.PressButton pressButton2;
    private report.ui.ReportTable reportTable1;
    // End of variables declaration//GEN-END:variables

}
