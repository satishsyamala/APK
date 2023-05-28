package com.example.wms.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.example.wms.listview.SubjectData;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReadAndWrite {

    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    public static File getBaseFile() {
        System.out.println("Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);
        System.out.println("Build.VERSION_CODES.GINGERBREAD_MR1 " + Build.VERSION.RELEASE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            System.out.println(" if " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            System.out.println(" Parent " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParent());
            System.out.println(" Else " + Environment.getExternalStorageDirectory().toString());
            System.out.println(" media State " + Environment.getExternalStorageState());
            System.out.println(" media isExternalStorageEmulated " + Environment.isExternalStorageEmulated());
        } else {
            System.out.println(" Else " + Environment.getExternalStorageDirectory().toString());
        }
        if (Double.parseDouble(Build.VERSION.RELEASE) >= 10)
            return Environment.getExternalStorageDirectory();
        else
            return Environment.getExternalStorageDirectory();
    }


    public static File getBaseFile1() {
        return Environment.getExternalStorageDirectory();
    }

    public static File getBaseFileWithFolder1() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/WMS");
    }

    public static File getBaseFileWithFolder() {
        return new File(getBaseFile() + "/WMS");
    }

    public static void createBaseFolder() {
        try {

            File f2 = getBaseFile();
            for (File s : f2.listFiles()) {
                System.out.println("Folder Created " + s.getAbsolutePath());
            }

            File f = new File(getBaseFile() + "/WMS");
            System.out.println(("Folder Created " + f.getAbsolutePath()));
            if (!f.isDirectory()) {
                f.mkdir();
                System.out.println("Folder Created " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createClientFolder(String clientName) {
        File f = new File(getBaseFileWithFolder() + "/" + clientName);
        System.out.println((f.getAbsolutePath()));
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }


    public static JSONObject getStockDetails(String clientName) {
        JSONObject st = new JSONObject();
        try {
            List<List<String>> ls = readCSVfile(clientName + "/Stocks.csv");
            for (List<String> s : ls) {
                JSONObject item = new JSONObject();
                item.put("code", s.get(0));
                item.put("name", s.get(1));
                item.put("price", s.get(2));
                item.put("barcode", s.get(3));
                if (s.size() == 5)
                    item.put("carton", s.get(4));
                else
                    item.put("carton", 0);
                st.put(s.get(3), item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(st.toString());
        return st;
    }

    public static List<List<String>> csvToList(String clientName) {
        return readCSVfile(clientName + "/Stocks.csv");
    }


    public static List<List<String>> readCSVfile(String path) {

        List<List<String>> data = new ArrayList<>();
        try {
            File m = getBaseFileWithFolder();
            File file = new File(m + "/" + path);
            System.out.println("F " + file.getAbsolutePath());
            if (file.exists()) {
                CSVReader reader = new CSVReader(new FileReader(file));
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    List<String> ls = new ArrayList<>();
                    for (String s : nextLine) {
                        ls.add(s);
                    }
                    data.add(ls);
                    // System.out.println(nextLine[0] + nextLine[1] + "etc...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<List<String>> readCSVFromFolder(String path) {
        List<List<String>> data = new ArrayList<>();
        try {
            File file = new File(path);

            if (file.exists()) {

                CSVReader reader = new CSVReader(new FileReader(file));
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    List<String> ls = new ArrayList<>();
                    for (String s : nextLine) {
                        ls.add(s);
                    }
                    data.add(ls);
                    // System.out.println(nextLine[0] + nextLine[1] + "etc...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeDataAtOnce(List<List<String>> data, String filePath) {
        File m = getBaseFileWithFolder();
        File file = new File(m + "/" + filePath);
        try {
            if (file.exists())
                file.delete();
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(listToArray(data));
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath) {
        File m = getBaseFileWithFolder();
        File file = new File(m + "/" + filePath);
        try {
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(String filePath) {
        File m = getBaseFileWithFolder();
        File file = new File(m + "/" + filePath);
        try {
            if (file.isDirectory())
                file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renameFolder(String oldName, String newName) {
        File m = getBaseFileWithFolder();
        File file = new File(m + "/" + oldName);
        try {
            if (file.isDirectory()) {
                File destFile = new File(m + "/" + newName);
                file.renameTo(destFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<String[]> listToArray(List<List<String>> data) {
        List<String[]> res = new ArrayList<>();
        for (List<String> ls : data) {
            res.add(ls.toArray(new String[0]));
        }
        return res;
    }

    public static String previousDate(String date) {
        String result = date;
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, -1);
            Date dateWith5Days = cal.getTime();
            result = new SimpleDateFormat("dd-MM-yyyy").format(dateWith5Days);
        } catch (Exception e) {

        }
        return result;
    }

    public static Date addDays(Date date, int days) {
        Date result = null;
        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            result = cal.getTime();

        } catch (Exception e) {
            result = date;
        }
        return result;
    }


    public static String jsonFileNames(String clientName, String pageName) {
        if (pageName.equalsIgnoreCase("Stock Take"))
            return clientName + "/" + pageName + "/ST_HISTORY.json";
        else if (pageName.equalsIgnoreCase("Purchases"))
            return clientName + "/" + pageName + "/STOCK_IN.json";
        else if (pageName.equalsIgnoreCase("Stock Out"))
            return clientName + "/" + pageName + "/STOCK_OUT.json";
        else if (pageName.equalsIgnoreCase("Sales"))
            return clientName + "/" + pageName + "/SALES.json";
        else if (pageName.equalsIgnoreCase("Invoice"))
            return clientName + "/" + pageName + "/INVOICE.json";
        else
            return "NA";
    }

    public static String CSVFileNames(String clientName, String pageName, String selDate) {
        if (pageName.equalsIgnoreCase("Stock Take"))
            return clientName + "/" + pageName + "/ST_" + selDate + ".csv";
        else if (pageName.equalsIgnoreCase("Purchases"))
            return clientName + "/" + pageName + "/SI_" + selDate + ".csv";
        else if (pageName.equalsIgnoreCase("Stock Out"))
            return clientName + "/" + pageName + "/SO_" + selDate + ".csv";
        else if (pageName.equalsIgnoreCase("Sales"))
            return clientName + "/" + pageName + "/SAL_" + selDate + ".csv";
        else if (pageName.equalsIgnoreCase("Invoice"))
            return clientName + "/" + pageName + "/INV_" + selDate + ".csv";
        else
            return "NA";
    }

    public static String tempCSVFileNames(String clientName, String pageName) {
        if (pageName.equalsIgnoreCase("Stock Take"))
            return clientName + "/" + pageName + "/ST_TEMP.csv";
        else if (pageName.equalsIgnoreCase("Purchases"))
            return clientName + "/" + pageName + "/SI_TEMP.csv";
        else if (pageName.equalsIgnoreCase("Stock Out"))
            return clientName + "/" + pageName + "/SO_TEMP.csv";
        else if (pageName.equalsIgnoreCase("Sales"))
            return clientName + "/" + pageName + "/SAL_TEMP.csv";
        else if (pageName.equalsIgnoreCase("Invoice"))
            return clientName + "/" + pageName + "/INV_TEMP.csv";
        else
            return "NA";
    }

    public static String tempJSONFileNames(String clientName, String pageName) {
        if (pageName.equalsIgnoreCase("Stock Take"))
            return clientName + "/" + pageName + "/ST_TEMP.json";
        else if (pageName.equalsIgnoreCase("Purchases"))
            return clientName + "/" + pageName + "/SI_TEMP.json";
        else if (pageName.equalsIgnoreCase("Stock Out"))
            return clientName + "/" + pageName + "/SO_TEMP.json";
        else if (pageName.equalsIgnoreCase("Sales"))
            return clientName + "/" + pageName + "/SAL_TEMP.json";
        else if (pageName.equalsIgnoreCase("Invoice"))
            return clientName + "/" + pageName + "/INV_TEMP.json";
        else
            return "NA";
    }

    public static void removeKeyFromPS(SharedPreferences sharedpreferences, String key) {
        try {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove(key);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public static Date previousStockTakeDate(Date data, String clinet) {
        Date d = null;
        try {
            String clinetJson = JSONReadAndWrite.readJSON(CSVReadAndWrite.jsonFileNames(clinet, "Stock Take"));
            if (clinetJson != null) {
                JSONArray j = JSONReadAndWrite.sortJSONArray(new JSONArray(clinetJson));
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    Date st = stringToDate(o.getString("date"));
                    if (st.before(data)) {
                        d = st;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return d;
    }

    public static Date stringToDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static String dateToString(Date date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static Map<String, Integer> transactions(Date fromDat, Date todate, String client, String madule) {
        Map<String, Integer> map = new HashMap<>();
        try {
            Date temp = (Date) fromDat.clone();
            while (temp.before(todate)) {
                List<List<String>> si = readCSVfile(CSVFileNames(client, madule, dateToString(temp)));
                for (List<String> s : si) {
                    int qty = Integer.parseInt(s.get(2));
                    map.put(s.get(0), map.containsKey(s.get(0)) ? (map.get(s.get(0)) + qty) : qty);
                }
                temp.setDate(temp.getDate() + 1);
            }
        } catch (Exception e) {
        }
        return map;
    }

    public static Map<String, Integer> invoiceData(Date fromDat, Date todate, String client, String madule) {
        Map<String, Integer> map = new HashMap<>();
        try {
            Date temp = (Date) fromDat.clone();
            while (temp.before(todate)) {
                List<List<String>> si = readCSVfile(CSVFileNames(client, madule, dateToString(temp)));
                for (List<String> s : si) {
                    int qty = Integer.parseInt(s.get(2));
                    map.put(s.get(0), map.containsKey(s.get(0)) ? (map.get(s.get(0)) + qty) : qty);
                }
                temp.setDate(temp.getDate() + 1);
            }
        } catch (Exception e) {
        }
        return map;
    }


}
