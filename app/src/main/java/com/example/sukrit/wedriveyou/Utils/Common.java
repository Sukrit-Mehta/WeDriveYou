package com.example.sukrit.wedriveyou.Utils;

import com.example.sukrit.wedriveyou.Remote.IGoogleApi;
import com.example.sukrit.wedriveyou.Remote.RetrofitClient;

/**
 * Created by sukrit on 13/3/18.
 */

public class Common {

    public static final String driver_tb2 = "Drivers";
    public static final String user_driver_tb2 = "DriversInformation";
    public static final String user_rider_tb2 = "RidersInformation";
    public static final String pickup_request_tb2 = "PickupRequest";

    public static final String baseURL = "https://maps.google.com";
    public static IGoogleApi getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }
}
