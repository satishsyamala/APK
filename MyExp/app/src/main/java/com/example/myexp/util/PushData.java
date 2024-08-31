package com.example.myexp.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class PushData {

    public static void pushExpenses(String name, String url) {

        try {
            String clinetJson = JSONReadAndWrite.readJSON("expenses.json");
            JSONArray j = new JSONArray(clinetJson);
            JSONArray n = new JSONArray();
            String newName = "";
            for (int i = 0; i < j.length(); i++) {
                JSONObject o = j.getJSONObject(i);
                JSONObject req = new JSONObject();
                req.put("expName", o.getString("name"));
                req.put("expInfo", o.getString("info"));
                req.put("budget", Integer.parseInt(o.getString("budgetAmt")));
                System.out.println(req.toString());
                HTTPPushTask h = new HTTPPushTask(url + "/expense/save", req.toString());
                h.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushExpTypes(String name, String url) {

        try {
            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Exp Types"));
            boolean statusChange = false;
            for (List<String> s : data) {
                if (s.get(1).equalsIgnoreCase("pending")) {
                    JSONObject req = new JSONObject();
                    req.put("expName", name);
                    req.put("expTypeName", s.get(0));
                    System.out.println(req.toString());
                    HTTPPushTask h = new HTTPPushTask(url + "/exptype/save", req.toString());
                    h.start();
                    statusChange = true;
                    s.remove(1);
                    s.add("synced");

                }
            }
            if (statusChange) {
                CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "Exp Types"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushExpOn(String name, String url) {

        try {
            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Exp On"));
            boolean statusChange = false;
            for (List<String> s : data) {
                if (s.get(1).equalsIgnoreCase("pending")) {
                    JSONObject req = new JSONObject();
                    req.put("expName", name);
                    req.put("expOnName", s.get(0));
                    System.out.println(req.toString());
                    HTTPPushTask h = new HTTPPushTask(url + "/expon/save", req.toString());
                    h.start();
                    statusChange = true;
                    s.remove(1);
                    s.add("synced");

                }
            }
            if (statusChange) {
                CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "Exp On"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushExpDetails(String name, String url) {

        try {
            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
            boolean statusChange = false;
            for (List<String> s : data) {
                if (s.get(8).equalsIgnoreCase("pending") || s.get(8).equalsIgnoreCase("update")) {
                    JSONObject req = new JSONObject();
                    req.put("expName", name);
                    req.put("tranId", s.get(0));
                    req.put("tranDate", s.get(1));
                    req.put("expTypeName", s.get(2));
                    req.put("expOnName", s.get(3));
                    req.put("tranDesc", s.get(4));
                    req.put("tranQty", Integer.parseInt(s.get(5)));
                    req.put("tranAmt", Integer.parseInt(s.get(6)));
                    req.put("expChargeName", s.get(7));
                    System.out.println(req.toString());
                    HTTPPushTask h = new HTTPPushTask(url + "/expdetails/"+(s.get(8).equalsIgnoreCase("pending")?"save":"update"), req.toString());
                    h.start();
                    statusChange = true;
                    s.remove(8);
                    s.add("synced");

                }
            }
            if (statusChange) {
                CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushExpCharges(String name, String url) {

        try {
            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Charges"));
            boolean statusChange = false;
            for (List<String> s : data) {
                if (s.get(4).equalsIgnoreCase("pending") || s.get(4).equalsIgnoreCase("update")) {
                    JSONObject req = new JSONObject();
                    req.put("expName", name);
                    req.put("expChargesName", s.get(0));
                    req.put("contactName", s.get(1));
                    req.put("contactPhone", s.get(2));
                    req.put("expBudget", Integer.parseInt(s.get(3)));
                    System.out.println(req.toString());
                    HTTPPushTask h = new HTTPPushTask(url + "/expcharges/" + (s.get(4).equalsIgnoreCase("pending") ? "save" : "update"), req.toString());
                    h.start();
                    statusChange = true;
                    s.remove(4);
                    s.add("synced");

                }
            }
            if (statusChange) {
                CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "Charges"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public static void pushCashIn(String name, String url) {

            try {
                List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Cash In"));
                boolean statusChange = false;
                for (List<String> s : data) {
                    if (s.get(2).equalsIgnoreCase("pending") || s.get(2).equalsIgnoreCase("update")) {
                        JSONObject req = new JSONObject();
                        req.put("expName", name);
                        req.put("contactName", s.get(0));
                        req.put("contactPhone", s.get(1));
                        System.out.println(req.toString());
                        HTTPPushTask h = new HTTPPushTask(url + "/cashin/" + (s.get(2).equalsIgnoreCase("pending") ? "save" : "update"), req.toString());
                        h.start();
                        statusChange = true;
                        s.remove(2);
                        s.add("synced");

                    }
                }
                if (statusChange) {
                    CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "Cash In"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public static void pushAddCashDetails(String name, String url) {
        System.out.println("pushAddCashDetails  "+name);
        try {
            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
            System.out.println(data);
            boolean statusChange = false;
            for (List<String> s : data) {
                if (s.get(5).equalsIgnoreCase("pending") || s.get(5).equalsIgnoreCase("update")) {
                    JSONObject req = new JSONObject();
                    req.put("expName", name);
                    req.put("contactName", s.get(0));
                    req.put("addCashDate", s.get(1));
                    req.put("addCashInfo", s.get(2));
                    req.put("cashAmount", Integer.parseInt(s.get(3)));
                    req.put("tranId", s.get(4));
                    System.out.println(req.toString());
                    HTTPPushTask h = new HTTPPushTask(url + "/addcash/" + (s.get(5).equalsIgnoreCase("pending") ? "save" : "update"), req.toString());
                    h.start();
                    statusChange = true;
                    s.remove(5);
                    s.add("synced");

                }
            }
            if (statusChange) {
                CSVReadAndWrite.writeDataAtOnce(data, CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
