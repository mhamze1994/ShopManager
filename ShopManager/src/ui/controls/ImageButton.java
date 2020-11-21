/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import javax.swing.Action;
import javax.swing.Icon;

/**
 *
 * @author PersianDevStudio
 */
public class ImageButton extends PressButton {

    public ImageButton() {
        init();
    }

    public ImageButton(Icon icon) {
        super(icon);
        init();
    }

    public ImageButton(String text) {
        super(text);
        init();
    }

    public ImageButton(Action a) {
        super(a);
        init();
    }

    public ImageButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setTextHorizontalPosition(PressButton.POSITION_CENTER);
        setTextVerticalPosition(PressButton.POSITION_BOTTOM);
    }

}
