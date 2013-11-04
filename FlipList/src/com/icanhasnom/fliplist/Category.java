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
public class Category implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int catID;
	String name;
    String description;
    boolean isNew = false;
    boolean showDueDate = true;
    boolean showDescription = true;
    int filterID = 0;
    int isVisible = 1;
    public Category() {
    }
    public Category(int ID, String catName, String catDesc, int v) {
    	catID = ID;
        name = catName;
        description = catDesc;
        isVisible = v;
    }
    public Category(String catName, String catDesc, int v) {
    	name = catName;
    	description = catDesc;
    	isVisible = v;
    }
    public boolean showDescription() {
    	return showDescription;
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
    public String getName() {
    	return name;
    }
    public String getDescription() {
    	return description;
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
