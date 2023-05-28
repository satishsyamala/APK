package com.example.wms.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Random;

public class AppActivation {


    public static int appToken() {
        Random r = new Random();
        int i1 = r.nextInt(999999 - 100000) + 100000;
        return i1;
    }

    public static String getActivationCode(String deviceId, int token, int versionCode) {
        String code = "";
        char[] a = deviceId.toCharArray();
        int sumt = codeSum(token);
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            int v = 0;
            try {
                v = Integer.parseInt(a[i] + "");
            } catch (Exception e) {
                v = a[i];
            }
            if (i % versionCode == 0)
                sum += v;
        }
        String st1 = sumt + "";
        String st2 = sum + "";
        int length = st1.length() > st2.length() ? st1.length() : st2.length();
        for (int i = 0; i < length; i++) {
            if (st1.length() > i)
                code += st1.charAt(i);
            else
                code += "" + versionCode;
            if (st2.length() > i)
                code += st2.charAt(i);
            else
                code += "" + versionCode;
        }
        return code;
    }

    public static int codeSum(int token) {
        int sum = 0;
        while (token > 0) {
            int r = token % 10;
            sum += r;
            token = token / 10;
        }
        return sum;
    }

    public static String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                try {
                    deviceUniqueIdentifier = tm.getDeviceId();
                } catch (Exception e) {
                    //  e.printStackTrace();
                }
            }
            if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
                deviceUniqueIdentifier = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceUniqueIdentifier;
    }

}
