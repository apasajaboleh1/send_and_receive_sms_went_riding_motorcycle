package com.kirimsms.yuwono.freddy.mengirim_sms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener {

    private double deltaX = 0.0, deltaY = 0.0, deltaZ = 0.0, lastX, lastY, lastZ;
    private TextView sumbux, sumbuy, sumbuz, result;
    private Button stop;
    private SensorManager sensorManager;
    private Sensor proxymity, accelerometer;
    private static double[][] data = new double[2000][5];
    private boolean knowprox=false;
    private ArrayList<String> test_data= new ArrayList<String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    public static double[][] getdata()
    {
        return data;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        initdata();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null)
        {
            proxymity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(this,proxymity,sensorManager.SENSOR_DELAY_NORMAL);

        }
        else
        {

        }

    }
    private void alert_data(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void initdata() {
        sumbux = (TextView) findViewById(R.id.sumbux);
        sumbuy = (TextView) findViewById(R.id.sumbuy);
        sumbuz = (TextView) findViewById(R.id.sumbyz);
        stop = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        File root = new File(Environment.getExternalStorageDirectory(), "/Notes/");
        if (!root.exists()) {
            alert_data("no folder in there");
            //System.exit(1);
        }
        File data_location = new File(root, "training.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(data_location));
            String temp;
            int counter = 0;
            while ((temp = br.readLine()) != null) {
                String[] temp2 = temp.split(",");
                for (int x = 0; x < temp2.length; x++)
                    data[counter][x] = Double.parseDouble(temp2[x]);
                counter++;
            }
            //try bug success
            //alert_data(String.valueOf(data[0][1]));
        } catch (IOException e) {
            alert_data("no file");
            e.getStackTrace();
        }
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proxymity, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    public void displayCleanValues() {
        sumbux.setText("0.0");
        sumbuy.setText("0.0");
        sumbuz.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        sumbux.setText(Double.toString(deltaX));
        sumbuy.setText(Double.toString(deltaY));
        sumbuz.setText(Double.toString(deltaZ));
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.accuracy==sensorManager.SENSOR_STATUS_UNRELIABLE)return;
        Sensor se=event.sensor;
        if(se.getType()==Sensor.TYPE_PROXIMITY)
        {
            float curlight=event.values[0];
            if(curlight==0){
                knowprox=true;
                //alert_data("deket");
            }
            else
            {
                //alert_data("jauh");
                knowprox=false;
            }
        }
        else if(se.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();
            // display the max x,y,z accelerometer values
            //displayMaxValues();

            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if(knowprox) {
                StringBuilder getdata = new StringBuilder();
                getdata.append(System.currentTimeMillis() + ",");
                getdata.append(deltaX + ",");
                getdata.append(deltaY + ",");
                getdata.append(deltaZ + "\n");
                test_data.add(getdata.toString());
                if(test_data.size()==10)
                {
                    //async
                }
            }

            // if the change is below 2, it is just plain noise

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
