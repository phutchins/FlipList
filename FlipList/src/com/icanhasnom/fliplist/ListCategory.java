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
    public ListCategory() {
    }
    public ListCategory(int catID, String catName, String catDesc, int catType) {
        name = catName;
        description = catDesc;
        type = catType;
    }
    public ListCategory(String catName, String catDesc, int catType) {
    	name = catName;
    	description = catDesc;
    	type = catType;
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
}
