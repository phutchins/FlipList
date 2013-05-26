/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

/**
 *
 * @author flip
 */
import java.util.*;
import java.text.*;
import java.io.*;

public class ListItem implements Serializable {
    public String description;
    public Date dueDate;
    public String category;
    public Date createDate;
    public boolean isSelected;
    
    public ListItem(String cat, String desc, Date due) {
        description = desc;
        dueDate = due;
        category = cat;
        createDate = new Date();
        isSelected = false;
    }
    public String getDueDate() {
        DateFormat dateFormatter = DateFormat.getDateInstance();
        String dateUS = dateFormatter.format(dueDate);
        return dateUS;
    }
    public void setDueDate(Date dd) {
        dueDate = dd;
    }
    public boolean isSelected() {
    	return isSelected;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String cat) {
        category = cat;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String d) {
        description = d;
    }
}
