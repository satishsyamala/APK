package com.example.codegen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
     EditText ver;
     EditText tok;
     EditText deid;
     TextView cod;
     Button gen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ver=findViewById(R.id.app_version);
        tok=findViewById(R.id.app_token);
        deid=findViewById(R.id.app_device_id);
        cod=findViewById(R.id.act_code);
        gen=findViewById(R.id.activate);
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation(ver.getText()) && validation(tok.getText()) && validation(deid.getText()))
                {
                    String code=AppActivation.getActivationCode(deid.getText().toString(),Integer.parseInt(tok.getText().toString()),Integer.parseInt(ver.getText().toString()));
                    cod.setText(code);
                }
                else{
                    Toast.makeText(MainActivity.this, "Required all Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean validation(Object text)
    {

        if(text!=null && text.toString().trim().length()>0)
            return true;
        else
            return false;
    }
}