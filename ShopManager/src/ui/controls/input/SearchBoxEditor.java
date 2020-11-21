/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.ComboBoxEditor;
import javax.swing.event.DocumentListener;

/**
 *
 * @author PersianDevStudio
 */
public class SearchBoxEditor implements ComboBoxEditor {

    private InputField editor;

    public SearchBoxEditor() {
        this.editor = new InputField();
        this.editor.setBorder(null);
    }

    @Override
    public Component getEditorComponent() {
        return editor;
    }

    @Override
    public void setItem(Object anObject) {
        if (anObject == null) {
            editor.setText("");
        } else {
            editor.setText(anObject.toString());
        }
    }

    public void addDocumentListener(DocumentListener documentListener) {
        editor.getDocument().addDocumentListener(documentListener);

    }

    public void setCaretPosition(int position) {
        editor.setCaretPosition(position);
    }

    @Override
    public Object getItem() {
        return editor.getText();
    }

    @Override
    public void selectAll() {
        editor.selectAll();
        editor.requestFocus();
    }

    @Override
    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }

    public void addKeyListener(KeyListener keyListener) {
        editor.addKeyListener(keyListener);
    }

    public void setComponentOrientation(ComponentOrientation orientation) {
        editor.setComponentOrientation(orientation);
    }

    public InputField getInputField() {
        return editor;
    }

    
    
}
