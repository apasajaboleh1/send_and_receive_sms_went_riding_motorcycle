package com.kirimsms.yuwono.freddy.mengirim_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GE72 2QD on 10/17/2016.
 */
public class SMSReceiver extends BroadcastReceiver {
    // SmsManager class is responsible for all SMS related actions
    final SmsManager sms = SmsManager.getDefault();
    public static boolean statuspesan=false;
    private static ArrayList<String> phoneNum=new ArrayList<String>();
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // A PDU is a "protocol data unit". This is the industrial standard for SMS message
                statuspesan=true;
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    // This will create an SmsMessage object from the received pdu
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    // Get sender phone number
                    String phoneNumber = sms.getDisplayOriginatingAddress();
                    String sender = phoneNumber;
                    phoneNum.add(sender);
                    String message = sms.getDisplayMessageBody();
                    String formattedText = String.format(context.getResources().getString(R.string.sms_message), sender, message);
                    // Display the SMS message in a Toast
                    Toast.makeText(context, formattedText, Toast.LENGTH_LONG).show();
                    MainActivity inst = MainActivity.instance();

                    //inst.updateList(formattedText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<String> getph()
    {
        ArrayList<String> halo = new ArrayList<String>(phoneNum);
        delph();
        return halo;
    }
    public static void delph()
    {
        phoneNum.clear();
    }
}