package com.abishek.language_translator;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import android.os.Bundle;

import javax.security.auth.callback.Callback;

import okhttp3.Response;

public class MainActivity4 extends AppCompatActivity {
    TextView txt;
    String s1,s2;
    private EditText edtLanguage;
    private TextView translateLanguageTV, filePath, fileUri, langcode;
    private Button translateLanguageBtn, convert;
    private Button clearBtn, fileBtn, src, dest;
    private ImageButton copyButton;
    String sourceText;
    String lang;

    // create a variable for our
    // firebase language translator.
    FirebaseTranslator englishGermanTranslator;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    ActivityResultLauncher<Intent> resultLauncher;
    PdfToTextConverter pd = new PdfToTextConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        edtLanguage = findViewById(R.id.idEdtLanguage);
        translateLanguageTV = findViewById(R.id.idTVTranslatedLanguage);
        translateLanguageBtn = findViewById(R.id.idBtnTranslateLanguage);
        copyButton = findViewById(R.id.idcopytext);
        clearBtn = findViewById(R.id.idCleartext);
        fileBtn = findViewById(R.id.btn_choose_file);
        filePath = findViewById(R.id.tv_file_path);
        fileUri = findViewById(R.id.tv_file_uri);
        src = findViewById(R.id.btn_src);
        convert = findViewById(R.id.btn_convert);
        lang = "";
        Intent i = getIntent();
        s1 =i.getStringExtra("lang_key");
        s2 = i.getStringExtra("lang_key2");
        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();


        // on below line we are creating our firebase translate option.

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        // below line we are specifying our source language.
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        // in below line we are displaying our target language.
                        .setTargetLanguage(FirebaseTranslateLanguage.TA)
                        // after that we are building our options.
                        .build();
        // below line is to get instance
        // for firebase natural language.
        englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(
                            ActivityResult result)
                    {
                        // Initialize result data
                        Intent data = result.getData();
                        // check condition

                        if (data != null) {
                            // When data is not equal to empty
                            // Get PDf uri
                            Uri sUri = data.getData();
                            byte[] byteData = getBytes(MainActivity4.this, sUri);

                            //edtLanguage.setText(new String(byteData));

                            String val = new String(byteData);

                            Intent j = new Intent(MainActivity4.this,MainActivity4.class);
                            j.putExtra("byte", val);
                            startActivity(j);

                            // Get PDF path
                            String sPath = sUri.getPath();
                            fileUri.setText(Html.fromHtml(
                                    "<big><b>PDF Uri</b></big><br>"
                                            + sUri));
                            // Set path on text view
                            filePath.setText(Html.fromHtml(
                                    "<big><b>PDF Path</b></big><br>"
                                            + sPath));
                        }
                    }
                });
        fileBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v)
                    {
                        // check condition
                        if (ActivityCompat.checkSelfPermission(
                                MainActivity4.this,
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE)
                                != PackageManager
                                .PERMISSION_GRANTED) {
                            // When permission is not granted
                            // Result permission
                            ActivityCompat.requestPermissions(
                                    MainActivity4.this,
                                    new String[] {
                                            Manifest.permission
                                                    .READ_EXTERNAL_STORAGE },
                                    1);
                        }
                        else {
                            // When permission is granted
                            // Create method
                            selectPDF();
                        }
                    }
                });

        // adding on click listener for button
        translateLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling method to download language
                // modal to which we have to translate.
                //sourceText = edtLanguage.getText().toString();

                sourceText = i.getStringExtra("byte");

                identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals("und")){
                            Toast.makeText(getApplicationContext(),"Language Not Identified",Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(getApplicationContext(), s,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //String string = edtLanguage.getText().toString();
                downloadModal(sourceText);
            }
        });
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.convertPdfToText("path/to/your/pdf/file.pdf", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle the failure scenario
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String convertedText = response.body().string();
                            // Process the converted text
                        } else {
                            // Handle the non-successful response
                        }
                        response.close();
                    }
                });
            }
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = translateLanguageTV.getText().toString();
                myClip = ClipData.newPlainText("result", tmp);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateLanguageTV.setText("");
                edtLanguage.setText("");
                fileUri.setText("");
                filePath.setText("");
                Toast.makeText(MainActivity4.this,"Cleared",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void selectPDF()
    {
        // Initialize intent
        Intent intent
                = new Intent(Intent.ACTION_GET_CONTENT);
        // set type
        intent.setType("text/plain");
        // Launch intent
        resultLauncher.launch(intent);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        // check condition
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            selectPDF();
        }
        else {
            // When permission is denied
            // Display toast
            Toast
                    .makeText(getApplicationContext(),
                            "Permission Denied",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
    byte[] getBytes(Context context, Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(context, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void downloadModal(String input) {
        // below line is use to download the modal which
        // we will require to translate in german language
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        // below line is use to download our modal.
        englishGermanTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // this method is called when modal is downloaded successfully.
                Toast.makeText(MainActivity4.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();

                // calling method to translate our entered text.
                translateLanguage(input);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity4.this, "Fail to download modal", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void translateLanguage(String input) {
        englishGermanTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                translateLanguageTV.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity4.this, "Fail to translate", Toast.LENGTH_SHORT).show();
            }
        });

    }
}