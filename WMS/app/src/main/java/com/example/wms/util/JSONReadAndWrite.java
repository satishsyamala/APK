package com.example.wms.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class JSONReadAndWrite {


    public static void writeJSONfile(String filename, JSONObject finalJSON) {
        try {
            File f = new File(CSVReadAndWrite.getBaseFileWithFolder() + "/" + filename);
            if (f.exists())
                  f.delete();
            FileWriter file = new FileWriter(CSVReadAndWrite.getBaseFileWithFolder() + "/" + filename);
            file.write(AESEncyption.encrypt(finalJSON.toString()));
            file.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void writeJSONArrayfile(String filename, JSONArray finalJSON) {
        try {
            System.out.println("Write : " + finalJSON.toString());
            File f = new File(CSVReadAndWrite.getBaseFileWithFolder() + "/" + filename);
            System.out.println(f.getAbsoluteFile());
            if (f.exists())
                f.delete();
            FileWriter file = new FileWriter(CSVReadAndWrite.getBaseFileWithFolder() + "/" + filename);
            file.write(AESEncyption.encrypt(finalJSON.toString()));
            file.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readJSON(String filename) {
        try {
            File file = new File(CSVReadAndWrite.getBaseFileWithFolder() + "/" + filename);
            System.out.println("Read : " + file.getAbsoluteFile());
            if (file.exists()) {
                System.out.println("Read : " + file.getAbsoluteFile());
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                System.out.println("Read : " + stringBuilder.toString());
                try {
                    return AESEncyption.decrypt(stringBuilder.toString());
                }catch (Exception e){
                    return stringBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray sortJSONArray(JSONArray jsonArray) {
        JSONArray sortedJsonArray = new JSONArray();
        try {
            List<JSONObject> list = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getJSONObject(i));
            }
            System.out.println("Before Sorted JSONArray: " + jsonArray);
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
            String KEY_NAME = "date";
            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    JSONObject a = (JSONObject) o1;
                    JSONObject b = (JSONObject) o2;
                    try {
                        Date d1 = sd.parse(a.getString(KEY_NAME));
                        Date d3 = sd.parse(b.getString(KEY_NAME));
                        return d3.compareTo(d1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }


            });
            for (int i = 0; i < jsonArray.length(); i++) {
                sortedJsonArray.put(list.get(i));
            }
        } catch (Exception e) {
        }
        return sortedJsonArray;
    }


}
