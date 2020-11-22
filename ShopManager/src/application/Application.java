/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import entity.invoice.Invoice;
import invoice.ui.InvoiceEditor;
import application.ui.ContactEditor;
import application.ui.ItemEditor;
import entity.invoice.InvoiceManager;
import java.awt.ComponentOrientation;
import javax.swing.UIManager;

import invoice.ui.InvoiceIdInputDialog;
import application.ui.ItemPricingDialog;
import report.ui.ReportPanelCustomerItemBill;
import report.ui.ReportPanelPaymentBill;

/**
 *
 * @author PersianDevStudio
 */
public class Application extends javax.swing.JFrame {

    public static Application instance;

    /**
     * Creates new form Application
     */
    public Application() {
        initComponents();

        tabbedContainerHeader.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tabbedContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tabbedContainer.setClosable(true);

        instance = this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedContainerHeader = new ui.container.TabbedContainer();
        panelStoreAndItems = new ui.container.GroupPane();
        buttonPriceAnnounce = new ui.controls.ImageButton();
        buttonDefineItem = new ui.controls.ImageButton();
        buttonContacts = new ui.controls.ImageButton();
        buttonUser = new ui.controls.ImageButton();
        panelBuy = new ui.container.GroupPane();
        buttonEditBuy = new ui.controls.ImageButton();
        jSeparator2 = new javax.swing.JSeparator();
        ButtonRefundSell = new ui.controls.ImageButton();
        buttonSell = new ui.controls.ImageButton();
        jSeparator1 = new javax.swing.JSeparator();
        buttonRefundBuy = new ui.controls.ImageButton();
        buttonBuy = new ui.controls.ImageButton();
        panelReports = new ui.container.GroupPane();
        buttonBuy2 = new ui.controls.ImageButton();
        buttonBuy1 = new ui.controls.ImageButton();
        mainPanel = new ui.container.GroupPane();
        tabbedContainer = new ui.container.TabbedContainer();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedContainerHeader.setMinimumSize(new java.awt.Dimension(105, 110));
        tabbedContainerHeader.setPreferredSize(new java.awt.Dimension(100, 120));

        panelStoreAndItems.setPreferredSize(new java.awt.Dimension(690, 110));
        panelStoreAndItems.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        buttonPriceAnnounce.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonPriceAnnounce.setLabel("اعلامیه قیمت");
        buttonPriceAnnounce.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonPriceAnnounce.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonPriceAnnounceMousePressed(evt);
            }
        });
        panelStoreAndItems.add(buttonPriceAnnounce);

        buttonDefineItem.setText("مدیریت کالا ها");
        buttonDefineItem.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonDefineItem.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonDefineItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonDefineItemMousePressed(evt);
            }
        });
        panelStoreAndItems.add(buttonDefineItem);

        buttonContacts.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonContacts.setLabel("مخاطبین");
        buttonContacts.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonContacts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonContactsMousePressed(evt);
            }
        });
        panelStoreAndItems.add(buttonContacts);
        buttonContacts.getAccessibleContext().setAccessibleName("buttonContact");
        buttonContacts.getAccessibleContext().setAccessibleDescription("");

        buttonUser.setText("کاربران");
        buttonUser.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonUser.setPreferredSize(new java.awt.Dimension(70, 70));
        panelStoreAndItems.add(buttonUser);

        tabbedContainerHeader.addTab("تعریف حسابها", panelStoreAndItems);

        panelBuy.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        buttonEditBuy.setText("اصلاح رسید");
        buttonEditBuy.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonEditBuy.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonEditBuy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonEditBuyMousePressed(evt);
            }
        });
        panelBuy.add(buttonEditBuy);

        jSeparator2.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setPreferredSize(new java.awt.Dimension(2, 70));
        panelBuy.add(jSeparator2);

        ButtonRefundSell.setText("برگشت فروش");
        ButtonRefundSell.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        ButtonRefundSell.setPreferredSize(new java.awt.Dimension(70, 70));
        ButtonRefundSell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ButtonRefundSellMousePressed(evt);
            }
        });
        panelBuy.add(ButtonRefundSell);

        buttonSell.setText("حواله فروش");
        buttonSell.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonSell.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonSell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonSellMousePressed(evt);
            }
        });
        panelBuy.add(buttonSell);

        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, 70));
        panelBuy.add(jSeparator1);

        buttonRefundBuy.setText("برگشت خرید");
        buttonRefundBuy.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonRefundBuy.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonRefundBuy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonRefundBuyMousePressed(evt);
            }
        });
        panelBuy.add(buttonRefundBuy);

        buttonBuy.setText("رسید خرید");
        buttonBuy.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonBuy.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonBuy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonBuyMousePressed(evt);
            }
        });
        panelBuy.add(buttonBuy);

        tabbedContainerHeader.addTab("خرید/فروش", panelBuy);

        panelReports.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        buttonBuy2.setText("مشتری / کالا");
        buttonBuy2.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonBuy2.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonBuy2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonBuy2MousePressed(evt);
            }
        });
        panelReports.add(buttonBuy2);

        buttonBuy1.setText("صورتحساب");
        buttonBuy1.setFont(new java.awt.Font("B Yekan", 0, 12)); // NOI18N
        buttonBuy1.setPreferredSize(new java.awt.Dimension(70, 70));
        buttonBuy1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonBuy1MousePressed(evt);
            }
        });
        panelReports.add(buttonBuy1);
        buttonBuy1.getAccessibleContext().setAccessibleName("button2");

        tabbedContainerHeader.addTab("گزارشات", panelReports);

        getContentPane().add(tabbedContainerHeader, java.awt.BorderLayout.NORTH);
        tabbedContainerHeader.getAccessibleContext().setAccessibleName("tab2");
        tabbedContainerHeader.getAccessibleContext().setAccessibleDescription("");

        mainPanel.setLayout(new java.awt.CardLayout());
        mainPanel.add(tabbedContainer, "card2");

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSellMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonSellMousePressed
        openInvoiceSell();
    }//GEN-LAST:event_buttonSellMousePressed

    private void ButtonRefundSellMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonRefundSellMousePressed
        openInvoiceRefundSell();
    }//GEN-LAST:event_ButtonRefundSellMousePressed

    private void buttonBuyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBuyMousePressed
        openInvoiceBuy();
    }//GEN-LAST:event_buttonBuyMousePressed

    private void buttonRefundBuyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRefundBuyMousePressed
        openInvoiceRefundBuy();
    }//GEN-LAST:event_buttonRefundBuyMousePressed

    private void buttonContactsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonContactsMousePressed
        ContactEditor.open();
    }//GEN-LAST:event_buttonContactsMousePressed

    private void buttonDefineItemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonDefineItemMousePressed
        ItemEditor.open();
    }//GEN-LAST:event_buttonDefineItemMousePressed

    private void buttonEditBuyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonEditBuyMousePressed
        openInvoiceForEditing();
    }//GEN-LAST:event_buttonEditBuyMousePressed

    private void buttonBuy2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBuy2MousePressed
        openContactItemBillPanel();
    }//GEN-LAST:event_buttonBuy2MousePressed

    private void buttonBuy1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBuy1MousePressed
        openPaymentBillPanel();
    }//GEN-LAST:event_buttonBuy1MousePressed

    private void buttonPriceAnnounceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonPriceAnnounceMousePressed
        ItemPricingDialog.open();
    }//GEN-LAST:event_buttonPriceAnnounceMousePressed

    private void openInvoiceBuy() {
        openInvoicePanel(new Invoice(Invoice.TYPE_BUY));
    }

    private void openInvoiceSell() {
        openInvoicePanel(new Invoice(Invoice.TYPE_SELL));
    }

    private void openInvoiceRefundBuy() {
        openInvoicePanel(new Invoice(Invoice.TYPE_REFUND_BUY));
    }

    private void openInvoiceRefundSell() {
        openInvoicePanel(new Invoice(Invoice.TYPE_REFUND_SELL));
    }

    public void openInvoicePanel(long invoiceId) {
        Invoice i = InvoiceManager.find(invoiceId);
        openInvoicePanel(i);
    }

    private void openInvoicePanel(Invoice invoice) {
        InvoiceEditor invoiceEditor = new InvoiceEditor(invoice);
        tabbedContainer.add(invoiceEditor);
        invoiceEditor.refreshTitle(tabbedContainer);
        tabbedContainer.setSelectedIndex(tabbedContainer.getComponentCount() - 1);
        tabbedContainer.revalidate();
        tabbedContainer.repaint();
    }

    private void openContactItemBillPanel() {
        ReportPanelCustomerItemBill reportPanel = new ReportPanelCustomerItemBill();
        tabbedContainer.add(reportPanel);
        reportPanel.refreshTitle(tabbedContainer);
        tabbedContainer.setSelectedIndex(tabbedContainer.getComponentCount() - 1);
        tabbedContainer.revalidate();
        tabbedContainer.repaint();
    }

    private void openPaymentBillPanel() {
        ReportPanelPaymentBill reportPanel = new ReportPanelPaymentBill();
        tabbedContainer.add(reportPanel);
        reportPanel.refreshTitle(tabbedContainer);
        tabbedContainer.setSelectedIndex(tabbedContainer.getComponentCount() - 1);
        tabbedContainer.revalidate();
        tabbedContainer.repaint();
    }

    private void openInvoiceForEditing() {
        Invoice invoice = InvoiceIdInputDialog.open();
        if (invoice != null) {
            openInvoicePanel(invoice);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                }
//            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                DatabaseManager.instance.StartUp();

                Application application = new Application();
                application.setSize(1000, 750);
                application.setLocationRelativeTo(null);
                application.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.controls.ImageButton ButtonRefundSell;
    private ui.controls.ImageButton buttonBuy;
    private ui.controls.ImageButton buttonBuy1;
    private ui.controls.ImageButton buttonBuy2;
    private ui.controls.ImageButton buttonContacts;
    private ui.controls.ImageButton buttonDefineItem;
    private ui.controls.ImageButton buttonEditBuy;
    private ui.controls.ImageButton buttonPriceAnnounce;
    private ui.controls.ImageButton buttonRefundBuy;
    private ui.controls.ImageButton buttonSell;
    private ui.controls.ImageButton buttonUser;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private ui.container.GroupPane mainPanel;
    private ui.container.GroupPane panelBuy;
    private ui.container.GroupPane panelReports;
    private ui.container.GroupPane panelStoreAndItems;
    private ui.container.TabbedContainer tabbedContainer;
    private ui.container.TabbedContainer tabbedContainerHeader;
    // End of variables declaration//GEN-END:variables

}
