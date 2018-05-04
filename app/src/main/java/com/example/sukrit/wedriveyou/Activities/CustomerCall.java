package com.example.sukrit.wedriveyou.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sukrit.wedriveyou.R;
import com.example.sukrit.wedriveyou.Remote.IGoogleApi;
import com.example.sukrit.wedriveyou.Utils.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String distance,duration,address,riderId, riderPhone,riderName;
    public static final String TAG = "DHON";
    Button btnConfirmRide;
    DatabaseReference mOngoingRidesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mOngoingRidesDb = FirebaseDatabase.getInstance().getReference().child("OnGoingRides");

        txtTime = findViewById(R.id.txtTime);
        txtDistance = findViewById(R.id.txtDistance);
        txtAddress = findViewById(R.id.txtAddress);
        btnConfirmRide = findViewById(R.id.btnConfirmRide);

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

            btnConfirmRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: "+btnConfirmRide.getTag());
                    if(btnConfirmRide.getTag()!="call")
                    {
                        FirebaseDatabase.getInstance().getReference().child("OnGoingRides").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                riderId = dataSnapshot.child("riderId").getValue().toString();
                                Log.d(TAG, "onDataChange: riderId : "+riderId);
                                FirebaseDatabase.getInstance().getReference().child("Riders").child(riderId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        riderPhone = dataSnapshot.child("phone").getValue().toString();
                                        riderName = dataSnapshot.child("name").getValue().toString();
                                        Log.d(TAG, "onDataChange: phone: "+riderPhone+"\n name : "+riderName);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        handleConfirmRide();
                    }
                    else if(btnConfirmRide.getTag()=="call")
                    {
                        Log.d(TAG, "onClick: pp: "+riderPhone);
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + riderPhone));
                        if (ActivityCompat.checkSelfPermission(CustomerCall.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    }
                }
            });
    }

    private void handleConfirmRide() {
        mOngoingRidesDb.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("rideConfirmed").setValue(true);
        btnConfirmRide.setText("Call Customer");
        btnConfirmRide.setBackground(getResources().getDrawable(R.color.btnSignIn));
        btnConfirmRide.setTag("call");

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
