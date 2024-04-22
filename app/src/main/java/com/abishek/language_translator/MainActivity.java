package com.abishek.language_translator;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity{

    private Button src;

    String lang;
    String lang2;

    Spinner languageSpinner, languageSpinner2;
    String[] languages = {"Select Language", "Hindi","Tamil","English","Chinese"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on below line we are initializing our variables.
        languageSpinner = findViewById(R.id.spinner_languages);
        languageSpinner2 = findViewById(R.id.spinner_languages2);
        src = findViewById(R.id.btn_src);
        lang = "";
        lang2 = "";

        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,languages);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        languageSpinner2.setAdapter(adapter);
        languageSpinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition("Select Language");
        languageSpinner2.setSelection(spinnerPosition);
        languageSpinner.setSelection(spinnerPosition);



        src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = languageSpinner.getSelectedItem().toString();
                lang2 = languageSpinner2.getSelectedItem().toString();
                      if(lang=="Hindi" & lang2=="English"){
                    //case "Tamil":
                        //langcode.setText("TA");
                        //return langCode;
                        //Intent t = new Intent(MainActivity.this,MainActivity.class);
                        //t.putExtra("lang_key","TA");
                        //startActivity(t);
                        //break;
                    //case "English":
                        //Intent e = new Intent(MainActivity.this,MainActivity.class);
                        //e.putExtra("lang_key","EN");
                        //startActivity(e);
                        //break;
                    //case "Hindi":
                        Intent h = new Intent(MainActivity.this,MainActivity3.class);
                        h.putExtra("lang_key","HI");
                        startActivity(h);
                        }
                    //case "Telugu":
                        //Intent te = new Intent(MainActivity.this,MainActivity.class);
                        //te.putExtra("lang_key","TE");
                        //startActivity(te);
                        //break;
                    //case "Malay":
                        //Intent m = new Intent(MainActivity.this,MainActivity.class);
                        //m.putExtra("lang_key","MS");
                        //startActivity(m);
                        //break;
                    //case "Kannada":
                        //Intent k = new Intent(MainActivity.this,MainActivity.class);
                        //k.putExtra("lang_key","KN");
                        //startActivity(k);
                        //break;
                     else if(lang=="Chinese"& lang2=="English"){
                        Intent c = new Intent(MainActivity.this,MainActivity2.class);
                        c.putExtra("lang_key","ZH");
                        startActivity(c);}
                       // break;
                     else if(lang=="English" & lang2=="Tamil"){
                         Intent t = new Intent(MainActivity.this,MainActivity4.class);
                         t.putExtra("lang_key","ZH");
                         t.putExtra("lang_key2","TA");
                         startActivity(t);
                     }
                }
            }
        );


    }

}
