package com.kirimsms.yuwono.freddy.mengirim_sms;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by freddy on 10/4/2016.
 */
public class KNearstNeighbour extends AsyncTask<ArrayList<String>,Void,Integer> {

    @Override
    protected Integer doInBackground(ArrayList<String>... params) {
        ArrayList<String> training_data=new ArrayList<String>();
        double[][] get_training_data= new double[20][5];
        for(int x=0;x<params.length;x++)
        {
            String[] temp=params[0].get(x).split(",");
            for(int y=0;y<temp.length;y++)
            {
                get_training_data[x][y]=Double.valueOf(temp[y]);
            }
        }
        return null;
    }
    protected void onPostExecute(Integer result) {
        
    }
}
