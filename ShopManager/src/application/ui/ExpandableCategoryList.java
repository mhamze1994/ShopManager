/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.ui;

import entity.Item;
import entity.ItemCategory;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import ui.AppTheme;
import ui.controls.TextView;

/**
 *
 * @author PersianDevStudio
 */
public class ExpandableCategoryList extends javax.swing.JPanel {

    public ItemCategory itemCategory;

    private ItemListModel itemListModel;

    public void setCategory(ItemCategory category) {
        itemCategory = category;
        inputField1.setText(itemCategory.getDescription());

        itemListModel = new ItemListModel();
        jListItem.setModel(itemListModel);
        jListItem.setCellRenderer(new ItemListRenderer());

        inputField1.setBackground(AppTheme.COLOR_MAIN);
        inputField1.setForeground(AppTheme.COLOR_WHITE);

    }

    public JList<Item> getjListItem() {
        return jListItem;
    }

    public void setjListItem(JList<Item> jListItem) {
        this.jListItem = jListItem;
    }

    public void refresh() {
        itemListModel.refresh();
    }

    public void addItem(Item item) {
        itemListModel.addElement(item);

//        itemsPane.add(btn);
//        itemsPane.revalidate();
//        itemsPane.repaint();
    }

    /**
     * Creates new form CategoryExpandable
     */
    public ExpandableCategoryList() {
        initComponents();

        inputField1.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputField1 = new ui.controls.input.InputField();
        jListItem = new javax.swing.JList<>();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        inputField1.setEditable(false);
        inputField1.setBackground(new java.awt.Color(102, 102, 255));
        inputField1.setForeground(new java.awt.Color(255, 255, 255));
        inputField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputField1.setText("عنوان گروه");
        inputField1.setFocusable(false);
        inputField1.setPreferredSize(new java.awt.Dimension(200, 30));
        inputField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                inputField1FocusLost(evt);
            }
        });
        inputField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                inputField1MousePressed(evt);
            }
        });
        inputField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputField1KeyPressed(evt);
            }
        });
        add(inputField1, java.awt.BorderLayout.NORTH);

        jListItem.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        add(jListItem, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void inputField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputField1MousePressed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (evt.getClickCount() == 1) {
                    toggle();
                } else {
                    enableEditing();
                }
            }

        });
    }//GEN-LAST:event_inputField1MousePressed

    private void inputField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_inputField1FocusLost
        commitChanges();
    }//GEN-LAST:event_inputField1FocusLost

    private void inputField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            commitChanges();
        }
    }//GEN-LAST:event_inputField1KeyPressed

    private void enableEditing() {
        inputField1.setEditable(true);
        inputField1.setFocusable(true);
        inputField1.requestFocus();
        inputField1.setSelectionStart(0);
        inputField1.setSelectionEnd(inputField1.getText().length());

        inputField1.setBackground(AppTheme.COLOR_WHITE);
        inputField1.setForeground(AppTheme.COLOR_TEXT_DEFAULT);
    }

    private void commitChanges() {
        try {
            if (inputField1.getText().trim().isEmpty()) {
                inputField1.setText(itemCategory.getDescription());
            } else {
                itemCategory.setDescription(inputField1.getText());

                itemCategory.save();
            }
            inputField1.setEditable(false);
            inputField1.setFocusable(false);

            inputField1.setBackground(AppTheme.COLOR_MAIN);
            inputField1.setForeground(AppTheme.COLOR_WHITE);

            if (updateListener != null) {
                updateListener.update();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "خطا", JOptionPane.ERROR_MESSAGE);
        }
    }

    private UpdateListener updateListener;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public interface UpdateListener {

        public void update();
    }

    private void toggle() {
        itemListModel.toggle();

        getParent().revalidate();
        getParent().repaint();
        revalidate();
        repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.controls.input.InputField inputField1;
    private javax.swing.JList<Item> jListItem;
    // End of variables declaration//GEN-END:variables

    private class ItemListModel extends DefaultListModel<Item> {

        public ArrayList<Item> items = new ArrayList<>();

        public boolean isOpen = false;

        public ItemListModel() {

        }

        @Override
        public int getSize() {
            if (isOpen == false) {
                return 0;
            }
            return items.size();
        }

        @Override
        public Item getElementAt(int index) {
            return items.get(index);
        }

        @Override
        public void addElement(Item element) {
            items.add(element);
            fireIntervalAdded(this, getSize(), getSize());
        }

        public void hide() {
            isOpen = false;
            fireContentsChanged(this, 0, items.size());
        }

        public void show() {
            isOpen = true;
            fireContentsChanged(this, 0, items.size());

        }

        private void toggle() {
            if (isOpen) {
                hide();
                isOpen = false;
            } else {
                show();
                isOpen = true;
            }
        }

        private void refresh() {
            fireContentsChanged(this, 0, items.size());
        }

    }

    private class ItemListRenderer implements ListCellRenderer<Item> {

        private TextView tv;

        public ItemListRenderer() {
            tv = new TextView();
            tv.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tv.setPreferredSize(new Dimension(1, 30));
            tv.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index, boolean isSelected, boolean cellHasFocus) {
            tv.setText(value.getDescription());
            if (isSelected) {
                tv.setBackground(AppTheme.COLOR_MAIN_2);
//                tv.setForeground(AppTheme.COLOR_WHITE);
            } else {
                tv.setBackground(AppTheme.COLOR_WHITE);
                tv.setForeground(AppTheme.COLOR_TEXT_DEFAULT);
            }
            return tv;
        }

    }
}
