package com.icanhasnom.fliplist;

import java.util.ArrayList;
import java.util.Arrays;

public class Filter {
	Integer id;
	String name;
	String description;
	String query = null;
	String values = null;
	String valueArgs = null;
	String groupBy = null;
	String orderBy = null;
	Boolean filterByCategory = false;
	Boolean filterByCurrentCategory = false;
	int hidden;

	public Filter(int filterID) {
		id = filterID;
	}
	public int getID() {
		return id;
	}

	public void setName(String n) {
		name = n;
	}

	public void setDescription(String d) {
		description = d;
	}

	public void setQuery(String q) {
		query = q;
	}
	public void setValues(String v) {
		values = v;
	}
	public void setValueArgs(String va) {
		valueArgs = va;
	}
	public void setGroupBy(String g) {
		groupBy = g;
	}
	public void setOrderBy(String o) {
		orderBy = o;
	}
	public void setHidden(int h) {
		hidden = h;
	}
	public void setFilterByCategory(Boolean bool) {
		filterByCategory = bool;
	}
	public void setFilterByCurrentCategor(Boolean bool) {
		filterByCurrentCategory = bool;
	}
	public String getValues() {
		//values.add("cat");
		return values;
	}
	public String getValueArgs() {
		return valueArgs;
	}
	//public ArrayList<String> getValues() {
	//	return values;
	//}
	//public String[] getValuesStringArray(Integer catID) {
	//	ArrayList<String> returnValues = values;
	//	if (filterByCategory) returnValues.add("cat=" + catID);
	//	String[] valuesString = new String[returnValues.size()];
	//	valuesString = values.toArray(valuesString);
	//	return valuesString;
	//}
	//public String[] getValuesStringArray() {
	//	String[] valuesArray = new String[values.size()];
	//	valuesArray = values.toArray(valuesArray);
	//	return valuesArray;
	//}
	public String getQueryString() {
		return query;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public String getName() {
		return name;
	}

}
