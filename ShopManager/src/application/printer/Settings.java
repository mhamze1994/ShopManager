/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.printer;

import application.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Settings {

    public static final String KEY_PRINT_VERTICAL_PADDING = "print_vp";
    public static final String KEY_PRINT_HORIZONTAL_PADDING = "print_hp";
    public static final String KEY_PRINT_PAPER_WIDTH = "print_paper_w";
    public static final String KEY_SHOP_TITLE = "shop_title";

    private static HashMap<String, String> config;

    static {
        try {
            config = new HashMap<>();
            try (PreparedStatement ps = DatabaseManager.instance.PrepareStatement("select * from config", Statement.NO_GENERATED_KEYS);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    config.put(rs.getString(1), rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void save(String key, String value) throws SQLException {
        String sql = "UPDATE config set value = '" + value + "' where configid = '" + key + "'";
        PreparedStatement ps = DatabaseManager.instance.PrepareStatement(sql, Statement.NO_GENERATED_KEYS);
        ps.executeUpdate();
        config.put(key, value);

    }

    public static int getInt(String key) {
        return Integer.parseInt(config.get(key));
    }

    public static String getString(String key) {
        return config.get(key);
    }

}
