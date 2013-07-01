package com.icanhasnom.fliplist;

public class Filter {
	Integer id;
	String name;
	String description;
	String query;
	String values;
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

	public String getQueryValues() {
		return values;
	}

	public String getQueryString() {
		return query;
	}


	public String getName() {
		return name;
	}

}
