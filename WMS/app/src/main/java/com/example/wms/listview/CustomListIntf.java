package com.example.wms.listview;

import android.view.View;

public interface CustomListIntf {

    public void onItemClick(View obj);

    default void onFileSelect(String path) {
    }

    ;

    default void onView(String string1, String string2) {

    }

    default void onCardClick(SubjectData sub) {

    }

    ;

    default void onEdit(String string1, String string2) {

    }

    ;

    default void onDelete(String string1, String string2) {

    }

    ;
}
