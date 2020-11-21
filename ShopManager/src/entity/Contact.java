/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import application.DatabaseManager;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PersianDevStudio
 */
public class Contact extends AbstractEntity {

    private long contactId;
    private long nationalId;
    private String address;
    private long registerDate;
    private String name;
    private String lastname;
    private String fathername;
    private String fullInfo;
    private String phone;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNationalId() {
        return nationalId;
    }

    public void setNationalId(long nationalId) {
        this.nationalId = nationalId;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getFullInfo() {
        return fullInfo;
    }

    public void setFullInfo(String fullInfo) {
        this.fullInfo = fullInfo;
    }

    @Override
    public void readResultSet(ResultSet rs) throws SQLException {
        int pIndex = 1;
        contactId = rs.getLong(pIndex++);
        nationalId = rs.getLong(pIndex++);
        address = rs.getString(pIndex++);
        registerDate = rs.getLong(pIndex++);
        name = rs.getString(pIndex++);
        lastname = rs.getString(pIndex++);
        fathername = rs.getString(pIndex++);
        fullInfo = rs.getString(pIndex++);
        phone = rs.getString(pIndex++);

    }

    @Override
    public String toString() {
        return (name + " " + lastname).trim();
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public static Contact find(long contactId) {
        Contact contact = null;
        try {
            CallableStatement call = DatabaseManager.instance.prepareCall("CALL search_contact_id(?)");
            DatabaseManager.SetLong(call, 1, contactId);
            ResultSet rs = call.executeQuery();
            if (rs.next()) {
                contact = new Contact();
                contact.readResultSet(rs);
            }
            rs.close();
            call.close();
        } catch (SQLException ex) {
            Logger.getLogger(Contact.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contact;
    }

    public String concatinatedInfo() {
        return (name + " " + lastname).trim();

    }

}
