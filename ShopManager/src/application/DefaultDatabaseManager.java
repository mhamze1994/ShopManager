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

//    @Override
//    public long Insert(PreparedStatement ps) throws SQLException {
//        ps.execute();
//        ResultSet rs = ps.getGeneratedKeys();
//        rs.beforeFirst();
//        if (rs.next()) {
//            long generatedId = rs.getLong(1);
//            rs.close();
//            return generatedId;
//        } else {
//            return 0;
//        }
//    }

//    @Override
//    public boolean Update(PreparedStatement ps) throws SQLException {
//        boolean result = ps.executeUpdate() > 0;
//        ps.close();
//        return result;
//    }

//    @Override
//    public <T extends AbstractEntity> ArrayList<T> SelectAll(String tableName, Class<T> type, String condition) {
//        try {
//            ArrayList<T> output = new ArrayList<>();
//
//            Statement createStatement = conn.createStatement();
//            condition = condition == null ? "1" : condition;
//            try (ResultSet rs = createStatement.executeQuery("SELECT * FROM " + tableName + " WHERE " + condition)) {
//                rs.beforeFirst();
//                while (rs.next()) {
//                    T t = type.newInstance();
//                    t.ReadParams(rs);
//                    output.add(t);
//                }
//            }
//            createStatement.close();
//            return output;
//        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
//            Logger.getLogger(DefaultDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }


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
