/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import interfaces.CTP;
import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Rypon
 */
public class AccountListTableModel extends DefaultTableModel {

    private ArrayList<CTP> accounts;
    private String[] header = {"Home URL", "Username", "Password"};

    public AccountListTableModel(ArrayList<CTP> accounts) {
        this.accounts = accounts;
    }

    @Override
    public int getRowCount() {
        if(accounts == null)
        {
            return 0;
        }
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return header[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return accounts.get(rowIndex).getHomeUrl();
            case 1:
                return accounts.get(rowIndex).getUsername();
            case 2:
                return accounts.get(rowIndex).getPassword();
            default:
                return "";
        }
    }

}
