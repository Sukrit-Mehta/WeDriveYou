package com.example.sukrit.wedriveyou.Firebase;

import android.content.Intent;
import android.util.Log;

import com.example.sukrit.wedriveyou.Activities.CustomerCall;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sukrit on 25/4/18.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
        Log.d("DHON", "onMessageReceived: "+remoteMessage.getNotification().getBody());
        Log.d("DHON", "onMessageReceived: "+remoteMessage.getFrom());
        Log.d("DHON", "onMessageReceived: "+remoteMessage.getTo());
        Log.d("DHON", "onMessageReceived: "+remoteMessage.getMessageId());

        Intent intent = new Intent(getBaseContext(),CustomerCall.class);
        intent.putExtra("remoteMessage",remoteMessage.getNotification().getBody());
        startActivity(intent);
    }
}
