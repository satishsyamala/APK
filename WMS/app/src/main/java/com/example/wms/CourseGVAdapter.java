package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;

import java.util.ArrayList;

public class CourseGVAdapter extends ArrayAdapter<SubjectData> {
    private CustomListIntf customListIntf;
    public CourseGVAdapter(@NonNull Context context, ArrayList<SubjectData> courseModelArrayList,CustomListIntf customListIntf) {
        super(context, 0, courseModelArrayList);
        this.customListIntf=customListIntf;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        try {
            if (listitemView == null) {
                // Layout Inflater inflates each item to be displayed in GridView.
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_items, parent, false);
            }

            SubjectData courseModel = getItem(position);

            CardView cardv = listitemView.findViewById(R.id.idcard);
            cardv.setBackgroundColor(courseModel.getColourId());
            TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
            ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
            System.out.println("S : "+courseModel.getString1());
            System.out.println("S : "+courseModel.getImgid());
            courseTV.setText(courseModel.getString1());
            courseIV.setImageResource(courseModel.getImgid());
            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customListIntf.onCardClick(courseModel);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return listitemView;
    }
}

