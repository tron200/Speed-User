package com.speed.user.models;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestInterface {

    @POST("mpesa/stkpush/v1/processrequest")
    Call<PaymentResponse> createSocialLogin(@Header("X-Requested-With") String requestWith,
                                            @Header("Content-Type") String contentType,
                                            @Header("Authorization") String Authorization,
                                            @Body PaymentRequest paymentRequest);

    @GET("api/user/check/rate/provider")
    Call<GetUserRate> getUserRate(@Header("X-Requested-With") String requestWith,
                                  @Header("Authorization") String Authorization);

    @POST("api/user/rate/provider")
    Call<ResponseBody> postUserRate(@Header("X-Requested-With") String requestWith,
                                    @Header("Authorization") String Authorization,
                                    @Body PostUserRate postUserRate);


    @GET("api/user/request/check")
    Call<ResponseBody> addPickUpNotes(@Header("X-Requested-With") String requestWith,
                                      @Header("Authorization") String Authorization,
                                      @Query("special_note") String pickupNotes,
                                      @Query("request_id") String requestID);


    @POST("api/user/add/change/location")
    Call<ResponseBody> changeDestinationRequest(@Header("X-Requested-With") String requestWith,
                                                @Header("Authorization") String Authorization,
                                                @Body ChangeDestRequest changeDestRequest);

}
