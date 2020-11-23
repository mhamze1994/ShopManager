/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

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
    


}
