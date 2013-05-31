package com.icanhasnom.fliplist;

public class CategoryType {
	int typeID;
	String typeName;
	String typeDescription;
	public CategoryType() {
		
	}
	public CategoryType(int id, String name, String desc) {
		typeID = id;
		typeName = name;
		typeDescription = desc;
	}
	public int getID() {
		return typeID;
	}
	public void setName(String name) {
		typeName = name;
	}
	public String getName() {
		return typeName;
	}
	public void setDescription(String desc) {
		typeDescription = desc;
	}
	public String getDescription() {
		return typeDescription;
	}
	public void setID(int id) {
		typeID = id;
	}
}
