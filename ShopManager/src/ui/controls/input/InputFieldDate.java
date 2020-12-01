/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controls.input;

import application.JalaliCalendar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Studio
 */
public class InputFieldDate extends InputField {

    private static final char DELIMITER = '/';

    private long lastValidDateInMillis;

    public InputFieldDate() {
        try {
            MaskFormatter dobMask = new MaskFormatter("####" + DELIMITER + "##" + DELIMITER + "##");
            dobMask.setPlaceholderCharacter('_');
            dobMask.install(this);
        } catch (ParseException ex) {
            Logger.getLogger(InputFieldDate.class.getName()).log(Level.SEVERE, null, ex);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setCaretPosition(0);
                setSelectionStart(0);
                setSelectionEnd(getText().length());
            }

        });
    }

    public boolean isValidInput() {
        String text = getText();
        if (text.trim().isEmpty()) {
            return false;
        }

        String[] date = text.split(DELIMITER + "");
        String year = date[0];
        String month = date[1];
        String day = date[2];

        int y;
        try {
            y = Integer.parseInt(year);
            if (y < 1299) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        int m;
        try {
            m = Integer.parseInt(month);
            if (m > 12 || m < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            int d = Integer.parseInt(day);
            if (d < 1) {
                return false;
            }
            if (m <= 6 && d > 31) {
                return false;
            }
            if (m > 6 && d > 30) {
                return false;
            }

            if (isLeapYear(y) == false && m == 12 && d > 29) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private boolean isLeapYear(int y) {
        return DETERMINERS.contains(y % 128);
    }

    private static final HashSet<Integer> DETERMINERS;

    static {
        DETERMINERS = new HashSet<>();
        DETERMINERS.add(0);
        DETERMINERS.add(4);
        DETERMINERS.add(8);
        DETERMINERS.add(12);
        DETERMINERS.add(16);
        DETERMINERS.add(20);
        DETERMINERS.add(24);
        DETERMINERS.add(25);
        DETERMINERS.add(29);
        DETERMINERS.add(33);
        DETERMINERS.add(37);
        DETERMINERS.add(41);
        DETERMINERS.add(45);
        DETERMINERS.add(49);
        DETERMINERS.add(53);
        DETERMINERS.add(57);
        DETERMINERS.add(58);
        DETERMINERS.add(62);
        DETERMINERS.add(66);
        DETERMINERS.add(70);
        DETERMINERS.add(74);
        DETERMINERS.add(78);
        DETERMINERS.add(82);
        DETERMINERS.add(86);
        DETERMINERS.add(90);
        DETERMINERS.add(91);
        DETERMINERS.add(95);
        DETERMINERS.add(99);
        DETERMINERS.add(103);
        DETERMINERS.add(107);
        DETERMINERS.add(111);
        DETERMINERS.add(115);
        DETERMINERS.add(119);
        DETERMINERS.add(120);
        DETERMINERS.add(124);
    }

    /**
     * Set date in Gregorian calendar
     *
     * @param invoiceDate
     */
    public void setDate(long invoiceDate) {
        final GregorianCalendar gregCal = new GregorianCalendar();
        gregCal.setTimeInMillis(invoiceDate);
        JalaliCalendar jc = new JalaliCalendar(gregCal);
        setText(jc.toString());
    }

    /**
     * return last valid date in Gregorian calendar
     *
     * @return
     */
    public long getLastValidDate() {
        if (isValidInput()) {
            String values[] = getText().split("/");
            int y = Integer.parseInt(values[0]);
            int m = Integer.parseInt(values[1]);
            int d = Integer.parseInt(values[2]);
            JalaliCalendar jc = new JalaliCalendar(y, m, d);
            lastValidDateInMillis = jc.toGregorian().getTimeInMillis();
        }
        return lastValidDateInMillis;
    }
}
