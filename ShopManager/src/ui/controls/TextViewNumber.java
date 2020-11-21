/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls;

import java.math.BigDecimal;

/**
 *
 * @author Studio
 */
public class TextViewNumber extends TextView {

    @Override
    public void setText(String text) {
        try {
            BigDecimal b = new BigDecimal(text);
            super.setText(b.toPlainString());
            return;
        } catch (NumberFormatException e) {

        }

        super.setText(text);

    }

}
