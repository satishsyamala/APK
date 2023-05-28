package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.databinding.ClientMenusBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;

import java.util.ArrayList;

public class MenuGrid  extends Fragment implements CustomListIntf {

    private ClientMenusBinding clientMenusBinding;
    private   SharedPreferences sharedpreferences;;
    private GridView coursesGV;
    private String clinetName;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientMenusBinding=ClientMenusBinding.inflate(inflater, container, false);
        coursesGV=clientMenusBinding.idGVcourses;
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
       try {
           ((MainActivity) requireContext()).getSupportActionBar().setTitle(clinetName);
       }catch (Exception e){}

        clientMenusBinding.addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MenuGrid.this)
                        .navigate(R.id.FirstFragment);
            }
        });

        ArrayList<SubjectData> courseModelArrayList=new ArrayList<SubjectData>();

        courseModelArrayList.add(new SubjectData("Stock", R.mipmap.ic_stock_foreground, Color.parseColor("#FF03DAC5"),R.id.StockMaster));
        courseModelArrayList.add(new SubjectData("Stock Take", R.mipmap.ic_stock_take, Color.parseColor("#FF018786"),R.id.StHistory));
        courseModelArrayList.add(new SubjectData("Purchases", R.mipmap.ic_stock_in_foreground, Color.parseColor("#FFBB86FC"),R.id.StHistory));
       // courseModelArrayList.add(new SubjectData("Invoice", R.mipmap.ic_sales_foreground, Color.parseColor("#FD6FA0"),R.id.StHistory));
        courseModelArrayList.add(new SubjectData("Sales", R.mipmap.ic_sales_foreground, Color.parseColor("#FF7F00"),R.id.StHistory));
        courseModelArrayList.add(new SubjectData("Stock Out", R.mipmap.ic_stock_out_foreground, Color.parseColor("#4EC2F0"),R.id.StHistory));
        courseModelArrayList.add(new SubjectData("Reports", R.mipmap.ic_reports_foreground, Color.parseColor("#F476BC"),R.id.ReportView));
      // courseModelArrayList.add(new SubjectData("Settings", R.mipmap.ic_settings_foreground, Color.parseColor("#FD6FA0"),R.id.Settings));
       // courseModelArrayList.add(new SubjectData("Clients", R.mipmap.ic_clients_foreground, Color.parseColor("#FF6AF1B2"),R.id.FirstFragment));
        CourseGVAdapter adapter = new CourseGVAdapter(requireContext(), courseModelArrayList,MenuGrid.this);
        coursesGV.setAdapter(adapter);
        return clientMenusBinding.getRoot();
    }

    @Override
    public void onItemClick(View obj) {

    }

    public void onCardClick(SubjectData sub) {
        try {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("pagename", sub.getString1());
            editor.commit();
            NavHostFragment.findNavController(MenuGrid.this)
                    .navigate(sub.getRedirectId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
