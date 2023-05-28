package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.databinding.AddClientBinding;
import com.example.wms.databinding.StHistoryBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class StHistory extends Fragment implements CustomListIntf,CustomDailogIntf {

    private StHistoryBinding binding;
    private AddClientBinding addClientBinding;
    ListView simpleList;
    ArrayList<SubjectData> arrayList;
    SharedPreferences sharedpreferences;
    private String clinetName;
    private String pageName;
    private String editOrDelete;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        arrayList = new ArrayList<SubjectData>();
        binding = StHistoryBinding.inflate(inflater, container, false);
        try {
            simpleList = binding.stHistoryLs;
            sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
            clinetName = sharedpreferences.getString("client", "NA");
            pageName = sharedpreferences.getString("pagename", "NA");
            CSVReadAndWrite.createClientFolder(clinetName + "/" + pageName);
            try {
                ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
            }catch (Exception e){}
            getClintsName();
            addClientBinding = AddClientBinding.inflate(inflater, container, false);
            binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHostFragment.findNavController(StHistory.this)
                            .navigate(R.id.StockTake);
                }
            });
            binding.btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHostFragment.findNavController(StHistory.this)
                            .navigate(R.id.MenuItem);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();

    }


    public void getClintsName() {
        try {
            arrayList = new ArrayList<>();
            String clinetJson = JSONReadAndWrite.readJSON(CSVReadAndWrite.jsonFileNames(clinetName, pageName));
            if (clinetJson != null) {
                JSONArray j = JSONReadAndWrite.sortJSONArray(new JSONArray(clinetJson));
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    String f1 = o.getString("date");
                    String f2 ="";
                    if(pageName.equalsIgnoreCase("Invoice"))
                        f2 = "Time : " + o.getString("datetime") + "\nAmount : "+o.getString("amount")+"\nMobile : "+o.getString("moblie")+"\nItem Count : " + o.getString("itemcount");
                   else
                    f2 = "Time : " + o.getString("datetime") + "\nItem Count : " + o.getString("itemcount");

                    arrayList.add(new SubjectData(f1, f2));
                }
            }
            showClientList();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void showClientList() {

            CustomAdapter customAdapter = new CustomAdapter(requireContext(), arrayList, StHistory.this);
            simpleList.setAdapter(customAdapter);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onItemClick(View obj) {
        try {
            TextView v = obj.findViewById(R.id.client_name);
            System.out.println(v.getText().toString());
            String date = v.getText().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("seldate", date);
            editor.putString("pagename", pageName);
            editor.commit();
            NavHostFragment.findNavController(StHistory.this)
                    .navigate(R.id.StockView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onView(String string1,String string2)
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("seldate", string1);
        editor.putString("pagename", pageName);
        editor.commit();
        NavHostFragment.findNavController(StHistory.this)
                .navigate(R.id.StockView);
    }

    @Override
    public void onEdit(String string1,String string2)
    {
        System.out.println("String 1 "+string1);
        editOrDelete=string1;
        CustomDailog c = new CustomDailog(null, StHistory.this, "Alert", "Do you want to Edit data?", 1);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDelete(String string1,String string2)
    {
        editOrDelete=string1;
        CustomDailog c = new CustomDailog(null, StHistory.this, "Alert", "Do you want to Delete data?", 2);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if(actionId==1)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("edit", "true");
            editor.putString("editdate", editOrDelete);
            editor.putString("pagename", pageName);
            editor.commit();
            NavHostFragment.findNavController(StHistory.this)
                    .navigate(R.id.StockTake);
        }else if(actionId==2){
            try {
                String clinetJson = JSONReadAndWrite.readJSON(CSVReadAndWrite.jsonFileNames(clinetName, pageName));
                JSONArray j = new JSONArray(clinetJson);
                JSONArray n = new JSONArray();
                for(int i=0;i<j.length();i++)
                {
                    JSONObject o=j.getJSONObject(i);
                    if(!o.getString("date").equalsIgnoreCase(editOrDelete))
                        n.put(o);
                }
                JSONReadAndWrite.writeJSONArrayfile(CSVReadAndWrite.jsonFileNames(clinetName, pageName),n);
                CSVReadAndWrite.deleteFile(CSVReadAndWrite.CSVFileNames(clinetName,pageName,editOrDelete));
                getClintsName();
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}