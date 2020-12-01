/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Studio
 */
public class DefaultDatabaseManager extends DatabaseManager {

    public String databaseName = "shopmanager";

    public String driverClass = "com.mysql.jdbc.Driver";

    public int port = 3306;

    @Override
    protected String GetUsername() {
        return "root";
    }

    @Override
    protected String GetPassword() {
        return "";
    }

    @Override
    protected String GetDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    protected int GetPort() {
        return 3306;
    }

    @Override
    protected String GetDatabaseName() {
        return "shop_accountant";
    }

    public static boolean backup(String mysqlBinDirectory, String user, String pass, String dbName, String outputPath) {
        if (outputPath.endsWith(".sql") || outputPath.endsWith(".txt")) {
            outputPath = outputPath.substring(0, outputPath.length() - 4);
        }

        String[] arg = new String[]{mysqlBinDirectory + "\\mysqldump -u" + user + " -p" + pass + " --databases " + dbName + " -r  " + outputPath};
        try {
            Runtime obj = null;

            Process p = Runtime.getRuntime().exec(arg[0]);

            int result = p.waitFor();
            if (result == 0) {
                JOptionPane.showMessageDialog(Application.instance,
                        "عملیات با موفقیت انجام شد!", "پیغام",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {

                BufferedWriter writeer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                writeer.write("dir");
                writeer.flush();

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                StringBuilder str = new StringBuilder();

                String ss = "";
                while ((ss = stdInput.readLine()) != null) {
                    str.append(ss);
                }
                str.append(System.lineSeparator());
                while ((ss = stdError.readLine()) != null) {
                    str.append(ss);
                }
                str.append(System.lineSeparator());

                JOptionPane.showMessageDialog(Application.instance,
                        "خطا در عملیات" + System.lineSeparator() + str.toString(), "خطا",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {

            System.out.println("FROM CATCH" + e.toString());
        }
        return false;

    }

}
