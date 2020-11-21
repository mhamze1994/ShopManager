/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import ui.controls.input.SearchBoxEditor;

/**
 *
 * @author PersianDevStudio
 */
public class SearchBoxUI extends BasicComboBoxUI {

    @Override
    protected JButton createArrowButton() {
        return null;
    }

    @Override
    protected ComboBoxEditor createEditor() {
        return new SearchBoxEditor();
    }

    public JList getList() {
        return popup.getList();
    }

    public BasicComboPopup getPopup() {
        return (BasicComboPopup) popup;
    }

}
