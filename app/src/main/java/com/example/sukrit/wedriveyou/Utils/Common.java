package com.example.sukrit.wedriveyou.Utils;

import android.location.Location;

import com.example.sukrit.wedriveyou.Remote.FCMClient;
import com.example.sukrit.wedriveyou.Remote.IFCMService;
import com.example.sukrit.wedriveyou.Remote.IGoogleApi;
import com.example.sukrit.wedriveyou.Remote.RetrofitClient;

/**
 * Created by sukrit on 13/3/18.
 */

public class Common {

    public static final String token_tb1 = "Tokens";
    public static final String driver_tb2 = "Drivers";
    public static final String user_driver_tb2 = "DriversInformation";
    public static final String user_rider_tb2 = "RidersInformation";
    public static final String pickup_request_tb2 = "PickupRequest";

    public static Location mLastLocation=null;

    public static final String baseURL = "https://maps.google.com";
    public static final String fcmURL = "https://fcm.googleapis.com";
    public static IGoogleApi getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
