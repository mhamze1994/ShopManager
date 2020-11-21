/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import application.DatabaseManager;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author PersianDevStudio
 */
public class ItemCategory {

    public long categoryId;
    public long categoryParentId;
    public String description;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(long categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public void save() throws SQLException {
        try (CallableStatement call = DatabaseManager.instance.prepareCall("CALL category_save(?,?,?);")) {
            call.registerOutParameter(1, Types.BIGINT);
            DatabaseManager.SetLong(call, 1, getCategoryId(), false);
            DatabaseManager.SetLong(call, 2, getCategoryParentId());
            DatabaseManager.SetString(call, 3, getDescription());
            
            call.execute();
            setCategoryId(call.getLong(1));
        }
    }

}
