package report;

import application.DatabaseManager;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public abstract class Report {

    private ResultSet resultSet;

    private CallableStatement call;

    protected void setInt(int value, int column) throws SQLException {
        DatabaseManager.SetInt(call, column, value);
    }

    protected void setBigDecimal(BigDecimal value, int column) throws SQLException {
        DatabaseManager.SetBigDecimal(call, column, value);
    }

    protected void setLong(long value, int column) throws SQLException {
        DatabaseManager.SetLong(call, column, value);
    }

    protected void setString(String value, int column) throws SQLException {
        DatabaseManager.SetString(call, column, value);
    }

    public void prepareCallStatement(String statment) {
        if (call != null) {
            try {
                call.close();
            } catch (SQLException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        call = DatabaseManager.instance.prepareCall(statment);
    }

    public void execute() throws SQLException {
        resultSet = call.executeQuery();
    }

    public String[] next() throws SQLException {
        String[] values = new String[getColumnCount()];
        if (resultSet.next()) {
            for (int i = 0; i < values.length; i++) {
                values[i] = read(resultSet, i + 1);
            }
            return values;
        } else {
            resultSet.close();
            call.close();
            return null;
        }

    }

    public abstract String read(ResultSet rs, int index) throws SQLException;

    public abstract int getColumnCount();

    public abstract void report() throws SQLException;

    public abstract String[] headers();

}
