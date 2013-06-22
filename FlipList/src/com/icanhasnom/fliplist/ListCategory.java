/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

import java.io.*;

/**
 *
 * @author flip
 */
public class ListCategory implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int catID;
	String name;
    String description;
    int type;
    boolean isNew = false;
    boolean showDueDate = true;
    boolean showDescription = true;
    int isVisible = 1;
    public ListCategory() {
    }
    public ListCategory(int ID, String catName, String catDesc, int catType, int v) {
    	catID = ID;
        name = catName;
        description = catDesc;
        type = catType;
        isVisible = v;
    }
    public ListCategory(String catName, String catDesc, int catType, int v) {
    	name = catName;
    	description = catDesc;
    	type = catType;
    	isVisible = v;
    }
    public boolean showDueDate() {
    	return showDueDate;
    }
    public boolean showDescription() {
    	return showDescription;
    }
    public void setShowDueDate(boolean sdd) {
    	showDueDate = sdd;
    }
    public void setShowDescription(boolean sd) {
    	showDescription = sd;
    }
    public void setID(int id) {
    	catID = id;
    }
    public int getID() {
    	return catID;
    }
    public void setName(String catName) {
    	name = catName;
    }
    public void setDescription(String catDesc) {
    	description = catDesc;
    }
    public void setType(int catType) {
    	type = catType;
    }
    public String getName() {
    	return name;
    }
    public String getDescription() {
    	return description;
    }
    public int getType() {
    	return type;
    }
    public boolean isNew() {
    	return isNew;
    }
    public void setIsNew() {
    	isNew = true;
    }
	public boolean isVisible() {
		boolean isVisibleBool;
		if (isVisible == 1) {
			isVisibleBool = true;
		} else {
			isVisibleBool = false;
		}
		return isVisibleBool;
	}
	public int getVisible() {
		return isVisible;
	}
	public void setVisible(int v) {
		isVisible = v;
	}
}
