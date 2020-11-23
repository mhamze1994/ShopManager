/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Studio
 */
public abstract class DatabaseManager {

    public static DatabaseManager instance;

    protected Connection conn;

    public DatabaseManager() {
    }

    public void StartUp() {
        try {
            //Assuming MySQL database is installed
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("C:/xampp/mysql/bin/mysqld");

            Class.forName(GetDriverClass());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:" + GetPort() + "/shopmanager?characterEncoding=utf8", GetUsername(), GetPassword());
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public PreparedStatement PrepareStatement(String sql, int autoGenerateKeys) {
        try {
            return conn.prepareStatement(sql, autoGenerateKeys);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean Delete(String tableName, String condition) {
        try {
            try (Statement stmt = conn.createStatement()) {
                condition = condition == null ? "1" : condition;
                stmt.executeUpdate("DELETE FROM " + tableName + " WHERE " + condition);
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void BeginTransaction() {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CommitTransaction() {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Rollback() {
        try {
            if (conn.getAutoCommit() == false) {
                conn.rollback();
            }

            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected abstract String GetUsername();

    protected abstract String GetPassword();

    protected abstract String GetDriverClass();

    protected abstract int GetPort();

    protected abstract String GetDatabaseName();

    public static void SetLong(PreparedStatement ps, int index, long aLong) throws SQLException {
        SetLong(ps, index, aLong, true);
    }

    public static void SetLong(PreparedStatement ps, int index, long aLong, boolean nullIfZero) throws SQLException {
        if (aLong == 0 && nullIfZero) {
            ps.setNull(index, java.sql.Types.BIGINT);
        } else {
            ps.setLong(index, aLong);
        }
    }

    public static void SetInt(PreparedStatement ps, int index, int aInt) throws SQLException {
        SetInt(ps, index, aInt, true);
    }

    public static void SetInt(PreparedStatement ps, int index, int aInt, boolean nullIfZero) throws SQLException {
        if (aInt == 0 && nullIfZero) {
            ps.setNull(index, java.sql.Types.INTEGER);
        } else {
            ps.setInt(index, aInt);
        }
    }

    public static void SetString(PreparedStatement ps, int index, String str) throws SQLException {
        if (str == null) {
            ps.setNull(index, java.sql.Types.VARCHAR);
        } else {
            ps.setString(index, str);
        }
    }

    public static void SetBigDecimal(PreparedStatement ps, int index, BigDecimal balance) throws SQLException {
        if (balance == null) {
            ps.setNull(index, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, balance);
        }
    }

    public static void SetBlobBytes(PreparedStatement ps, int index, byte[] metaData) throws SQLException {
        if (metaData == null) {
            ps.setNull(index, java.sql.Types.BLOB);
        } else {
            ps.setBytes(index, metaData);
        }
    }

    public static void SetBoolean(PreparedStatement ps, int index, boolean includeTax) throws SQLException {
        ps.setBoolean(index, includeTax);
    }

    public static void SetLong(CallableStatement call, int index, long aLong) throws SQLException {
        SetLong(call, index, aLong, true);
    }

    public static void SetLong(CallableStatement call, int index, long aLong, boolean nullIfZero) throws SQLException {
        if (aLong == 0 && nullIfZero) {
            call.setNull(index, java.sql.Types.BIGINT);
        } else {
            call.setLong(index, aLong);
        }
    }

    public static void SetInt(CallableStatement call, int index, int aInt) throws SQLException {
        SetInt(call, index, aInt, true);
    }

    public static void SetInt(CallableStatement call, int index, int aInt, boolean nullIfZero) throws SQLException {
        if (aInt == 0 && nullIfZero) {
            call.setNull(index, java.sql.Types.INTEGER);
        } else {
            call.setInt(index, aInt);
        }
    }

    public static void SetString(CallableStatement call, int index, String str) throws SQLException {
        if (str == null) {
            call.setNull(index, java.sql.Types.VARCHAR);
        } else {
            call.setString(index, str);
        }
    }

    public static void SetBigDecimal(CallableStatement call, int index, BigDecimal balance) throws SQLException {
        if (balance == null) {
            call.setNull(index, java.sql.Types.DECIMAL);
        } else {
            call.setBigDecimal(index, balance);
        }
    }

    public static void SetBlobBytes(CallableStatement call, int index, byte[] metaData) throws SQLException {
        if (metaData == null) {
            call.setNull(index, java.sql.Types.BLOB);
        } else {
            call.setBytes(index, metaData);
        }
    }

    public static void SetBoolean(CallableStatement call, int index, boolean aBoolean) throws SQLException {
        call.setBoolean(index, aBoolean);
    }

    static {
        DatabaseManager.instance = new DefaultDatabaseManager();
    }

    public CallableStatement prepareCall(String sql) {
        try {
            return conn.prepareCall(sql);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
