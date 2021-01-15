package com.codycaughlan.smsbutler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.content.ContentResolver;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {
    private static final String TAG = "IncomingSms";
    
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(Constants.PREFS_TITLE, Context.MODE_PRIVATE);
        
        boolean enabled = preferences.getBoolean(Constants.PREFS_ENABLED, false);
        if(!enabled) {
            Log.i(TAG, "SMS Butler: not enabled.");
            return;
        } else {
            Log.i(TAG, "SMS Butler: Enabled. At your bidding!");
        }

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;

                    int offset = Integer.parseInt(preferences.getString(Constants.PREFS_OFFSET, "15"));
                    if (recentMessage(context.getApplicationContext(), senderNum, offset)) {
                        return;
                    }

                    // This should be made an optional setting in the app, but for my purposes, I want it by default
                    if (preferences.getBoolean(Constants.PREFS_CONTACTS_ONLY, true) && ! contactExists(context.getApplicationContext(), senderNum)) {
                        return;
                    }

                    String message = currentMessage.getDisplayMessageBody();

                    Log.i(TAG, "senderNum: " + senderNum + "; message: " + message);

                    String autoReplyMessage = preferences.getString(Constants.PREFS_AUTO_REPLY_KEY, "");
                    if (autoReplyMessage != null && autoReplyMessage.trim().length() > 0) {
                        Log.i(TAG, "sending back: " + autoReplyMessage);
                        sms.sendTextMessage(phoneNumber, null, autoReplyMessage, null, null);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception smsReceiver: " + e.toString());
        }
        
    }

    // https://stackoverflow.com/a/9965978
    public boolean contactExists(Context _activity, String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
            Cursor cur = _activity.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
            return false;
        }
        else {
            return false;
        }
    }

    // https://stackoverflow.com/a/9494532
    public static final boolean recentMessage(Context _activity, String number, int offset) {
        long inboxMsgDate = 0;
        long sentMsgDate = 0;
        Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // Get most recent received msg timestamp
        Cursor cursor = _activity.getContentResolver().query(lookupUri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst() && cursor.moveToNext()) { // must check the result to prevent exception
            inboxMsgDate = Long.parseLong(cursor.getString(cursor.getColumnIndex("date")));
            String inboxMsgBody = cursor.getString(cursor.getColumnIndex("body"));
            String inboxMsgId = cursor.getString(cursor.getColumnIndex("_id"));
        }

        // Get most recent sent msg timestamp
        cursor = _activity.getContentResolver().query(lookupUri.parse("content://sms/sent"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            sentMsgDate = Long.parseLong(cursor.getString(cursor.getColumnIndex("date")));
        }

        offset = offset * 60 * 1000;
        long epoch = System.currentTimeMillis();

        if (epoch - sentMsgDate > offset && epoch - inboxMsgDate > offset) {
            return false;
        }
        return true;
    }
}