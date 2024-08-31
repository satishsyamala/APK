package com.example.myexp.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PullData implements PullDataintf {

    public void pullExpTypes(String name, String url, String type, String input) {

        HTTPPullTask h = new HTTPPullTask(url + "/" + type + "/latest", input, this, type, name);
        h.start();
    }

    @Override
    public void processData(String name, String type, String data) {
        if (data != null) {
            System.out.println("data pull table "+data);
            try {
                JSONArray jo = new JSONArray(data);
                List<String> ls = null;
                if (type.equalsIgnoreCase("exptype")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "Exp Types", 0);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("expTypeName"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("expTypeName"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Exp Types"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "Exp Types"));
                    }

                } else if (type.equalsIgnoreCase("expon")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "Exp On", 0);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("expOnName"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("expOnName"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Exp On"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "Exp On"));
                    }
                }else if (type.equalsIgnoreCase("expcharges")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "Charges", 0);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("expChargesName"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("expChargesName"));
                            r.add(d.getString("contactName"));
                            r.add(d.getString("contactPhone"));
                            r.add(d.getString("expBudget"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Charges"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "Charges"));
                    }
                }else if (type.equalsIgnoreCase("expdetails")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "Expenses", 0);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("tranId"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("tranId"));
                            r.add(d.getString("tranDate"));
                            r.add(d.getString("expTypeName"));
                            r.add(d.getString("expOnName"));
                            r.add(d.getString("tranDesc"));
                            r.add(d.getString("tranQty"));
                            r.add(d.getString("tranAmt"));
                            r.add(d.getString("expChargeName"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
                    }
                }else if (type.equalsIgnoreCase("cashin")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "Cash In", 0);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("contactName"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("contactName"));
                            r.add(d.getString("contactPhone"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Cash In"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "Cash In"));
                    }
                }else if (type.equalsIgnoreCase("addcash")) {
                    ls = CSVReadAndWrite.getDropdownValue(name, "AddCash", 4);
                    List<List<String>> dt = new ArrayList<>();
                    for (int i = 0; i < jo.length(); i++) {
                        JSONObject d = jo.getJSONObject(i);
                        if (!ls.contains(d.getString("tranId"))) {
                            List<String> r = new ArrayList<>();
                            r.add(d.getString("contactName"));
                            r.add(d.getString("addCashDate"));
                            r.add(d.getString("addCashInfo"));
                            r.add(d.getString("cashAmount"));
                            r.add(d.getString("tranId"));
                            r.add("synced");
                            dt.add(r);
                        }
                    }
                    if (!dt.isEmpty()) {
                        List<List<String>> or = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
                        or.addAll(dt);
                        CSVReadAndWrite.writeDataAtOnce(or, CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

