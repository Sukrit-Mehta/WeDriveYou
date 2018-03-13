package com.example.sukrit.wedriveyou.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by sukrit on 13/3/18.
 */

public interface IGoogleApi {
    @GET
    Call<String> getPath(@Url String url);
}
