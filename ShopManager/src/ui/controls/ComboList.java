/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;
import ui.AppResource;

/**
 *
 * @author Studio
 */
public class ComboList extends JComboBox {

    public ComboList() {
        super();
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                final JButton jButton = new JButton(AppResource.getIcon(AppResource.ICON_ARROW_DOWN));
                jButton.setContentAreaFilled(false);
                return jButton;
            }
        });
    }

}
