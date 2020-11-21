/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author PersianDevStudio
 */
public class NotificationManager {

    public static void showMessage(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "پیام", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "خطا", JOptionPane.ERROR_MESSAGE);
    }

}
