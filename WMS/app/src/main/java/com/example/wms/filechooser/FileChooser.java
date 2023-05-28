package com.example.wms.filechooser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.wms.listview.CustomListIntf;
import com.example.wms.util.FileUtils;

public class FileChooser {
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private CustomListIntf customListIntf;
    private static final String LOG_TAG = "AndroidExample";
    private Fragment  fragment;
    private String path;

    public  FileChooser(Fragment  fragment)
    {
        this.customListIntf=customListIntf;
        this.fragment=fragment;
    }

    public void askPermissionAndBrowseFile()  {
              doBrowseFile();
    }

    private void doBrowseFile()  {
        System.out.println("filePath");
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        fragment.startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
        System.out.println(" End ");
    }







    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(" requestCode "+requestCode);
        System.out.println(" requestCode "+requestCode);
        System.out.println(" resultCode "+resultCode);
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        Uri fileUri = data.getData();
                        Log.i(LOG_TAG, "Uri: " + fileUri);

                        String filePath = null;
                        try {
                            filePath = FileUtils.getPath(fragment.getContext(),fileUri);
                        } catch (Exception e) {
                            Log.e(LOG_TAG,"Error: " + e);
                            Toast.makeText(fragment.getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                        System.out.println(filePath);
                        path=filePath;
                    }
                }
                break;
        }
        fragment.onActivityResult(requestCode, resultCode, data);
    }
    public String getPath()
    {
        return path;
    }
}
