/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.ui;

import application.Application;
import application.DatabaseManager;
import entity.Item;
import entity.ItemCategory;
import entity.Unit;
import application.Calculator;
import invoice.ui.CustomFocusTraversalPolicy;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale.Category;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import ui.controls.PressButton;

/**
 *
 * @author PersianDevStudio
 */
public class ItemEditor extends javax.swing.JPanel {

    private Item item;

    private ArrayList<ItemCategory> itemCategories;

    private ArrayList<Unit> units;

    public ItemEditor(Item item) {
        this.item = item;
        init();
    }

    /**
     * Creates new form ItemEditor
     */
    public ItemEditor() {
        init();
    }

    private void init() {
        initComponents();

        ArrayList<Component> uiComponentOrder = new ArrayList<>();
        uiComponentOrder.add(inputDescription);
        uiComponentOrder.add(comboListCategory);
        uiComponentOrder.add(inputUnit1);
        uiComponentOrder.add(inputRatio1);
        uiComponentOrder.add(inputUnit2);
        uiComponentOrder.add(pressButton1);
        groupPane1.setFocusCycleRoot(true);
        groupPane1.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(uiComponentOrder));

        java.awt.EventQueue.invokeLater(() -> {
            inputDescription.requestFocusInWindow();
        });

        pressButton1.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton2.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton3.setTextHorizontalPosition(PressButton.POSITION_CENTER);
        pressButton4.setTextHorizontalPosition(PressButton.POSITION_CENTER);

        inputFieldCategoryDescription.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputFieldNumber1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputDescription.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        pressButton1.drawTextUnderline(true);
        pressButton2.drawTextUnderline(true);
        pressButton3.drawTextUnderline(true);

        itemCategories = new ArrayList<>();
        units = new ArrayList<>();

        try {
            try (CallableStatement call = DatabaseManager.instance.prepareCall("CALL shopmanager.select_category_all_leaf()");
                    ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    ItemCategory cat = new ItemCategory();
                    cat.setCategoryId(rs.getLong("categoryId"));
                    cat.setCategoryParentId(rs.getLong("categoryParentId"));
                    cat.setDescription(rs.getString("description"));
                    itemCategories.add(cat);
                    createNewCategoryUI(cat);
                }
            }

            try (CallableStatement call = DatabaseManager.instance.prepareCall("CALL shopmanager.select_unit_all()");
                    ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    Unit unit = new Unit();
                    unit.setUnitId(rs.getInt("unitId"));
                    unit.setUnitName(rs.getString("unitName"));
                    units.add(unit);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ItemEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        refreshCategoryList();

        inputUnit1.setModel(new DefaultComboBoxModel(units.toArray()));
        inputUnit2.setModel(new DefaultComboBoxModel(units.toArray()));

        resetInputFields();

        try {
            CallableStatement call = DatabaseManager.instance.prepareCall("CALL select_catalog()");
            ResultSet rs = call.executeQuery();
            while (rs.next()) {
                //out : itemid , `categoryId` , itemDesc , unit1 , ratio1 , unit2 , catDesc 
                int paramIndex = 1;
                Item item = new Item();
                item.setItemId(rs.getLong(paramIndex++));
                item.setCategoryId(rs.getLong(paramIndex++));
                item.setDescription(rs.getString(paramIndex++));
                item.setUnit1(rs.getInt(paramIndex++));
                item.setRatio1(rs.getBigDecimal(paramIndex++));
                item.setUnit2(rs.getInt(paramIndex++));

                ItemCategory category = new ItemCategory();
                category.setDescription(rs.getString(paramIndex++));
                category.setCategoryId(item.getCategoryId());

                insertInUI(item, category);
            }
            rs.close();
            call.close();
        } catch (SQLException ex) {
            Logger.getLogger(ItemEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshCategoryList() {
        comboListCategory.setModel(new DefaultComboBoxModel(itemCategories.toArray()));
    }

    private ItemCategory findCategory(long catId) {
        for (ItemCategory itemCategory : itemCategories) {
            if (itemCategory.getCategoryId() == catId) {
                return itemCategory;
            }
        }
        return null;
    }

    private Unit findUnit(long unitId) {
        for (Unit unit : units) {
            if (unit.getUnitId() == unitId) {
                return unit;
            }
        }
        return null;
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
        groupPaneInput = new ui.container.GroupPane();
        inputDescription = new ui.controls.input.InputField();
        inputUnit1 = new ui.controls.ComboList();
        inputRatio1 = new ui.controls.input.InputFieldNumber();
        inputUnit2 = new ui.controls.ComboList();
        comboListCategory = new ui.controls.ComboList();
        inputFieldNumber1 = new ui.controls.input.InputFieldNumber();
        pressButton1 = new ui.controls.PressButton();
        pressButton2 = new ui.controls.PressButton();
        pressButton3 = new ui.controls.PressButton();
        groupPane4 = new ui.container.GroupPane();
        scroll1 = new ui.container.Scroll();
        groupPane6 = new ui.container.GroupPane();
        groupPane2 = new ui.container.GroupPane();
        inputFieldCategoryDescription = new ui.controls.input.InputField();
        pressButton4 = new ui.controls.PressButton();
        groupPane5 = new ui.container.GroupPane();
        catalogPanel = new ui.container.GroupPane();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(650, 352));
        setPreferredSize(new java.awt.Dimension(650, 352));
        setLayout(new java.awt.CardLayout());

        splitGroup1.setDividerLocation(300);

        groupPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        groupPaneInput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "تعریف", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شرح کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputUnit1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "واحد 1", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputRatio1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ضریب", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputRatio1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        inputUnit2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "واحد 2", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        comboListCategory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "دسته", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        inputFieldNumber1.setEditable(false);
        inputFieldNumber1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شناسه کالا", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldNumber1.setOpaque(false);

        javax.swing.GroupLayout groupPaneInputLayout = new javax.swing.GroupLayout(groupPaneInput);
        groupPaneInput.setLayout(groupPaneInputLayout);
        groupPaneInputLayout.setHorizontalGroup(
            groupPaneInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPaneInputLayout.createSequentialGroup()
                .addComponent(comboListCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPaneInputLayout.createSequentialGroup()
                .addGap(0, 104, Short.MAX_VALUE)
                .addGroup(groupPaneInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupPaneInputLayout.createSequentialGroup()
                        .addComponent(inputUnit2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputRatio1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputUnit1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(inputFieldNumber1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        groupPaneInputLayout.setVerticalGroup(
            groupPaneInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPaneInputLayout.createSequentialGroup()
                .addComponent(inputFieldNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(groupPaneInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboListCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupPaneInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputUnit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputRatio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputUnit2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pressButton1.setText("ثبت کالا");
        pressButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton1ActionPerformed(evt);
            }
        });

        pressButton2.setText("بازگردانی");
        pressButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton2ActionPerformed(evt);
            }
        });

        pressButton3.setText("فرم جدید");
        pressButton3.setActionCommand("asdas");
        pressButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupPane1Layout = new javax.swing.GroupLayout(groupPane1);
        groupPane1.setLayout(groupPane1Layout);
        groupPane1Layout.setHorizontalGroup(
            groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPaneInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(groupPane1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pressButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        groupPane1Layout.setVerticalGroup(
            groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupPaneInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(groupPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pressButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pressButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(213, Short.MAX_VALUE))
        );

        splitGroup1.setRightComponent(groupPane1);

        groupPane4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        groupPane4.setPreferredSize(new java.awt.Dimension(200, 361));
        groupPane4.setLayout(new java.awt.BorderLayout());

        scroll1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll1.setPreferredSize(new java.awt.Dimension(300, 100));

        groupPane6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        groupPane6.setLayout(new java.awt.BorderLayout());

        groupPane2.setPreferredSize(new java.awt.Dimension(266, 40));
        groupPane2.setLayout(new java.awt.BorderLayout());

        inputFieldCategoryDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "شرح", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        inputFieldCategoryDescription.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputFieldCategoryDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldCategoryDescriptionKeyPressed(evt);
            }
        });
        groupPane2.add(inputFieldCategoryDescription, java.awt.BorderLayout.CENTER);

        pressButton4.setText("ثبت گروه جدید");
        pressButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pressButton4ActionPerformed(evt);
            }
        });
        groupPane2.add(pressButton4, java.awt.BorderLayout.EAST);

        groupPane6.add(groupPane2, java.awt.BorderLayout.NORTH);

        groupPane5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        groupPane5.setLayout(new java.awt.BorderLayout());

        catalogPanel.setLayout(new javax.swing.BoxLayout(catalogPanel, javax.swing.BoxLayout.PAGE_AXIS));
        groupPane5.add(catalogPanel, java.awt.BorderLayout.NORTH);

        groupPane6.add(groupPane5, java.awt.BorderLayout.CENTER);

        scroll1.setViewportView(groupPane6);

        groupPane4.add(scroll1, java.awt.BorderLayout.CENTER);

        splitGroup1.setLeftComponent(groupPane4);

        add(splitGroup1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void pressButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton1ActionPerformed
        saveItem();
    }//GEN-LAST:event_pressButton1ActionPerformed

    private void pressButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton2ActionPerformed
        resetInputFields();
    }//GEN-LAST:event_pressButton2ActionPerformed

    private void pressButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton3ActionPerformed
        setSelectedItem(null);
    }//GEN-LAST:event_pressButton3ActionPerformed

    private void pressButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pressButton4ActionPerformed
        insertNewCategory();
    }//GEN-LAST:event_pressButton4ActionPerformed

    private void inputFieldCategoryDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldCategoryDescriptionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insertNewCategory();
        }
    }//GEN-LAST:event_inputFieldCategoryDescriptionKeyPressed

    private boolean inputIsValid() {
        boolean isValid = true;
        StringBuilder errorLog = new StringBuilder();
        if (inputDescription.getText().trim().isEmpty()) {
            errorLog.append("شرح کالا را وارد کنید. -").append(System.lineSeparator());
            isValid = false;
        }

        if (comboListCategory.getSelectedItem() == null) {
            isValid = false;
            errorLog.append("گروه کالا را انتخاب کنید. -").append(System.lineSeparator());
        }
        if (inputUnit1.getSelectedItem() == null) {
            isValid = false;
            errorLog.append("حداقل واحد شماره یک باید انتخاب شود. -").append(System.lineSeparator());
        }

        if (inputUnit2.getSelectedItem() != null && inputRatio1.getText().isEmpty()) {
            isValid = false;
            errorLog.append("ورودی ضریب را پر کنید. -").append(System.lineSeparator());
        }

        try {
            if (inputUnit2.getSelectedItem() != null) {
                BigDecimal bigDecimal = new BigDecimal(inputRatio1.getText());
                if (Calculator.isLessOrEqual(bigDecimal, BigDecimal.ZERO)) {
                    isValid = false;
                    errorLog.append("ضریب باید رقمی بزرگتر از صفر باشد. -").append(System.lineSeparator());
                }
            }
        } catch (Exception e) {
            isValid = false;
            errorLog.append("ورودی ضریب نادرست است. -").append(System.lineSeparator());
        }

        if (isValid == false) {
            showError(errorLog.toString());
        }
        return isValid;
    }

    private void saveItem() {
        if (item == null) {
            item = new Item();
        }

        if (inputIsValid() == false) {
            return;
        }

        try {
            item.setCategoryId(((ItemCategory) comboListCategory.getSelectedItem()).getCategoryId());
            item.setDescription(inputDescription.getText().trim());
            item.setUnit1(((Unit) inputUnit1.getSelectedItem()).getUnitId());

            if (inputUnit2.getSelectedItem() != null) {
                item.setRatio1(new BigDecimal(inputRatio1.getText()));
            } else {
                item.setRatio1(null);
            }

            if (inputUnit2.getSelectedItem() != null) {
                item.setUnit2(((Unit) inputUnit2.getSelectedItem()).getUnitId());
            } else {
                item.setUnit2(0);
            }

            CallableStatement call = DatabaseManager.instance.prepareCall("CALL shopmanager.item_save(?,?,?,?,?,?)");
            DatabaseManager.SetLong/*  */(call, 1, item.getItemId());
            DatabaseManager.SetLong/*  */(call, 2, item.getCategoryId());
            DatabaseManager.SetString/**/(call, 3, item.getDescription());
            DatabaseManager.SetInt/*   */(call, 4, item.getUnit1());
            DatabaseManager.SetBigDecimal(call, 5, item.getRatio1());
            DatabaseManager.SetInt/*   */(call, 6, item.getUnit2());

            call.execute();

            if (item.getItemId() == 0) {
                ExpandableCategoryList ui = allCats.get(item.getCategoryId());
                if (ui == null) {
                    ui = createNewCategoryUI(findCategory(item.getCategoryId()));
                }
                ui.addItem(item);
            } else {
                allCats.get(item.getCategoryId()).refresh();
            }

            setSelectedItem(null);
        } catch (Exception ex) {
            Logger.getLogger(ItemEditor.class.getName()).log(Level.SEVERE, null, ex);
            showError(ex.toString());
        }
    }

    private void showError(String message) throws HeadlessException {
        JOptionPane.showMessageDialog(this, message, "خطا", JOptionPane.ERROR_MESSAGE);
    }

    private void resetInputFields() {
        if (item != null) {
            inputFieldNumber1.setText(item.getItemId() + "");
            inputDescription.setText(item.getDescription());
            comboListCategory.setSelectedItem(findCategory(item.getCategoryId()));
            inputUnit1.setSelectedItem(findUnit(item.getUnit1()));

            if (item.getRatio1() != null) {
                inputRatio1.setText(item.getRatio1().stripTrailingZeros().toPlainString());
            } else {
                inputRatio1.setText("");
            }

            inputUnit2.setSelectedItem(findUnit(item.getUnit2()));
        } else {
            inputFieldNumber1.setText("");
            inputDescription.setText("");
//            comboListCategory.setSelectedItem(null);
            inputUnit1.setSelectedItem(null);
            inputRatio1.setText("");
            inputUnit2.setSelectedItem(null);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.container.GroupPane catalogPanel;
    private ui.controls.ComboList comboListCategory;
    private ui.container.GroupPane groupPane1;
    private ui.container.GroupPane groupPane2;
    private ui.container.GroupPane groupPane4;
    private ui.container.GroupPane groupPane5;
    private ui.container.GroupPane groupPane6;
    private ui.container.GroupPane groupPaneInput;
    private ui.controls.input.InputField inputDescription;
    private ui.controls.input.InputField inputFieldCategoryDescription;
    private ui.controls.input.InputFieldNumber inputFieldNumber1;
    private ui.controls.input.InputFieldNumber inputRatio1;
    private ui.controls.ComboList inputUnit1;
    private ui.controls.ComboList inputUnit2;
    private ui.controls.PressButton pressButton1;
    private ui.controls.PressButton pressButton2;
    private ui.controls.PressButton pressButton3;
    private ui.controls.PressButton pressButton4;
    private ui.container.Scroll scroll1;
    private ui.container.SplitGroup splitGroup1;
    // End of variables declaration//GEN-END:variables

    public static void open() {
        JDialog d = new JDialog(Application.instance, true);
        d.setTitle("مدیریت کالا و گروه ها");
        d.getContentPane().setLayout(new CardLayout());
        d.getContentPane().add(new ItemEditor());
        d.setSize(900, 600);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private void setSelectedItem(Item selectedValue) {
        item = selectedValue;
        resetInputFields();
    }

    private HashMap<Long, ExpandableCategoryList> allCats = new HashMap<>();

    private void insertInUI(Item item, ItemCategory category) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExpandableCategoryList catUI = allCats.get(category.getCategoryId());

                if (catUI == null) {
                    catUI = createNewCategoryUI(category);
                }
                catUI.addItem(item);
            }

        });

    }

    private ExpandableCategoryList createNewCategoryUI(ItemCategory category) {
        final ExpandableCategoryList lv_catUI = new ExpandableCategoryList();
        lv_catUI.setCategory(category);
        lv_catUI.getjListItem().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                setSelectedItem(lv_catUI.getjListItem().getSelectedValue());
            }
        });

        allCats.put(category.getCategoryId(), lv_catUI);
        catalogPanel.add(lv_catUI);
        catalogPanel.revalidate();
        catalogPanel.repaint();

        lv_catUI.setUpdateListener(() -> {
            findCategory(category.getCategoryId()).setDescription(lv_catUI.itemCategory.getDescription());
            refreshCategoryList();
        });
        return lv_catUI;
    }

    private void insertNewCategory() {
        String description = inputFieldCategoryDescription.getText().trim();
        if (description.isEmpty()) {
            showError("شرح گروه را وارد کنید.");
            return;
        }

        ItemCategory cat = new ItemCategory();
        cat.setDescription(description);

        try {

            cat.save();
            createNewCategoryUI(cat);
            itemCategories.add(cat);
            refreshCategoryList();
            inputFieldCategoryDescription.setText("");
            inputFieldCategoryDescription.requestFocusInWindow();
        } catch (SQLException ex) {
            showError(ex.toString());
        }

    }

}
