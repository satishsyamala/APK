package com.example.wms.tableview;

import java.util.List;

public interface TableViewIntf {

    default void editRow(List<String> ls){}
    default void deleteRow(List<String> ls){}
}
