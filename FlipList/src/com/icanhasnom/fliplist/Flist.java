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
public class Flist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int flistID;
	String name;
    String description;
    int type;
    boolean isNew = false;
    boolean showDueDate = true;
    boolean showDescription = true;
    int filterID = 0;
    int isVisible = 1;
    public Flist() {
    }
    public Flist(int ID, String flistName, String flistDesc, int flistType, int v) {
    	flistID = ID;
        name = flistName;
        description = flistDesc;
        type = flistType;
        isVisible = v;
    }
    public Flist(String flistName, String flistDesc, int flistType, int v) {
    	name = flistName;
    	description = flistDesc;
    	type = flistType;
    	isVisible = v;
    }
    //public Flist(String flistName, String flistDesc, int flistType, int v, int f) {
    //	name = flistName;
    //	description = flistDesc;
    //	type = flistType;
    //	isVisible = v;
    //	filterID = f;
    //}
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
    	flistID = id;
    }
    public int getID() {
    	return flistID;
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
	public int getFilterID() {
		// TODO Auto-generated method stub
		return filterID;
	}
	public void setFilterID(int fid) {
		filterID = fid;
	}
}
