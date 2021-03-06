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
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity implements SensorEventListener {

    private double deltaX = 0.0, deltaY = 0.0, deltaZ = 0.0, lastX, lastY, lastZ;
    private TextView sumbux, sumbuy, sumbuz, result;
    private Button stop;
    private SensorManager sensorManager;
    private Sensor proxymity, accelerometer;
    private static double[][] data = new double[2000][5];
    private boolean knowprox=false;
    private ArrayList<String> test_data= new ArrayList<String>();
    private SmsMessage[] msgs = null;
    private static MainActivity activity;
    // jumlah data buat rata - ratanya gmn
    private final static int jumlah_data=20;
    private static int supercounter=0;
    ArrayList<String> tel= new ArrayList<String>();
    //private boolean statusmsg=false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    public static MainActivity instance() {
        return activity;
    }
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
        //SmsListener sl = new SmsListener();
        //sl.onReceive();
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
    public void onStart() {
        super.onStart();
        activity = this;
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

                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getfiledata();
                    //Toast.makeText(MainActivity.this, "ok1", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }

                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage / read SMS", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class KNearstNeighbour extends AsyncTask<ArrayList<String>,Void,Integer>{
        protected void sendingsms(String Pesan)
        {
            //alert_data(Pesan);
            ArrayList<String> temp=new ArrayList<String>(SmsReceiver.getph());
            for(int x=0;x<temp.size();x++)
            {
                String ph=temp.get(x);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph, null, Pesan, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Integer doInBackground(ArrayList<String>... params) {
            ArrayList<String> training_data=new ArrayList<String>();
            double[] get_training_data= new double[5];
            int[] mau_final= new int[10];
            ArrayList<Pair<Double,Double>> res_sementara = new ArrayList<Pair<Double,Double>>();
            StringBuilder sb= new StringBuilder();
            ArrayList<String> temp12=params[0];



            //for(int x=0;x<temp12.size();x++)
            //{
                String[] temp123=temp12.get(0).split(",");
                for(int y=0;y<temp123.length;y++)
                {
                    get_training_data[y]=Double.parseDouble(temp123[y]);
                    //sb.append(get_training_data[y]); //-> ws oke
                }

                double[] perhitungan_knn=new double[2000];
                for(int y=0;y<supercounter;y++)
                {
                    double tempsaja=0.0;
                    for(int z=1;z<data[y].length-1;z++)
                    {
                        tempsaja+=Math.pow(data[y][z]-get_training_data[z],2);
                    }
                    res_sementara.add(new Pair(Math.sqrt(tempsaja),data[y][data[y].length-1]));
                    //sb.append(Math.sqrt(tempsaja)+" "+data[y][data[y].length-1]+"\n");//-> ws oke
                }
                Collections.sort(res_sementara, new Comparator<Pair<Double, Double>>() {
                    @Override
                    public int compare(Pair<Double, Double> lhs, Pair<Double, Double> rhs) {
                        return lhs.first < rhs.first ? -1 : lhs.first == rhs.first ? 0 : 1;
                    }
                });
                //misalnya k =3
                int[] simpan = new int[7];
                Arrays.fill(simpan,0);
                // ini bisa d ganti ganti .......
                for(int y=0;y<10;y++)
                {
                    simpan[ res_sementara.get(y).second.intValue()]++;
                    //sb.append(simpan[ res_sementara.get(y).second.intValue()]+" "+res_sementara.get(y).second.intValue());
                }
                /*for(int y=0;y<50;y++)
                {
                    sb.append(res_sementara.get(y).first+" "+res_sementara.get(y).second+"\n");
                }
                sb.append("\n\n sempak goreng\n");*/
                int max=0,res=0;
                for(int y=0;y<7;y++)
                {
                    if(simpan[y]>max)
                    {
                        max=simpan[y];
                        res=y;
                    }
                }

                return res;
                //return sb.toString();
                //alert_data(Integer.toString(res));
                //sb.append(mau_final[x]+"\n");//-> bener

            /*int[] temp_for_last_step= new int[20];
            Arrays.fill(temp_for_last_step,0);
            for(int x=0;x<mau_final.length;x++)
            {
                temp_for_last_step[mau_final[x]]++;
                //sb.append(temp_for_last_step[mau_final[x]]+" "+ mau_final[x]+" ");
            }
            sb.append("\n");
            /*for(int x=0;x<7;x++)
                sb.append(temp_for_last_step[x]+"\n");
            int max=0,res=0;
            for(int x=0;x<7;x++)
                if (temp_for_last_step[x]>max)
                {
                    max=temp_for_last_step[x];
                    res=x;
                    //sb.append(res+" "+temp_for_last_step[x]+" ");
                }
            //sb.append(res);*/

            //return hasil_yang_didapat;
            //return sb.toString();
        }

        protected void onPostExecute(Integer result)
        {
            //result =1 or 2 or 3 or 4 or 5 we dont send any of sms because our constraint that we want to send sms went the message arrive and we drive bike
            if(result==1) {
                alert_data("jalan celana");
                sendingsms("kemungkinan besar adalah jalan dengan device pada saku celana");
            }
            else if(result==2){
                alert_data("saku celana");
                sendingsms("kemungkinan besar adalah lari dengan device pada saku celama");
            }
            else if (result==3) {
                alert_data("saku kemeja");
                sendingsms("kemungkinan besar adalah jalan dengan device pada saku kemeja");
            }
            else if(result==4) {
                alert_data("lari kemeja");
                sendingsms("kemungkinan besar adalah lari dengan device pada saku kemeja");
            }
            else if (result==5) {
                alert_data("duduk");
                sendingsms("kemungkinan besar adalah duduk");
            }
            else if (result==6){
                alert_data("motor");
                sendingsms("kemungkinan besar adalah naik sepeda motor jadi smsnya nanti akan di balas");
            }
            else
            {
                alert_data(Integer.toString(result));
            }
        }
        protected void onPostExecute(String result)
        {
            alert_data(result);
        }
    }
    public void getfiledata()
    {
        File root = new File(Environment.getExternalStorageDirectory(), "/Notes/");
        if (!root.exists()) {
            alert_data("no folder in there");
            //System.exit(1);
        }
        File data_location = new File(root, "training.csv");
        try {
            StringBuilder sb= new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(data_location));
            String temp;
            int counter = 0,counter1=0;
            double x=0.0,y=0.0,z=0.0,time=0.0,clas=0.0;
            while ((temp = br.readLine()) != null) {
                String[] temp2 = temp.split(",");
                /*for (int x = 0; x < temp2.length; x++) {
                    if(x==3)data[counter][x] = Math.abs( Double.parseDouble(temp2[x])-9.0);
                    else
                        data[counter][x] = Double.parseDouble(temp2[x]);
                }*/
                x+=Double.parseDouble(temp2[1]);
                y+=Double.parseDouble(temp2[2]);
                z+=Math.abs( Double.parseDouble(temp2[3])-9.0);
                time+=Double.parseDouble(temp2[0]);
                clas+=Double.parseDouble(temp2[4]);
                //if(data[counter][1]>=2.0&&data[counter][2]>=2.0&&data[counter][3]>=2.0) {
                counter1++;
                if(counter1==jumlah_data)
                {
                    x/=jumlah_data;y/=jumlah_data;z/=jumlah_data;time/=jumlah_data;clas/=jumlah_data;counter1=0;
                    data[counter][0]=time;data[counter][1]=x;data[counter][2]=y;data[counter][3]=z;data[counter][4]=Math.floor(clas);
                    counter++;
                    sb.append(data[counter-1][1]+" "+data[counter-1][2]+" "+data[counter-1][3]+"\n");
                    x=0.0;y=0.0;z=0.0;clas=0.0;

                }
                //}

            }
            if(counter1!=0)
            {
                x/=counter1;y/=counter1;z/=counter1;time/=counter1;
                data[counter][0]=time;data[counter][1]=x;data[counter][2]=y;data[counter][3]=z;
                sb.append(x+" "+y+" "+z+"\n");
                counter++;
            }
            supercounter=counter;
            //try bug success
            alert_data(String.valueOf(sb));
        } catch (IOException e) {
            alert_data("no file");
            e.getStackTrace();
        }
    }

    public void initdata() {
        sumbux = (TextView) findViewById(R.id.sumbux);
        sumbuy = (TextView) findViewById(R.id.sumbuy);
        sumbuz = (TextView) findViewById(R.id.sumbyz);
        stop = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);

        boolean hasreadexternalstorage=(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean hasreadsms=(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);
        String[] permit={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_SMS};
        if(!hasreadexternalstorage||!hasreadsms)
            ActivityCompat.requestPermissions(MainActivity.this, permit,
                    1);
        else if (hasreadexternalstorage||hasreadsms)getfiledata();

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
        result.setText("0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues(int a) {
        sumbux.setText(Double.toString(deltaX));
        sumbuy.setText(Double.toString(deltaY));
        sumbuz.setText(Double.toString(deltaZ));

        result.setText(Integer.toString(a));
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
            deltaZ-=9.0;
            deltaZ=Math.abs(deltaZ);
            double x=0.0,y=0.0,z=0.0;
            //int counter12=0;

            if(knowprox&&SmsReceiver.statuspesan) {
                //if(deltaX>0&&deltaY>0&&deltaZ>0) {
                x+=deltaX;y+=deltaY;z+=deltaZ;
                StringBuilder sb= new StringBuilder();
                sb.append(x+y+z);
                tel.add(sb.toString());
                displayCurrentValues(tel.size());
                //if (test_data.size() == 10) {
                    //async
                    //debug
                    /*StringBuilder sb=new StringBuilder();
                    for(int x=0;x<test_data.size();x++)
                        sb.append(test_data.get(x));
                    alert_data(sb.toString());*/
                    if ( tel.size()== jumlah_data) {
                        alert_data("benar");
                        x/=jumlah_data;y/=jumlah_data;z/=jumlah_data;
                        StringBuilder getdata = new StringBuilder();
                        getdata.append(System.currentTimeMillis() + ",");
                        getdata.append( x+ ",");
                        getdata.append(y + ",");
                        getdata.append(z + "\n");
                        test_data.add(getdata.toString());
                        x=y=z=0.0;
                        ArrayList<String> tempsaja = new ArrayList<String>(test_data);
                        KNearstNeighbour dn = new KNearstNeighbour();
                        dn.execute(tempsaja);
                        SmsReceiver.statuspesan = false;
                        test_data.clear();
                        tel.clear();
                    }

                //}
                //}
            }

            // if the change is below 2, it is just plain noise

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
