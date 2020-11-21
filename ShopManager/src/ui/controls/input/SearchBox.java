/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import application.DatabaseManager;
import application.SearchBoxQueryModel;
import entity.AbstractEntity;
import invoice.ui.InvoiceEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import ui.SearchBoxUI;
import ui.controls.ComboList;
import ui.controls.TextView;

/**
 *
 * @author PersianDevStudio
 */
public final class SearchBox extends ComboList {

    private SearchBoxEditor editor;

    private JList list;

    private SearchBoxUI searchBoxUI;

    private Object selectedItem;

    private OnRequestDataUpdate onRequestDataUpdate;

    private OnValueChange onValueChange;

    public SearchBox() {

        Dimension d = getPreferredSize();
        d.width = 200;
        d.height = 27;
        setPreferredSize(d);
        setEditable(true);

        searchBoxUI = new SearchBoxUI();
        setUI(searchBoxUI);

        list = searchBoxUI.getList();
        editor = (SearchBoxEditor) getEditor();

        list.setCellRenderer(new DefaultListCellRendererImpl());

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (Character.isAlphabetic(e.getKeyChar())
                        || Character.isDigit(e.getKeyChar())
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    requestModelUpdate(editor.getItem());
                }
            }

        });

        list.addListSelectionListener((ListSelectionEvent e) -> {
            selectedItem = list.getSelectedValue();
        });

        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                updateEditor();

                if (onValueChange != null) {
                    onValueChange.onValueChange(selectedItem);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        list.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    }

    private void updateEditor() {
        if (selectedItem != null) {
            editor.setItem(selectedItem);
        } else {
            editor.setItem("");
        }
        revalidate();
        repaint();
    }

    public void setItem(Object selectedItem) {
        this.selectedItem = selectedItem;

        updateEditor();

    }

    public void setOnRequestDataUpdate(OnRequestDataUpdate onRequestDataUpdate) {
        this.onRequestDataUpdate = onRequestDataUpdate;
    }

    public void setOnValueChange(OnValueChange onValueChange) {
        this.onValueChange = onValueChange;
    }

    private void requestModelUpdate(Object item) {
        if (onRequestDataUpdate != null) {
            onRequestDataUpdate.onRequestDataUpdate(item);
            if (item != null) {

                hidePopup();
                editor.setItem(item);
                editor.setCaretPosition(item.toString().length());

                if (item.toString().isEmpty() == false) {
                    showPopup();

                }
            }
        }
    }

    public static <T extends AbstractEntity> void setupSearchbox(Class clazz, SearchBox searchBox, String callStatement, SearchBox.OnValueChange onValueChange) {
        SearchBoxQueryModel<T> queryModel = new SearchBoxQueryModel(clazz);
        searchBox.setModel(queryModel);
        searchBox.setOnValueChange(onValueChange);

        searchBox.setOnRequestDataUpdate((Object item) -> {
            if (item != null) {
                try {
                    CallableStatement call = DatabaseManager.instance.prepareCall(callStatement);
                    DatabaseManager.SetString(call, 1, item.toString());
                    queryModel.update(call);
                } catch (SQLException ex) {
                    Logger.getLogger(InvoiceEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public interface OnValueChange {

        public void onValueChange(Object currentValue);

    }

    public interface OnRequestDataUpdate {

        public void onRequestDataUpdate(Object editorInput);
    }

    private class DefaultListCellRendererImpl extends DefaultListCellRenderer {

        private TextView tv = new TextView();
        private Color hightlightColor = new Color(97, 137, 205);

        public DefaultListCellRendererImpl() {
            tv.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            tv.setPreferredSize(new Dimension(10, 25));
            tv.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            tv.setText(String.valueOf(value));
            if (isSelected) {
                tv.setBackground(hightlightColor);
                tv.setForeground(Color.WHITE);
            } else {
                tv.setBackground(Color.WHITE);
                tv.setForeground(Color.DARK_GRAY.darker());
            }
            return tv;
        }
    }
}
