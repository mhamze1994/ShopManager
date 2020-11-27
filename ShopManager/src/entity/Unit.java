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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Unit {
    
    private static HashMap<Integer , Unit> allUnits;

    static {
        allUnits = new HashMap<>();
        try {
            try (PreparedStatement ps = DatabaseManager.instance.PrepareStatement("select * from unit",
                    Statement.NO_GENERATED_KEYS); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Unit u = new Unit();
                    u.setUnitId(rs.getInt(1));
                    u.setUnitName(rs.getString(2));
                    allUnits.put(u.unitId, u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Unit getUnit(int unitId){
        return allUnits.get(unitId);
    }

    public static String toString(int unit1) {
        return allUnits.get(unit1).getUnitName();
    }

    public int unitId;

    public String unitName;

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return unitName;
    }

}
