/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import ui.controls.ComboList;

/**
 *
 * @author Studio
 */
public final class AutoCompleteComboBox extends ComboList {

    public int caretPos = 0;
    public JTextField tfield = null;
    public JList list;

    private int selectedIndex;

    private IFilter filter;

    private ISelectionChange selectionChangeListener;

    private Object selectedItem;

    public AutoCompleteComboBox() {
        super();
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        ((JLabel) getRenderer()).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        setFont(new Font("B Yekan+", Font.PLAIN, 14));

        addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if ((getSelectedItem() instanceof String) == false) {
                    selectedItem = getSelectedItem();
                    selectionChangeListener.onSelectionChange(selectedItem);
                } else {
                    tfield.setText("");
                    selectionChangeListener.onSelectionChange(null);
                }
            }
        });

        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                endSelection();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

    }

    public void init(final Object items[], IFilter filter) {

        this.filter = filter;

        setModel(new DefaultComboBoxModel(items));
        setEditor(new BasicComboBoxEditor());
        setSelectedItem(null);
        setEditable(true);
    }

    public void setSelectionChangeListener(ISelectionChange selectionChangeListener) {
        this.selectionChangeListener = selectionChangeListener;
    }

    public void setSelection(String currentText, int index) {
        showPopup();
        selectedIndex = index;
        if (index >= 0) {
            super.setSelectedIndex(index);
        }
        tfield.setText(currentText);
    }

    public void endSelection() {
        hidePopup();
        if (tfield.getText().trim().isEmpty()) {
            selectedItem = null;
            tfield.setText("");
        } else {
            tfield.setText(selectedItem.toString());
        }
    }

    @Override
    public void setEditor(ComboBoxEditor editor) {
        super.setEditor(editor);

        if (editor != null && editor.getEditorComponent() instanceof JTextField) {
            tfield = (JTextField) editor.getEditorComponent();
            tfield.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            tfield.setHorizontalAlignment(SwingConstants.RIGHT);
            tfield.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent ke) {
                    char key = ke.getKeyChar();
                    if (!(Character.isLetterOrDigit(key) || Character.isSpaceChar(key))) {
                        return;
                    }
                    caretPos = tfield.getCaretPosition();
                    String text = "";
                    try {
                        text = tfield.getText(0, caretPos);
                    } catch (javax.swing.text.BadLocationException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < getItemCount(); i++) {
                        Object element = getItemAt(i);
                        if (filter.accept(element, text)) {
                            setSelection(text, i);
                            return;
                        }
                    }
//                    setSelection(text, -1);
                }
            });
        }
    }

    public void setText(String string) {
        tfield.setText(string);
    }

    public interface ISelectionChange<T> {

        public void onSelectionChange(T selectedItem);
    }

    public interface IFilter<T> {

        /**
         * Search for specified pattern and return first index found
         *
         * @param object
         * @param pattern
         * @return
         */
        public boolean accept(T object, String pattern);
    }
}
