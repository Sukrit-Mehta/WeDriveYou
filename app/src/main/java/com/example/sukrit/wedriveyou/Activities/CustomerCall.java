package com.example.sukrit.wedriveyou.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sukrit.wedriveyou.R;
import com.example.sukrit.wedriveyou.Remote.IGoogleApi;
import com.example.sukrit.wedriveyou.Utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {

    TextView txtTime,txtDistance,txtAddress;
    MediaPlayer mediaPlayer;
    IGoogleApi iGoogleApiService;
    String remoteMessage;
    Double lat,lng;
    String distance,duration,address;
    public static final String TAG = "DHON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        txtTime = findViewById(R.id.txtTime);
        txtDistance = findViewById(R.id.txtDistance);
        txtAddress = findViewById(R.id.txtAddress);

        iGoogleApiService = Common.getGoogleAPI();

//        mediaPlayer = MediaPlayer.create(this,R.raw.audio);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.start();
        Log.d(TAG, "onCreate: ");

            remoteMessage = getIntent().getStringExtra("remoteMessage");
            //Distance:0.33164255539956095 , Time:1 min , Address:NH 24, Industrial Area, Sector 62, Noida, Uttar Pradesh 201014, India
            distance = remoteMessage.split(",")[0];
            duration = remoteMessage.split(",")[1];
            address = remoteMessage.substring(remoteMessage.indexOf("Address"));
            txtTime.setText(duration);
            txtAddress.setText(address);
            txtDistance.setText(distance);
            Log.d("DHON", "onCreate: remote: "+distance+"\n"+duration+"\n"+address);
            /*
            double lat = getIntent().getDoubleExtra("lat",0.0);
            double lng = getIntent().getDoubleExtra("lng",0.0);*/
           // getDirection(lat,lng);

    }

    private void getDirection(Double lat, Double lng) {
        String requestApi = null;
        try {
            requestApi = "https://maps.google.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less_driving&"+
                    "origin="+Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "keys="+getResources().getString(R.string.google_directions_api);

            iGoogleApiService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                Log.d("TAG", "onResponse: "+response.body().toString());
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray routes = jsonObject.getJSONArray("routes");
                                JSONObject routesObject = routes.getJSONObject(0);
                                JSONArray legs = routesObject.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);
                                txtTime.setText(legsObject.getString("time"));
                                txtAddress.setText(legsObject.getString("end_address"));
                                txtDistance.setText(legsObject.getString("distance"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
/*
    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();
    }*/
}
