/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author flip
 */
public class ItemList implements Serializable {
    String listName = new String();
    ArrayList<ListItem> myItemList = new ArrayList<ListItem>();
    
    public ItemList(String ln) {
        listName = ln;
    }
    
    public ArrayList<ListItem> getListItems() {
        return myItemList;
    }
    
    public void addListItem(ListItem li) {
        myItemList.add(li);
    }
    
    public void removeListItem(ListItem li) {
        myItemList.remove(li);
    }
}
