package com.example.datamatrixcollecting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.os.Environment;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewCollectingActivity extends AppCompatActivity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {

    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;

    ArrayList<String> listCodes=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collecting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        barcodeReader = MainActivity.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, false);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Apply the settings
            barcodeReader.setProperties(properties);
        }

        // get initial list
        barcodeList = (ListView) findViewById(R.id.listViewCodes);
        adapter = new ArrayAdapter<String>(
                NewCollectingActivity.this, android.R.layout.simple_list_item_1, listCodes);

        final Button button = findViewById(R.id.buttonSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String textToSave = "";

                for(int i = 0; i < listCodes.size(); i++){
                    textToSave += listCodes.get(i) + "\n";
                }

                Long timeStamp = System.currentTimeMillis();

                String fileName = timeStamp.toString() + ".txt";

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

                try {
                    FileOutputStream f = new FileOutputStream(file);
                    PrintWriter pw = new PrintWriter(f);
                    pw.println(textToSave);
                    pw.flush();
                    pw.close();
                    f.close();

                    Toast.makeText(v.getContext(), "Файл " + fileName + " сохранен!", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {

                    Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                } catch (IOException e) {

                    Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

            }
        });

        final ImageButton button2 = findViewById(R.id.imageButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listCodes.size() > 0) {
                    listCodes.remove(listCodes.size() - 1);
                    adapter.notifyDataSetChanged();
                    button.setText("Сохранить (" + listCodes.size() + ")");
                }
            }
        });

    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                String RawData = event.getBarcodeData();

                MarkingCode Code;

                try {
                    Code = new MarkingCode(RawData);
                    listCodes.add(Code.getUniqueCode());
                    adapter.notifyDataSetChanged();

                    Button button = findViewById(R.id.buttonSave);
                    button.setText("Сохранить (" + listCodes.size() + ")");
                    // list.add("Barcode data: " + event.getBarcodeData());
                    //list.add("Character Set: " + event.getCharset());
                    //list.add("Code ID: " + event.getCodeId());
                    //list.add("AIM ID: " + event.getAimId());
                    //list.add("Timestamp: " + event.getTimestamp());

                } catch (Exception e) {

                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, "Некорректный код маркировки!", duration);
                    toast.show();
                }


                barcodeList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onFailureEvent(final BarcodeFailureEvent event) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(NewCollectingActivity.this, "Нет данных!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onTriggerEvent(final TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            barcodeReader.aim(event.getState());
            barcodeReader.light(event.getState());
            barcodeReader.decode(event.getState());

        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
        }
    }
}
