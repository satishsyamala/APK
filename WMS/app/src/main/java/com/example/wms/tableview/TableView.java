package com.example.wms.tableview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.wms.StockMaster;
import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableView implements CustomDailogIntf {

    private TableLayout mTableLayout;
    private TableViewIntf tableViewIntf;
    private Fragment fragment;
    private List<String> headers;
    private boolean editBtn;
    private boolean deleteBtn;
    private Context context;
    public String numberColums;
    public String displayColums;
    public String widthColums;
    int textSize = 22, smallTextSize = 26, mediumTextSize = 30;


    public TableView(TableLayout mTableLayout, Context context, TableViewIntf tableViewIntf) {
        this.mTableLayout = mTableLayout;
        this.tableViewIntf = tableViewIntf;
        this.context = context;
    }

    public void setFontSize(int textSize, int smallTextSize) {
        this.textSize = textSize;
        this.smallTextSize = smallTextSize;
    }


    public List<Integer> getNumberColumns() {
        List<Integer> res = new ArrayList<>();
        if (this.numberColums != null) {
            String s[] = this.numberColums.split(",");
            for (String s1 : s) {
                res.add(Integer.parseInt(s1));
            }
        }
        return res;
    }

    public Map<Integer,Integer> getWidthColumns() {
        Map<Integer,Integer> res = new HashMap<>();
        if (this.widthColums != null) {
            String s[] = this.widthColums.split(",");
            int i=0;
            for (String s1 : s) {
                if(Integer.parseInt(s1)>0)
                 res.put(i,Integer.parseInt(s1));
                i++;
            }
        }
        return res;
    }

    public List<Integer> getDIsplayColumns() {
        List<Integer> res = new ArrayList<>();
        if (this.displayColums != null) {
            String s[] = this.displayColums.split(",");
            for (String s1 : s) {
                res.add(Integer.parseInt(s1));
            }
        }
        else{
            for(int i=0;i<100;i++)
                res.add(i);
        }
        return res;
    }

    public void gererateHeareds(List<String> headers) {
        TableRow row = new TableRow(context);
        for (String s : headers) {
            final TextView tv = new TextView(context);
            tv.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.LEFT);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setPadding(5, 15, 10, 15);
            tv.setText(s);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.parseColor("#039696"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            row.addView(tv);
        }
        mTableLayout.addView(row);
    }

    public void addRow(List<String> rowData) {
        TableRow row = new TableRow(context);
        int index = 0;
        List<Integer> nlls = getNumberColumns();
        List<Integer> discl =getDIsplayColumns();
        Map<Integer,Integer> widcl=getWidthColumns();
        for (String s : rowData) {
            if(discl.contains(index)) {
                final TextView tv = new TextView(context);
                 tv.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.LEFT);
                tv.setPadding(10, 15, 10, 15);
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setText(s);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv.setTypeface(null, Typeface.BOLD);
                if (nlls.contains(index)) {
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
                if (widcl.containsKey(index)) {
                    tv.setWidth(widcl.get(index));
                }
                row.addView(tv);
            }
            index++;
        }
        if(isEditBtn() || isDeleteBtn()) {
            TableLayout l=new TableLayout(context);

            CustomDailog c = new CustomDailog(l, TableView.this, " ", null,-2);
            if(isEditBtn())
            {
                TableRow boxrow = new TableRow(context);
                boxrow.setPadding(10,10,10,10);
                boxrow.setGravity(TableRow.TEXT_ALIGNMENT_GRAVITY);
                Button b=new Button(context);
                b.setText("Edit");
                b.setTextColor(Color.WHITE);
                b.setWidth(300);
                b.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                b.setHeight(40);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c.dismiss();
                        tableViewIntf.editRow(rowData);
                    }
                });
                boxrow.addView(b);
                l.addView(boxrow);
            }
            if(isDeleteBtn())
            {
                TableRow boxrow = new TableRow(context);
                boxrow.setGravity(TableRow.TEXT_ALIGNMENT_GRAVITY);
                boxrow.setPadding(10,10,10,10);
                Button b=new Button(context);
                b.setText("Delete");
                b.setTextColor(Color.WHITE);
                b.setWidth(200);
                b.setBackgroundColor(Color.parseColor("#FD6FA0"));
                b.setHeight(40);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c.dismiss();
                        tableViewIntf.deleteRow(rowData);
                    }
                });
                boxrow.addView(b);
                l.addView(boxrow);
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    c.show(fragment.getFragmentManager(), "NoticeDialogFragment");
                }
            });
        }
        mTableLayout.addView(row);
    }




    public void fullTable(List<String> headers, List<List<String>> rows) {
        gererateHeareds(headers);
        for (List<String> row : rows) {
            addRow(row);
        }
    }

    public TableLayout getmTableLayout() {
        return mTableLayout;
    }

    public void setmTableLayout(TableLayout mTableLayout) {
        this.mTableLayout = mTableLayout;
    }

    public int getMediumTextSize() {
        return mediumTextSize;
    }

    public void setMediumTextSize(int mediumTextSize) {
        this.mediumTextSize = mediumTextSize;
    }

    public String getNumberColums() {
        return numberColums;
    }

    public void setNumberColums(String numberColums) {
        this.numberColums = numberColums;
    }

    public String getDisplayColums() {
        return displayColums;
    }

    public void setDisplayColums(String displayColums) {
        this.displayColums = displayColums;
    }

    public String getWidthColums() {
        return widthColums;
    }

    public void setWidthColums(String widthColums) {
        this.widthColums = widthColums;
    }

    public boolean isEditBtn() {
        return editBtn;
    }

    public void setEditBtn(boolean editBtn) {
        this.editBtn = editBtn;
    }

    public boolean isDeleteBtn() {
        return deleteBtn;
    }

    public void setDeleteBtn(boolean deleteBtn) {
        this.deleteBtn = deleteBtn;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
