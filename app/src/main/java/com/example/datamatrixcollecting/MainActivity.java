package com.example.datamatrixcollecting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;

public class MainActivity extends AppCompatActivity {

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    private Button btnCheckDM;
    private Button btnNewCollecting;
    private Button btnLoadCollecting;
    private Button btnSettings;
    private Button btnExit;

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AidcManager.create(this, new AidcManager.CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
            }
        });

        ActivitySetting();
    }

    public void ActivitySetting() {
        btnCheckDM = (Button) findViewById(R.id.buttonCheckDatamatrix);
        btnCheckDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(MainActivity.this, CheckDatamatrixActivity.class);
                startActivity(barcodeIntent);
            }
        });

        btnNewCollecting = (Button) findViewById(R.id.buttonNewCollecting);
        btnNewCollecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(MainActivity.this, NewCollectingActivity.class);
                startActivity(barcodeIntent);
            }
        });

        btnLoadCollecting = (Button) findViewById(R.id.buttonLoadCollecting);
        btnLoadCollecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(MainActivity.this, LoadCollectingActivity.class);
                startActivity(barcodeIntent);
            }
        });

        btnSettings = (Button) findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(barcodeIntent);
            }
        });

        btnExit = (Button) findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}
