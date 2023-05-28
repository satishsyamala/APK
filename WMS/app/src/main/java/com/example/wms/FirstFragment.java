package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.databinding.FragmentFirstBinding;
import com.example.wms.databinding.AddClientBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends Fragment implements CustomDailogIntf, CustomListIntf {

    private FragmentFirstBinding binding;
    private AddClientBinding addClientBinding;
    ListView simpleList;
    ArrayList<SubjectData> arrayList;
    SharedPreferences sharedpreferences;
    private TableLayout tableLayout;
    private List<String> delete;
    private String editOrDelete;
    private String string2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        arrayList = new ArrayList<SubjectData>();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        simpleList = binding.clientView;
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        getClintsName();
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } catch (Exception e) {
        }
        addClientBinding = AddClientBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void getClintsName() {
        try {
            arrayList = new ArrayList<>();
            String clinetJson = JSONReadAndWrite.readJSON("client.json");
            if (clinetJson != null) {
                JSONArray j = new JSONArray(clinetJson);
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    arrayList.add(new SubjectData(o.getString("name"), o.getString("address")));

                }
            }
            showClientList();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void showClientList() {
        CustomAdapter customAdapter = new CustomAdapter(requireContext(), arrayList, FirstFragment.this);
        simpleList.setAdapter(customAdapter);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDailog c = new CustomDailog(addClientBinding.getRoot(), FirstFragment.this, "Add Client", null);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if (actionId == 0) {
            try {
                String clinetJson = JSONReadAndWrite.readJSON("client.json");
                JSONArray j = null;
                if (clinetJson != null) {
                    j = new JSONArray(clinetJson);
                } else
                    j = new JSONArray();
                JSONObject jo = new JSONObject();
                jo.put("name", addClientBinding.clientName.getText() != null ? addClientBinding.clientName.getText().toString() : "");
                jo.put("address", addClientBinding.address.getText() != null ? addClientBinding.address.getText().toString() : "");
                j.put(jo);
                JSONReadAndWrite.writeJSONArrayfile("client.json", j);
                CSVReadAndWrite.createClientFolder(addClientBinding.clientName.getText().toString());
                JSONObject a = new JSONObject();
                a.put("pergedays", "15");
                a.put("rdv", "false");
                JSONReadAndWrite.writeJSONfile(addClientBinding.clientName.getText().toString()+"/setting.json",a);
                getClintsName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (actionId == 1) {
            delete = new ArrayList<>();
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                View child = tableLayout.getChildAt(i);
                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;
                    for (int x = 0; x < row.getChildCount(); x++) {
                        CheckBox c = (CheckBox) row.getChildAt(x); // get child index on particular row
                        if (c.isChecked())
                            delete.add(c.getText().toString());
                        System.out.println(c.getText() + " : " + c.isSelected() + " " + c.isChecked());
                    }
                }
            }
            if (!delete.isEmpty()) {
                CustomDailog c = new CustomDailog(null, FirstFragment.this, "Alert", "Do you want delete client?", 2);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }

        } else if (actionId == 2) {
            try {
                String clinetJson = JSONReadAndWrite.readJSON("client.json");
                JSONArray j = new JSONArray(clinetJson);
                JSONArray n = new JSONArray();
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    if (delete.contains(o.getString("name"))) {
                        CSVReadAndWrite.deleteFolder(o.getString("name"));
                    } else {
                        n.put(o);
                    }
                }
                delete = new ArrayList<>();
                JSONReadAndWrite.writeJSONArrayfile("client.json", n);
                getClintsName();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (actionId == 11) {
            try {
                String clinetJson = JSONReadAndWrite.readJSON("client.json");
                JSONArray j = new JSONArray(clinetJson);
                JSONArray n = new JSONArray();
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    if (!editOrDelete.equalsIgnoreCase(o.getString("name"))) {
                        n.put(o);
                    }
                }
                CSVReadAndWrite.deleteFolder(editOrDelete);
                JSONReadAndWrite.writeJSONArrayfile("client.json", n);
                getClintsName();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(actionId==10)
        {
            addClientBinding.clientName.setText(editOrDelete);
            addClientBinding.address.setText(string2);
            CustomDailog c = new CustomDailog(addClientBinding.getRoot(), FirstFragment.this, "Edit Client", null,12);
            c.show(getFragmentManager(), "NoticeDialogFragment");
        }else if(actionId==12)
        {
            try {
            String clinetJson = JSONReadAndWrite.readJSON("client.json");
            JSONArray j = new JSONArray(clinetJson);
            JSONArray n = new JSONArray();
            String newName="";
            for (int i = 0; i < j.length(); i++) {
                JSONObject o = j.getJSONObject(i);
                if (editOrDelete.equalsIgnoreCase(o.getString("name"))) {
                    newName=addClientBinding.clientName.getText() != null ? addClientBinding.clientName.getText().toString():"";
                    o.put("name",newName);
                    o.put("address", addClientBinding.address.getText() != null ? addClientBinding.address.getText().toString() : "");
                  }
                n.put(o);
            }
            CSVReadAndWrite.renameFolder(editOrDelete,newName);
            JSONReadAndWrite.writeJSONArrayfile("client.json", n);
            getClintsName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onItemClick(View obj) {
        try {
            TextView v = obj.findViewById(R.id.client_name);
            System.out.println(v.getText().toString());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("client", v.getText().toString());
            editor.commit();
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.MenuItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onView(String string1, String string2) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("client", string1);
        editor.commit();
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.SecondFragment);
    }

    @Override
    public void onEdit(String string1, String string2) {
        System.out.println("String 1 " + string1);
        editOrDelete = string1;
        this.string2=string2;
        CustomDailog c = new CustomDailog(null, FirstFragment.this, "Alert", "Do you want to Edit Client?", 10);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDelete(String string1, String string2) {
        editOrDelete = string1;
        CustomDailog c = new CustomDailog(null, FirstFragment.this, "Alert", "Do you want to Delete Client?", 11);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }
}