package com.example.myexp.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GeneralUtil {

    public static double stringToDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String doubleFarmate(double amount)
    {
        return  (new DecimalFormat("##,##,##,##,###")).format(amount);
    }
    public static String doubleFarmate(String amount)
    {
        try {
        return  (new DecimalFormat("##,##,##,##,###")).format(Double.parseDouble(amount));
        } catch (Exception e) {
            return amount;
        }
    }

    public static int getTotalExpense(String name) {
        int amount = 0;
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
        for (List<String> a : ls) {
            amount += GeneralUtil.stringToDouble(a.get(6));
        }
        return amount;
    }

    public static int getTotalCharges(String name, String charge) {
        int amount = 0;
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
        for (List<String> a : ls) {
            if (a.get(7).equalsIgnoreCase(charge))
                amount += GeneralUtil.stringToDouble(a.get(6));
        }
        return amount;
    }

    public static int getCashIn(String name, String charge) {
        int amount = 0;
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
        for (List<String> a : ls) {
            if (a.get(0).equalsIgnoreCase(charge))
                amount += GeneralUtil.stringToDouble(a.get(3));
        }
        return amount;
    }

    public static int getCashIn(String name) {
        int amount = 0;
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
        for (List<String> a : ls) {
            amount += GeneralUtil.stringToDouble(a.get(3));
        }
        return amount;
    }

    public static int geChargeAmount(String name, String charge) {

        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Charges"));
        for (List<String> a : ls) {
            if (a.get(0).equalsIgnoreCase(charge))
                return Integer.parseInt(a.get(3));
        }
        return 0;
    }

    public static List<List<String>> geListCharges(String name, String charge) {
        List<List<String>> data = new ArrayList<>();
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "Expenses"));
        for (List<String> a : ls) {
            if (a.get(7).equalsIgnoreCase(charge))
                data.add(a);
        }
        return data;
    }

    public static List<List<String>> geAddCash(String name, String charge) {
        List<List<String>> data = new ArrayList<>();
        List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(name, "AddCash"));
        for (List<String> a : ls) {
            if (a.get(0).equalsIgnoreCase(charge))
                data.add(a);
        }
        return data;
    }

    public static void sortExpenses(List<List<String>> exp) {
        Collections.sort(exp, new Comparator<List<String>>() {
                    @Override
                    public int compare(List<String> o1, List<String> o2) {
                        if (!o1.get(1).equalsIgnoreCase(o2.get(1)))
                            return stringDate(o2.get(1)).compareTo(stringDate(o1.get(1)));
                        else
                            return o2.get(0).compareTo(o1.get(0));
                    }
                }
        );
    }

    public static boolean fromDateVal(String date,String fromDate)
    {
        System.out.println("fromDate "+fromDate+" Date : "+date);
        if(stringDate(fromDate).before(stringDate(date)) || stringDate(fromDate).equals(stringDate(date)))
            return true;
        return false;
    }

    public static boolean toDateVal(String date,String toDate)
    {
        System.out.println("ToDate "+toDate+" Date : "+date);
        try {
            if (stringDate(toDate).after(stringDate(date)) || stringDate(toDate).equals(stringDate(date)))
                return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static Date stringDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }
}
