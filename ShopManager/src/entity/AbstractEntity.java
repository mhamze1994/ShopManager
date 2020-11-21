/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Note : SELECT implementation is different case to case So selects are
 * implemented in reports
 *
 * @author Studio
 */
public abstract class AbstractEntity {


    public abstract void readResultSet(ResultSet rs) throws SQLException;

}
