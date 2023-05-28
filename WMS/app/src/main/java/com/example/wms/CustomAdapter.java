package com.example.wms;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;


import java.util.ArrayList;

class CustomAdapter implements ListAdapter {
    ArrayList<SubjectData> arrayList;
    Context context;
    CustomListIntf customListIntf;
    String type;

    public CustomAdapter(Context context, ArrayList<SubjectData> arrayList,CustomListIntf customListIntf) {
        this.arrayList = arrayList;
        this.context = context;
        this.customListIntf=customListIntf;
    }

    public CustomAdapter(Context context, ArrayList<SubjectData> arrayList,CustomListIntf customListIntf,String type) {
        this.arrayList = arrayList;
        this.context = context;
        this.customListIntf=customListIntf;
        this.type=type;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (!arrayList.isEmpty()) {
                SubjectData subjectData = arrayList.get(position);
                if (convertView == null) {
                    if (type == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        convertView = layoutInflater.inflate(R.layout.client_list, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customListIntf.onItemClick(v);
                            }
                        });
                        TextView tittle = convertView.findViewById(R.id.client_name);
                       /* Button b1 = convertView.findViewById(R.id.button);
                        b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customListIntf.onView(subjectData.getString1(),subjectData.getString2());
                            }
                        });
                        b1.setBackgroundColor(Color.parseColor("#FF03DAC5"));*/
                        Button b2 = convertView.findViewById(R.id.button2);
                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customListIntf.onEdit(subjectData.getString1(),subjectData.getString2());
                            }
                        });
                        b2.setBackgroundColor(Color.parseColor("#4EC2F0"));
                        Button b3 = convertView.findViewById(R.id.button3);
                        b3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customListIntf.onDelete(subjectData.getString1(),subjectData.getString2());
                            }
                        });
                        b3.setBackgroundColor(Color.parseColor("#FF7F00"));
                        tittle.setText(subjectData.getString1());
                        TextView tittle1 = convertView.findViewById(R.id.address);
                        tittle1.setText(subjectData.getString2());
                    } else if (type.equalsIgnoreCase("stocktake")) {
                        System.out.println(type);
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        convertView = layoutInflater.inflate(R.layout.stock_take_list, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customListIntf.onItemClick(v);
                            }
                        });
                        TextView tittle = convertView.findViewById(R.id.sl_sku_name);

                        tittle.setText(subjectData.getString1());
                        TextView tittle1 = convertView.findViewById(R.id.sl_sku_qty);
                        tittle1.setText(subjectData.getString2());
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
