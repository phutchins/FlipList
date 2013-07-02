package com.icanhasnom.fliplist;

public class Filter {
	Integer id;
	String name;
	String description;
	String query = null;
	String values = null;
	Boolean filterByCategory = false;
	int hidden;

	public Filter(int filterID) {
		// TODO Auto-generated constructor stub
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

	public void setHidden(int h) {
		hidden = h;
	}
	public void setFilterByCategory(Boolean bool) {
		filterByCategory = bool;
	}

	public String getQueryValues() {
		return values;
	}
	public String getQueryValues(Integer catID) {
		String query = null;
		if (values.equals("")) {
			if (filterByCategory) query = "cat=" + catID;
		} else {
			if (filterByCategory) query = values + "AND cat=" + catID;
			else query = values;
		}
		return query;
	}

	public String getQueryString() {
		return query;
	}


	public String getName() {
		return name;
	}

}
