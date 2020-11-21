/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author Studio
 */
public class TextView extends JLabel {

    public TextView(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        init();
    }

    public TextView(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        init();
    }

    public TextView(String text) {
        super(text);
        init();
    }

    public TextView(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        init();
    }

    public TextView(Icon image) {
        super(image);
        init();
    }

    public TextView() {
        init();
    }

    private void init() {
        setFont(new Font("B yekan+", Font.PLAIN, 13));
    }

}
