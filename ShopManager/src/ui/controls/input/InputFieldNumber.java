/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Studio
 */
public class InputFieldNumber extends InputField {

    NumberFormat format;

    public InputFieldNumber() {

        format = NumberFormat.getInstance();

        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);

        //formatter.setAllowsInvalid(false);
        formatter.install(this);
        
    }

    public void setMaximumFractionDigits(int value) {
        format.setMaximumFractionDigits(value);
    }

    public void setMinimumFractionDigits(int value) {
        format.setMinimumFractionDigits(value);
    }

    public void setGroupingUsed(boolean groupingUsed) {
        format.setGroupingUsed(groupingUsed);
    }



}
