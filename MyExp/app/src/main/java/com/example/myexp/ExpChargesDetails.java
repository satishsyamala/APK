package com.example.myexp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.myexp.dailog.CustomDailog;
import com.example.myexp.dailog.CustomDailogIntf;
import com.example.myexp.databinding.AddExpChargesBinding;
import com.example.myexp.databinding.ExpChargesBinding;
import com.example.myexp.databinding.ExpChargesDetailsBinding;
import com.example.myexp.databinding.ExpFilterBinding;
import com.example.myexp.listview.CustomListIntf;
import com.example.myexp.listview.SubjectData;
import com.example.myexp.util.CSVReadAndWrite;
import com.example.myexp.util.GeneralUtil;
import com.example.myexp.util.PDFUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpChargesDetails extends Fragment implements CustomDailogIntf, CustomListIntf {
    private ExpChargesDetailsBinding expChargesBinding;
    private SharedPreferences sharedpreferences;
    private ExpFilterBinding expFilterBinding;
    private ListView simpleList;
    private String clinetName;
    private String pageName;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final String LOG_TAG = "AndroidExample";
    private EditText editTextPath;
    private String path;
    private List<List<String>> stocks;
    private List<List<String>> shareList;
    private List<String> seletedItem;
    private int totalExp;
    private int paidExp;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("chargename", "NA");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        } catch (Exception e) {
        }
        expChargesBinding = ExpChargesDetailsBinding.inflate(inflater, container, false);
        simpleList = expChargesBinding.expseList;
        getStock();
        tableData(null, null);
        expChargesBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    String file = clinetName + "/share/"+seldate+"/"+(new SimpleDateFormat("HH-mm-ss").format(new Date()))+".csv";
                    CSVReadAndWrite.createClientFolder(clinetName + "/share/"+seldate);
                    List<List<String>> reportData = new ArrayList<>();
                    List<List<String>> ls =  GeneralUtil.geListCharges(clinetName,pageName);
                    List<String> headers = new ArrayList<>();
                    headers.add("Date");
                    headers.add("Expense Type");
                    headers.add("Description");
                    headers.add("Amount");
                    reportData.add(headers);
                    int amount = 0;
                    for (List<String> a : ls) {
                        List<String> data = new ArrayList<>();
                        data.add(a.get(1));
                        data.add(a.get(2));
                        data.add(a.get(4));
                        data.add(a.get(6));
                        amount += GeneralUtil.stringToDouble(a.get(6));
                        reportData.add(data);
                    }
                    List<String> footer = new ArrayList<>();
                    footer.add("");
                    footer.add("");
                    footer.add("Total");
                    footer.add("" + amount);
                    reportData.add(footer);
                    CSVReadAndWrite.writeDataAtOnce(reportData, file);
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = pageName+" Expenses";
                    String shareSub = pageName+" Expenses";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    List<String> reh=new ArrayList<>();
                    int totCash=totalExp;
                    reh.add("Total Amount : "+GeneralUtil.doubleFarmate(totCash));
                    reh.add("Paid Amount : "+GeneralUtil.doubleFarmate(amount));
                    reh.add("Balance Amount : "+GeneralUtil.doubleFarmate(totCash-amount));
                    List<Integer> nc = new ArrayList<>();
                    nc.add(4);
                    float[] columnWidths = {1.25f, 2.5f, 2f, 2.5f};
                    String myFilePath = PDFUtil.createPDF(clinetName, reh, reportData, nc, columnWidths);
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return expChargesBinding.getRoot();

    }


    public void getStock() {
        stocks = new ArrayList<>();
        stocks = GeneralUtil.geListCharges(clinetName,pageName);
        ;
    }



    public void tableData(String searchValue, String searchValue1) {
        try {
            totalExp=GeneralUtil.geChargeAmount(clinetName,pageName);
            paidExp=0;
            ArrayList<SubjectData> subData = new ArrayList<>();
            for (List<String> ls : stocks) {
               subData.add(getSubjectDataForList(ls));
            }
            CustomAdapter customAdapter = new CustomAdapter(requireContext(), subData, ExpChargesDetails.this, "expenses");
            simpleList.setAdapter(customAdapter);
            expChargesBinding.totalExpAmount.setText(GeneralUtil.doubleFarmate(paidExp)+"/"+GeneralUtil.doubleFarmate(totalExp)+" ("+GeneralUtil.doubleFarmate(totalExp-paidExp)+")");
        } catch (Exception w) {

        }
    }


    public SubjectData getSubjectDataForList(List<String> ls) {
        paidExp+=GeneralUtil.stringToDouble(ls.get(6));
        SubjectData s = new SubjectData(ls.get(2) + "/" + ls.get(3), ls.get(1), "Rs : " + ls.get(6), ls.get(4)+" / "+ls.get(7));
        s.setImage(ls.get(0));
        return s;
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onItemClick(View obj) {

    }
}
