/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.ui;

import application.Application;
import entity.Contact;
import application.DatabaseManager;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import ui.container.GroupPane;
import ui.controls.PressButton;

/**
 *
 * @author Studio
 */
public class ContactEditor extends GroupPane {

    private Contact contact;

    /**
     * Creates new form CustomerEditor
     */
    public ContactEditor() {
        initComponents();
        splitGroup1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputSearch.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        inputId.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputName.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputLastname.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputFathername.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputNationalId.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputPhone.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputAddress.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        pressButton1.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton1.setTextVerticalPosition(PressButton.POSITION_CENTER);
        pressButton1.drawTextUnderline(true);

        pressButton2.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton2.setTextVerticalPosition(PressButton.POSITION_CENTER);
        pressButton2.drawTextUnderline(true);

        pressButton3.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton3.setTextVerticalPosition(PressButton.POSITION_CENTER);
        pressButton3.drawTextUnderline(true);

        setup();
        
    }

    public void setup() {
        loadContacts("");

        inputSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadContacts(inputSearch.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadContacts(inputSearch.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        
        createNew();
    }

    private void loadContacts(String pattern) {
        try {
            content.removeAll();

            CallableStatement call = DatabaseManager.instance.prepareCall("CALL search_contact(?);");
            DatabaseManager.SetString(call, 1, pattern);

            ResultSet rs = call.executeQuery();
            while (rs.next()) {
                Contact c = new Contact();
                c.readResultSet(rs);

                PressButton pressButton = new PressButton();
                pressButton.setText(c.toString());
                pressButton.setFont(getFont());
                pressButton.setHorizontalAlignment(SwingConstants.RIGHT);
                pressButton.drawBottomSeparator(true);
                pressButton.setPreferredSize(new Dimension(0, 45));
                pressButton.setFocusable(false);
                pressButton.addActionListener((ActionEvent e) -> {
                    contact = c;
                    resetInputFields();
                });
                content.add(pressButton);
            }
            rs.close();
            call.close();
            content.revalidate();
            content.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(ContactEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final Object SEARCH_LOCK = new Object();

    private void saveContactInfo() {
        try {

            contact.setName(inputName.getText());
            contact.setFathername(inputName.getText());
            contact.setLastname(inputLastname.getText());

            if (inputNationalId.getText().isEmpty()) {
                contact.setNationalId(0);
            } else {
                contact.setNationalId(Long.parseLong(inputNationalId.getText()));
            }

            contact.setPhone(inputPhone.getText());
            contact.setAddress(inputAddress.getText());

            CallableStatement call = DatabaseManager.instance.prepareCall("CALL contact_save(?,?,?,?,?,?,?)");
            call.registerOutParameter(1, Types.BIGINT);
            DatabaseManager.SetLong(call, 1, contact.getContactId(), false);
            DatabaseManager.SetLong(call, 2, contact.getNationalId());
            DatabaseManager.SetString(call, 3, contact.getAddress());
            DatabaseManager.SetString(call, 4, contact.getName());
            DatabaseManager.SetString(call, 5, contact.getLastname());
            DatabaseManager.SetString(call, 6, contact.getFathername());
            DatabaseManager.SetString(call, 7, contact.getPhone());

            call.execute();
            contact.setContactId(call.getLong(1));

            loadContacts("");
            resetInputFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetInputFields() {
        if (contact.getContactId() != 0) {
            inputId.setText(contact.getContactId() + "");
        } else {
            inputId.setText("");
        }

        inputName.setText(contact.getName());
        inputLastname.setText(contact.getLastname());
        inputFathername.setText(contact.getFathername());

        if (contact.getNationalId() != 0) {
            inputNationalId.setText(contact.getNationalId() + "");
        } else {
            inputNationalId.setText("");
        }

        inputPhone.setText(contact.getPhone());
        inputAddress.setText(contact.getAddress());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitGroup1 = new ui.container.SplitGroup();
        groupPane1 = new ui.container.GroupPane();
        scroll1 = new ui.container.Scroll();
        groupPane3 = new ui.container.GroupPane();
        content = new ui.container.GroupPane();
        inputSearch = new ui.controls.input.InputField();
        groupPane2 = new ui.container.GroupPane();
        groupPersonalInfo = new ui.container.GroupPane();
        inputId = new ui.controls.input.InputField();
        inputName = new ui.controls.input.InputField();
        inputLastname = new ui.controls.input.InputField();
        inputFathername = new ui.controls.input.InputField();
        inputNationalId = new ui.controls.input.InputField();
        groupContactInfo = new ui.container.GroupPane();
        inputPhone = new ui.controls.input.InputField();
        inputAddress = new ui.controls.input.InputField();
        pressButton1 = new ui.controls.PressButton();
        pressButton2 = new ui.controls.PressButton();
        pressButton3 = new ui.controls.PressButton();

        setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N

        splitGroup1.setDividerLocation(260);
        splitGroup1.setContinuousLayout(true);

        groupPane1.setLayout(new java.awt.BorderLayout());

        groupPane3.setLayout(new java.awt.BorderLayout());

        content.setLayout(new java.awt.GridLayout(0, 1));
        groupPane3.add(content, java.awt.BorderLayout.NORTH);

        scroll1.setViewportView(groupPane3);

        groupPane1.add(scroll1, java.awt.BorderLayout.CENTER);

        inputSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "جستجو", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 14))); // NOI18N
        inputSearch.setToolTipText("");
        inputSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputSearchKeyReleased(evt);
            }
        });
        groupPane1.add(inputSearch, java.awt.BorderLayout.NORTH);

        splitGroup1.setLeftComponent(groupPane1);

        groupPersonalInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "مشخصات فردی", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N

        inputId.setEditable(false);
        inputId.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "کد", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputId.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputId.setNextFocusableComponent(inputName);
        inputId.setOpaque(false);
        inputId.setPreferredSize(new java.awt.Dimension(92, 43));

        inputName.setBackground(new java.awt.Color(240, 240, 240));
        inputName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "نام", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputName.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputName.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputName.setNextFocusableComponent(inputLastname);
        inputName.setOpaque(false);
        inputName.setPreferredSize(new java.awt.Dimension(230, 43));

        inputLastname.setBackground(new java.awt.Color(240, 240, 240));
        inputLastname.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "نام و نام خانوادگی", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputLastname.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputLastname.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputLastname.setNextFocusableComponent(inputFathername);
        inputLastname.setOpaque(false);
        inputLastname.setPreferredSize(new java.awt.Dimension(230, 43));

        inputFathername.setBackground(new java.awt.Color(240, 240, 240));
        inputFathername.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "نام پدر", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputFathername.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputFathername.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputFathername.setNextFocusableComponent(inputNationalId);
        inputFathername.setOpaque(false);
        inputFathername.setPreferredSize(new java.awt.Dimension(169, 43));

        inputNationalId.setBackground(new java.awt.Color(240, 240, 240));
        inputNationalId.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شماره ملی", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputNationalId.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputNationalId.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputNationalId.setNextFocusableComponent(inputPhone);
        inputNationalId.setOpaque(false);
        inputNationalId.setPreferredSize(new java.awt.Dimension(170, 43));

        javax.swing.GroupLayout groupPersonalInfoLayout = new javax.swing.GroupLayout(groupPersonalInfo);
        groupPersonalInfo.setLayout(groupPersonalInfoLayout);
        groupPersonalInfoLayout.setHorizontalGroup(
            groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPersonalInfoLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPersonalInfoLayout.createSequentialGroup()
                        .addComponent(inputNationalId, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputFathername, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPersonalInfoLayout.createSequentialGroup()
                        .addComponent(inputLastname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        groupPersonalInfoLayout.setVerticalGroup(
            groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPersonalInfoLayout.createSequentialGroup()
                .addGroup(groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputId, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addGroup(groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(inputLastname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupPersonalInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputFathername, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(inputNationalId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        groupContactInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "اطلاعات تماس", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N

        inputPhone.setBackground(new java.awt.Color(240, 240, 240));
        inputPhone.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شماره تلفن", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputPhone.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputPhone.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputPhone.setOpaque(false);
        inputPhone.setPreferredSize(new java.awt.Dimension(170, 43));

        inputAddress.setBackground(new java.awt.Color(240, 240, 240));
        inputAddress.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "آدرس", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("B Yekan+", 0, 12))); // NOI18N
        inputAddress.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputAddress.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        inputAddress.setNextFocusableComponent(inputId);
        inputAddress.setOpaque(false);
        inputAddress.setPreferredSize(new java.awt.Dimension(169, 43));

        javax.swing.GroupLayout groupContactInfoLayout = new javax.swing.GroupLayout(groupContactInfo);
        groupContactInfo.setLayout(groupContactInfoLayout);
        groupContactInfoLayout.setHorizontalGroup(
            groupContactInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupContactInfoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(groupContactInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputPhone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputAddress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        groupContactInfoLayout.setVerticalGroup(
            groupContactInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupContactInfoLayout.createSequentialGroup()
                .addComponent(inputPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
        );

        pressButton1.setText("ثبت");
        pressButton1.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });

        pressButton2.setText("بازگردانی");
        pressButton2.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        pressButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton2ActionPerformed(evt);
            }
        });

        pressButton3.setText("جدید");
        pressButton3.setFont(new java.awt.Font("B Yekan+", 0, 14)); // NOI18N
        pressButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupPane2Layout = new javax.swing.GroupLayout(groupPane2);
        groupPane2.setLayout(groupPane2Layout);
        groupPane2Layout.setHorizontalGroup(
            groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupPane2Layout.createSequentialGroup()
                        .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pressButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(groupPersonalInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupContactInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        groupPane2Layout.setVerticalGroup(
            groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupPersonalInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupContactInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pressButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(170, Short.MAX_VALUE))
        );

        splitGroup1.setRightComponent(groupPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        saveContactInfo();
    }//GEN-LAST:event_pressButton1ActionPerformed

    private void pressButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton2ActionPerformed
        resetInputFields();
    }//GEN-LAST:event_pressButton2ActionPerformed

    private void inputSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputSearchKeyReleased
//        loadContacts(inputSearch.getText());
    }//GEN-LAST:event_inputSearchKeyReleased

    private void pressButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton3ActionPerformed
        createNew();
    }//GEN-LAST:event_pressButton3ActionPerformed

    private void createNew() {
        contact = new Contact();
        resetInputFields();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.container.GroupPane content;
    private ui.container.GroupPane groupContactInfo;
    private ui.container.GroupPane groupPane1;
    private ui.container.GroupPane groupPane2;
    private ui.container.GroupPane groupPane3;
    private ui.container.GroupPane groupPersonalInfo;
    private ui.controls.input.InputField inputAddress;
    private ui.controls.input.InputField inputFathername;
    private ui.controls.input.InputField inputId;
    private ui.controls.input.InputField inputLastname;
    private ui.controls.input.InputField inputName;
    private ui.controls.input.InputField inputNationalId;
    private ui.controls.input.InputField inputPhone;
    private ui.controls.input.InputField inputSearch;
    private ui.controls.PressButton pressButton1;
    private ui.controls.PressButton pressButton2;
    private ui.controls.PressButton pressButton3;
    private ui.container.Scroll scroll1;
    private ui.container.SplitGroup splitGroup1;
    // End of variables declaration//GEN-END:variables

    public static void open() {
        JDialog d = new JDialog(Application.instance, true);
        d.setTitle("مدیریت مخاطبین");
        d.getContentPane().setLayout(new CardLayout());
        d.getContentPane().add(new ContactEditor());
        d.pack();
        d.setSize(d.getWidth(), (int) (Application.instance.getHeight() * 0.80));
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
}
