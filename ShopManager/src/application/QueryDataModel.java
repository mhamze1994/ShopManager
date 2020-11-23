/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import entity.AbstractEntity;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 * query model updates its data list using callable statement provided by the user
 * @author PersianDevStudio
 * @param <T>
 */
public class QueryDataModel<T extends AbstractEntity> extends DefaultComboBoxModel {

    private final CustomArrayList<T> dataList = new CustomArrayList(1000);

    private Class<T> clazz;

    public QueryDataModel(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void update(CallableStatement call) {
        try {
            dataList.clear();
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    T newInstance = clazz.newInstance();
                    newInstance.readResultSet(rs);
                    dataList.add(newInstance);
                }
            }
            call.close();
            fireContentsChanged(this, 0, dataList.size());
        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(QueryDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public T getElementAt(int index) {
        return dataList.get(index);
    }

    @Override
    public int getSize() {
        return dataList.size();
    }

}
