/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import application.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Bank {

    public long id;
    public String name;

    public static ArrayList<Bank> all() {
        ArrayList<Bank> all = new ArrayList<>();
        try {
            try (PreparedStatement ps = DatabaseManager.instance.PrepareStatement("select * from bank", Statement.NO_GENERATED_KEYS);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bank bank = new Bank();
                    bank.id = rs.getLong(1);
                    bank.name = rs.getString(2);
                    all.add(bank);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
        }

        return all;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
